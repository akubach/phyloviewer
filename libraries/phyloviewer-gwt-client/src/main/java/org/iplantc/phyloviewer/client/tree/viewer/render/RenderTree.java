/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.iplantc.phyloviewer.client.services.CombinedService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.DetailView.RequestRenderCallback;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class RenderTree {
	private static Logger rootLogger = Logger.getLogger("");
	private RenderPreferences renderPreferences = new RenderPreferences();
	IDocument document;
	
	public RenderTree() {
	}
	
	public RenderTree(IDocument document) {
		this.document = document;
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void renderTree(ILayout layout, IGraphics graphics, Camera camera, RequestRenderCallback renderCallback) {
		ITree tree = document != null ? document.getTree() : null;
		if ( document == null || tree == null || graphics == null || layout == null)
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
	
	public RenderPreferences getRenderPreferences()
	{
		return renderPreferences;
	}
	
	public void setRenderPreferences(RenderPreferences preferences)
	{
		renderPreferences = preferences;
	}
	
	protected void renderNode(INode node, ILayout layout, IGraphics graphics, final RequestRenderCallback renderCallback) {

		if ( graphics.isCulled(layout.getBoundingBox(node))) {
			return;
		}
		
		if (renderPreferences.drawLabels() && node.isLeaf()) {
			drawLabel(node, layout, graphics);
		} else if (renderPreferences.collapseOverlaps() && !canDrawChildLabels(node, layout, graphics)) {
			renderPlaceholder(node, layout, graphics);
		} else if (!checkForData(node,layout,renderCallback)) {
			//while checkForData gets children and layouts (async), render a subtree placeholder
			renderPlaceholder(node, layout, graphics);			
		} else {
			renderChildren(node, layout, graphics, renderCallback);
		}
		
		graphics.setStyle(this.getStyle(node).getNodeStyle());
		graphics.drawPoint(layout.getPosition(node)); 
	}

	protected abstract void drawLabel(INode node, ILayout layout, IGraphics graphics);
	protected abstract boolean canDrawChildLabels(INode node, ILayout layout, IGraphics graphics);
	protected abstract void renderChildren(INode node, ILayout layout, IGraphics graphics, RequestRenderCallback renderCallback);
	protected abstract void renderPlaceholder(INode node, ILayout layout, IGraphics graphics);

	/**
	 * Styling is done in layers: default style, node style, user style, highlight style
	 */
	protected IStyle getStyle(INode node) {
		
		IStyle highlightStyle = renderPreferences.getHighlightStyle();
		if(renderPreferences.isHighlighted(node) && highlightStyle != null) {
			return highlightStyle;
		}
		
		assert(document!=null);
		return document.getStyle(node);
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
				rootLogger.log(Level.INFO, "RenderTree.checkForData(): Fetching layouts for children of node \"" + rNode + "\".");
				rLayout.getLayoutAsync(rNode.getChildren(), rLayout.new GotLayouts() {
					@Override
					protected void gotLayouts(LayoutResponse[] responses) {
						if (renderCallback != null) {
							rootLogger.log(Level.INFO, "Rendering: got layouts for children of node \"" + rNode + "\".");
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
