/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.DetailView.RequestRenderCallback;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.NodeStyle;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class RenderTree {
	private boolean collapseOverlaps = true;
	private boolean drawLabels = true;
	private static Logger rootLogger = Logger.getLogger("");
	
	private INodeStyle highlightStyle = new NodeStyle("#FFFF00", Defaults.POINT_COLOR, 2.0, 
			"#FFFF00", Defaults.LINE_COLOR, 2.0, 
			"#FFFF00", Defaults.TRIANGLE_FILL_COLOR, 1.0, 
			Defaults.TEXT_COLOR, Defaults.TEXT_COLOR, 0.0);
	
	private INodeStyle defaultStyle = new NodeStyle(Defaults.POINT_COLOR, Defaults.POINT_COLOR, 1.0, 
			Defaults.LINE_COLOR, Defaults.LINE_COLOR, 1.0, 
			Defaults.TRIANGLE_OUTLINE_COLOR, Defaults.TRIANGLE_FILL_COLOR, 1.0, 
			Defaults.TEXT_COLOR, Defaults.TEXT_COLOR, 0.0);
	
	private HashSet<Integer> highlights = new HashSet<Integer>();

	public void renderTree(ITree tree, ILayout layout, IGraphics graphics, Camera camera, RequestRenderCallback renderCallback) {
		if ( tree == null || graphics == null || layout == null)
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix());
		}
		
		graphics.clear();
		
		this.renderNode(root, layout, graphics, renderCallback);
	}
	
	protected void renderNode(INode node, ILayout layout, IGraphics graphics, final RequestRenderCallback renderCallback) {

		if ( graphics.isCulled(layout.getBoundingBox(node))) {
			return;
		}
		
		if (drawLabels && node.isLeaf()) {
			drawLabel(node, layout, graphics);
		} else if (collapseOverlaps && !canDrawChildLabels(node, layout, graphics)) {
			renderPlaceholder(node, layout, graphics);
		} else if (!checkForData(node,layout,renderCallback)) {
			//while checkForData gets children and layouts (async), render a subtree placeholder
			renderPlaceholder(node, layout, graphics);			
		} else {
			renderChildren(node, layout, graphics, renderCallback);
		}
		
		setStyle(node, graphics, Element.NODE);
		graphics.drawPoint(layout.getPosition(node));
	}

	protected abstract void drawLabel(INode node, ILayout layout, IGraphics graphics);
	protected abstract boolean canDrawChildLabels(INode node, ILayout layout, IGraphics graphics);
	protected abstract void renderChildren(INode node, ILayout layout, IGraphics graphics, RequestRenderCallback renderCallback);
	protected abstract void renderPlaceholder(INode node, ILayout layout, IGraphics graphics);

	protected void setStyle(INode node, IGraphics graphics, Element element) {
		INodeStyle style = null;
		
		if (isHighlighted(node))
		{
			style = highlightStyle;
		}
		else if (node.getStyle() != null)
		{
			style = node.getStyle();
		} 
		else
		{
			style = defaultStyle;
		}
		
		graphics.setStyle(style.getElementStyle(element));
	}

	public void setCollapseOverlaps(boolean collapseOverlaps) {
		this.collapseOverlaps = collapseOverlaps;
	}
	
	public void setDrawLabels(boolean drawLabels) {
		this.drawLabels = drawLabels;
	}
	
	public void setDefaultStyle(INodeStyle style)
	{
		defaultStyle = style;
	}
	
	public void setHighlightStyle(INodeStyle style)
	{
		highlightStyle = style;
	}
	
	public void highlight(INode node)
	{
		highlights.add(node.getId());
	}
	
	public void clearHighlights()
	{
		highlights.clear();
	}
	
	private boolean isHighlighted(INode node)
	{
		return highlights.contains(node.getId());
	}
	
	private static boolean checkForData(final INode node, final ILayout layout, final RequestRenderCallback renderCallback ) 
	{
		if (node instanceof RemoteNode && layout instanceof RemoteLayout) 
		{
			final RemoteLayout rLayout = (RemoteLayout) layout;
			final RemoteNode rNode = (RemoteNode) node;
		
			if (rNode.getChildren() == null) {
				
				rNode.getChildrenAsync(new AsyncCallback<RemoteNode[]>()
				{
					@Override
					public void onSuccess(RemoteNode[] arg0)
					{
						if (renderCallback != null) {
							//do another check to make sure layouts have been fetched before requesting render
							checkForData(rNode, rLayout, renderCallback);
						}
					}
					
					@Override
					public void onFailure(Throwable arg0)
					{
						// TODO Auto-generated method stub
					}
				});
	
				return false;
				
			} else if (!rLayout.containsNodes(rNode.getChildren())) {
				
				rLayout.getLayoutAsync(rNode.getChildren(), rLayout.new GotLayouts() {
					@Override
					protected void gotLayouts(LayoutResponse[] responses) {
						if (renderCallback != null) {
							rootLogger.log(Level.INFO, "Got layouts for children of node \"" + rNode + "\".");
							renderCallback.requestRender();
						}
					}
				});
				
				return false;
			}
		}
		
		return true;
	}
	
	protected String getLabel(INode node)
	{
		//TODO give user options on how to label internal nodes without modifying the INode itself
		return node.getLabel();
	}

}
