/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

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
	
	protected abstract void renderNode(INode node, ILayout layout, IGraphics graphics, Camera camera);
	
	protected static double _estimateNumberOfPixelsNeeded(INode node) {
		int numberOfLeafNodes = node.getNumberOfChildren();
		int pixelsPerTaxon = 15;
		return numberOfLeafNodes * pixelsPerTaxon;
	}
	
	protected static double _getHeightOfBoundingBoxInPixels(Box2D box, Camera camera) {
		Box2D displayedBox = camera.getMatrix().transform(box);
		return displayedBox.getMax().getY() - displayedBox.getMin().getY();
	}
}
