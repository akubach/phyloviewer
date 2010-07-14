/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;


public class RenderTree {

	public static void renderTree(ITree tree, ILayout layout, IGraphics graphics, Camera camera) {
		if ( tree == null || graphics == null || layout == null)
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix());
		}
		
		graphics.clear();
		
		_renderNode(root, layout, graphics, camera);
	}
	
	private static void _renderNode(INode node, ILayout layout, IGraphics graphics, Camera camera) {
		
		if ( graphics.isCulled(layout.getBoundingBox(node)))
			return;
		
		graphics.setStrokeStyle(Defaults.POINT_COLOR);
		graphics.setFillStyle(Defaults.POINT_COLOR);
		graphics.drawPoint(layout.getPosition(node));
		
		if (node.isLeaf()) {
			graphics.setStrokeStyle(Defaults.TEXT_COLOR);
			graphics.setFillStyle(Defaults.TEXT_COLOR);
			graphics.drawText(new Vector2(layout.getPosition(node).getX(),layout.getPosition(node).getY()), node.getLabel());
		}
		
		Box2D boundingBox = layout.getBoundingBox(node);
		
		// If the current clade won't fit on the screen, draw a triangle.
		if ( _estimateNumberOfPixelsNeeded(node) > _getHeightOfBoundingBoxInPixels(boundingBox, camera)) {
			Vector2 min = boundingBox.getMin();
			Vector2 max = boundingBox.getMax();
			
			graphics.setStrokeStyle(Defaults.TRIANGLE_OUTLINE_COLOR);
			graphics.setFillStyle(Defaults.TRIANGLE_FILL_COLOR);
			graphics.drawTriangle(layout.getPosition(node),max.getX(),min.getY(),max.getY());
			
			// Find a label to use, if the node doesn't have one.
			if ( node.getLabel() == null || node.getLabel().equals("") ) {
				node.setLabel(node.findLabelOfFirstLeafNode());
			}
			
			// Draw the label.
			graphics.setStrokeStyle(Defaults.TEXT_COLOR);
			graphics.setFillStyle(Defaults.TEXT_COLOR);
			graphics.drawText(new Vector2(max.getX(),(min.getY()+max.getY())/2.0), node.getLabel());
		}
		else {
			int numChildren = node.getNumberOfChildren();
			for ( int i = 0; i < numChildren; ++i ) {
				graphics.setFillStyle(Defaults.LINE_COLOR);
				graphics.drawRightAngle(layout.getPosition(node), layout.getPosition(node.getChild(i)));
				
				_renderNode(node.getChild(i),layout,graphics,camera);
			}
		}
	}
	
	private static double _estimateNumberOfPixelsNeeded(INode node) {
		int numberOfLeafNodes = node.getNumberOfChildren();
		int pixelsPerTaxon = 15;
		return numberOfLeafNodes * pixelsPerTaxon;
	}
	
	private static double _getHeightOfBoundingBoxInPixels(Box2D box, Camera camera) {
		Box2D displayedBox = camera.getMatrix().transform(box);
		return displayedBox.getMax().getY() - displayedBox.getMin().getY();
	}
}
