package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;


public class RenderTree {

	public static void renderTree(ITree tree, IGraphics graphics, Camera camera) {
		if ( tree == null || graphics == null )
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix());
		}
		
		graphics.clear();
		
		_renderNode(root,graphics,camera);
	}
	
	private static void _renderNode(INode node, IGraphics graphics, Camera camera) {
		
		if ( graphics.isCulled(node.getBoundingBox()))
			return;
		
		graphics.drawPoint(node.getPosition());
		
		if (node.isLeaf()) {
			graphics.drawText(new Vector2(node.getPosition().getX(),node.getPosition().getY()), node.getLabel());
		}
		
		Box2D boundingBox = node.getBoundingBox();
		
		// If the current clade won't fit on the screen, draw a triangle.
		if ( _estimateNumberOfPixelsNeeded(node) > _getHeightOfBoundingBoxInPixels(boundingBox, camera)) {
			Vector2 min = boundingBox.getMin();
			Vector2 max = boundingBox.getMax();
			
			graphics.drawTriangle(node.getPosition(),max.getX(),min.getY(),max.getY());
			
			// Find a label to use, if the node doesn't have one.
			if ( node.getLabel() == null || node.getLabel().equals("") ) {
				node.setLabel(node.findLabelOfFirstLeafNode());
			}
			
			// Draw the label.
			graphics.drawText(new Vector2(max.getX(),(min.getY()+max.getY())/2.0), node.getLabel());
		}
		else {
			int numChildren = node.getNumberOfChildren();
			for ( int i = 0; i < numChildren; ++i ) {
				graphics.drawLine(node.getPosition(), node.getChild(i).getPosition());
				
				_renderNode(node.getChild(i),graphics,camera);
			}
		}
	}
	
	private static double _estimateNumberOfPixelsNeeded(INode node) {
		int numberOfLeafNodes = node.getNumberOfChildren();
		int pixelsPerTaxon = 15;
		return numberOfLeafNodes * pixelsPerTaxon;
	}
	
	private static double _getHeightOfBoundingBoxInPixels(Box2D box, Camera camera) {
		Vector2 min = camera.getMatrix().transform(box.getMin());
		Vector2 max = camera.getMatrix().transform(box.getMax());
		double height = max.getY() - min.getY();
		return height;
	}
}
