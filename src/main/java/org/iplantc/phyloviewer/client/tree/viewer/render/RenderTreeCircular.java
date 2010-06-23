package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class RenderTreeCircular {
	
	public static void renderTree(ITree tree, ILayoutCircular layout, IGraphics graphics, Camera camera) {
		if ( tree == null || graphics == null || layout == null) {
			return;
		}
		
		INode root = tree.getRootNode();
		
		if ( root == null ) {
			return;
		}
		
		if (camera!=null){
			//setting xzoom == yzoom, to keep it circular (the zoom handler only zooms in y direction, for the rectangular layout)
			Matrix33 m = camera.getMatrix();
			m = m.multiply(Matrix33.makeScale(1/m.getScaleX(), 1.0));
			m = m.multiply(Matrix33.makeScale(m.getScaleY(), 1.0));
			graphics.setViewMatrix(m);
		}
		
		graphics.clear();
		
		renderNode(root, layout, graphics, camera);
	}

	private static void renderNode(INode node, ILayoutCircular layout, IGraphics graphics, Camera camera) {

		if ( graphics.isCulled(layout.getBoundingBox(node))) {
			return;
		}
		
		graphics.drawPoint(layout.getPosition(node));
		
		if (node.isLeaf()) {
			drawLabel(node, layout, graphics);
		} else if (canDrawLeafLabels(node, layout, camera)) {
			renderChildren(node, layout, graphics, camera);
		} else {
			// Find a label to use, if the node doesn't have one.
			if ( node.getLabel() == null || node.getLabel().equals("") ) {
				node.setLabel(node.findLabelOfFirstLeafNode());
			}
			
			renderPlaceholder(node, layout, graphics);
			drawLabel(node, layout, graphics);
		}
	}
	
	private static boolean canDrawLeafLabels(INode node, ILayoutCircular layout, Camera camera) {
		int pixelsPerTaxon = 15;
		double pixelsNeeded = node.getNumberOfLeafNodes() * pixelsPerTaxon;
		double pixelsAvailable = pixelsAvailableForLabel(node, layout, camera);
		return pixelsAvailable >= pixelsNeeded;
	}
	
	private static void drawLabel(INode node, ILayoutCircular layout, IGraphics graphics) {
		PolarVector2 labelPosition;
		if (node.isLeaf()) {
			labelPosition = layout.getPosition(node);
		} else {
			AnnularSector bounds = layout.getPolarBoundingBox(node);
			labelPosition = new PolarVector2(bounds.getMax().getRadius(), (bounds.getMin().getAngle() + bounds.getMax().getAngle()) / 2.0);
		}
		
		//TODO rotate so labels are along radii
		graphics.drawTextRadial(labelPosition, node.getLabel());
	}
	
	private static void renderPlaceholder(INode node, ILayoutCircular layout, IGraphics graphics) {
		//note: can't use existing graphics.drawTriangle because it only draws triangles with the base on the right.
		PolarVector2 peak = layout.getPosition(node);
		AnnularSector bounds = layout.getPolarBoundingBox(node);
		PolarVector2 base0 = new PolarVector2(bounds.getMax().getRadius(), bounds.getMin().getAngle());
		PolarVector2 base1 = new PolarVector2(bounds.getMax());
		
		//TODO move all of this into a Graphics method and draw a real triangle with fill
		graphics.drawLine(peak, base0);
		graphics.drawArc(new PolarVector2(0.0,0.0), base0.getRadius(), base0.getAngle(), base1.getAngle());
		graphics.drawLine(base1, peak);
	}
	
	private static void renderChildren(INode parent, ILayoutCircular layout, IGraphics graphics, Camera camera) {
		PolarVector2 parentPosition = layout.getPosition(parent);
		AnnularSector childBounds = new AnnularSector(); //bounds of children, without descendants, for branch layout
		int numChildren = parent.getNumberOfChildren();
		for ( int i = 0; i < numChildren; ++i ) {
			INode child = parent.getChild(i);
			PolarVector2 childPosition = layout.getPosition(child);
			PolarVector2 branchStart = new PolarVector2(parentPosition.getRadius(), childPosition.getAngle());
			graphics.drawLine(branchStart, childPosition);
			renderNode(child, layout, graphics, camera);
			childBounds.expandBy(childPosition);
		}
		graphics.drawArc(new PolarVector2(0.0,0.0), parentPosition.getRadius(), childBounds.getMin().getAngle(), childBounds.getMax().getAngle());
	}
	
	private static double pixelsAvailableForLabel(INode node, ILayoutCircular layout, Camera camera) {
		AnnularSector polarBounds = layout.getPolarBoundingBox(node); 
		double arcLength = (polarBounds.getMax().getAngle() - polarBounds.getMin().getAngle()) * polarBounds.getMax().getRadius();
		arcLength *= camera.getMatrix().getScaleY(); //assuming xzoom == yzoom
		return arcLength;
		
		//For now we'll just assume the labels are not rotated
		//Box2D bounds = polarBounds.getOuterArcBounds();
		//Box2D displayedBounds = camera.getMatrix().transform(bounds);
		//return displayedBounds.getMax().getY() - displayedBounds.getMin().getY();
	}
}
