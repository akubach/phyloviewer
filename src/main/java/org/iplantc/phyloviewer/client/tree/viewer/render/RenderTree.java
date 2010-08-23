/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.IElementStyle;

public abstract class RenderTree {

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
}
