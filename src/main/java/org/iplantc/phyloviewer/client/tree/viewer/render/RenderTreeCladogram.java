package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;

public class RenderTreeCladogram extends RenderTree {

	protected void renderNode(INode node, ILayout layout, IGraphics graphics, Camera camera) {
		
		if ( graphics.isCulled(layout.getBoundingBox(node)))
			return;
		
		if (node.isLeaf()) {
			graphics.setStyle(node.getStyle().getElementStyle(Element.LABEL));
			graphics.drawText(new Vector2(layout.getPosition(node).getX(),layout.getPosition(node).getY()), node.getLabel());
		}
		
		Box2D boundingBox = layout.getBoundingBox(node);
		
		// If the current clade won't fit on the screen, draw a triangle.
		if ( _estimateNumberOfPixelsNeeded(node) > _getHeightOfBoundingBoxInPixels(boundingBox, camera)) {
			Vector2 min = boundingBox.getMin();
			Vector2 max = boundingBox.getMax();

			graphics.setStyle(node.getStyle().getElementStyle(Element.GLYPH));

			graphics.drawTriangle(layout.getPosition(node),max.getX(),min.getY(),max.getY());
			
			// Find a label to use, if the node doesn't have one.
			if ( node.getLabel() == null || node.getLabel().equals("") ) {
				node.setLabel(node.findLabelOfFirstLeafNode());
			}
			
			// Draw the label.
			graphics.setStyle(node.getStyle().getElementStyle(Element.LABEL));

			graphics.drawText(new Vector2(max.getX(),(min.getY()+max.getY())/2.0), node.getLabel());
		}
		else {
			int numChildren = node.getNumberOfChildren();
			for ( int i = 0; i < numChildren; ++i ) {

				INode child = node.getChild(i);
				graphics.setStyle(child.getStyle().getElementStyle(Element.BRANCH));
				graphics.drawRightAngle(layout.getPosition(node), layout.getPosition(child));
				
				renderNode(node.getChild(i),layout,graphics,camera);
			}
		}
		
		graphics.setStyle(node.getStyle().getElementStyle(Element.NODE));
		graphics.drawPoint(layout.getPosition(node));
	}
}
