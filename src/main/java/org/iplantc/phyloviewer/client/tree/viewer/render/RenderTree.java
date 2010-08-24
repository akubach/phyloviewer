/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.TreeWidget;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayout;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutServiceAsync;
import org.iplantc.phyloviewer.client.tree.viewer.layout.remote.RemoteLayoutService.LayoutResponse;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNode;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeService;
import org.iplantc.phyloviewer.client.tree.viewer.model.remote.RemoteNodeServiceAsync;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class RenderTree {
	private static final RemoteNodeServiceAsync nodeService = GWT.create(RemoteNodeService.class);
	private static final RemoteLayoutServiceAsync layoutService = GWT.create(RemoteLayoutService.class);

	public void renderTree(ITree tree, ILayout layout, IGraphics graphics, Camera camera) {
		if ( tree == null || graphics == null || layout == null)
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix());
		}
		
		graphics.clear();
		
		this.renderNode(root, layout, graphics, camera);
	}
	
	protected void renderNode(INode node, ILayout layout, IGraphics graphics, Camera camera) {

		if ( graphics.isCulled(layout.getBoundingBox(node))) {
			return;
		}
		
		if (node.isLeaf()) {
			drawLabel(node, layout, graphics);
		} else if (node instanceof RemoteNode && layout instanceof RemoteLayout && node.getNumberOfChildren() == RemoteNode.CHILDREN_NOT_FETCHED) {
			//TODO do a (async) RPC fetch and request another render() in the callback
			fetchChildren(((RemoteNode) node), (RemoteLayout) layout);
			renderPlaceholder(node, layout, graphics);
		} else if (!canDrawChildLabels(node, layout, camera)) {
			renderPlaceholder(node, layout, graphics);
		} else {
			renderChildren(node, layout, graphics, camera);
		}
		
		setStyle(node, graphics, Element.NODE);
		graphics.drawPoint(layout.getPosition(node));
	}

	protected abstract void drawLabel(INode node, ILayout layout, IGraphics graphics);
	protected abstract boolean canDrawChildLabels(INode node, ILayout layout, Camera camera);
	protected abstract void renderChildren(INode node, ILayout layout, IGraphics graphics, Camera camera);
	protected abstract void renderPlaceholder(INode node, ILayout layout, IGraphics graphics);

	protected static void setStyle(INode node, IGraphics graphics, Element element) {
		IElementStyle style = node.getStyle().getElementStyle(element);
		graphics.setStyle(style);
	}
	
	private void fetchChildren(final RemoteNode node, final RemoteLayout layout) {
		//TODO would be faster (and cleaner code here) if the children and the layout came from one RPC call

		nodeService.getChildren(node.getUUID(), new AsyncCallback<RemoteNode[]>() {
			
			@Override
			public void onSuccess(final RemoteNode[] children) {
				System.out.println("Received children of " + node.getUUID() + ". Getting layouts...");
				layoutService.getLayout(node, layout.getLayoutID(), new AsyncCallback<LayoutResponse>() {

					@Override
					public void onSuccess(LayoutResponse response) {
						System.out.println("Received layouts for children of " + node.getUUID());
						layout.setPosition(node, response.position);
						layout.setBoundingBox(node, response.boundingBox);
						node.setChildren(children);
						TreeWidget.instance.requestRender();
					}
					
					@Override
					public void onFailure(Throwable thrown) {
						//TODO
						thrown.printStackTrace();
					}
				});
				
			}

			@Override public void onFailure(Throwable thrown) { 
				//TODO
				thrown.printStackTrace();
			}
		});
	}
	
}
