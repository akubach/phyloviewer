package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;

public class RenderTreeCircular {
	
	public static void renderTree(ITree tree, ILayoutCircular layout, IGraphics graphics, Camera camera) {
		if ( tree == null || graphics == null )
			return;
		
		INode root = tree.getRootNode();
		
		if ( root == null )
			return;
		
		if (camera!=null){
			graphics.setViewMatrix(camera.getMatrix());
		}
		
		graphics.clear();
		
		renderNode(root, layout, graphics, camera);
	}

	private static void renderNode(INode node, ILayoutCircular layout, IGraphics graphics, Camera camera) {

		if ( graphics.isCulled(layout.getBoundingBox(node))) {
			return;
		}
		
		if (canDrawLeafLabels(node, layout, camera)) {
			graphics.drawPoint(layout.getPosition(node));
			renderChildren(node, layout, graphics, camera);
		} else {
			renderPlaceholder(node, layout, graphics);
		}
		
		if (node.isLeaf()) {
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
		//TODO rotate so labels are along radii
		graphics.drawText(layout.getPosition(node), node.getLabel());
	}
	
	private static void renderPlaceholder(INode node, ILayoutCircular layout, IGraphics graphics) {
		//note: can't use existing graphics.drawTriangle because it only draws triangles with the base on the right.
		// TODO make the outside edge an arc.  for now, just drawing a triangle.
		PolarVector2 peak = layout.getPosition(node);
		AnnularSector bounds = layout.getPolarBoundingBox(node);
		PolarVector2 base0 = new PolarVector2(bounds.getMax());
		PolarVector2 base1 = new PolarVector2(bounds.getMax().getRadius(), bounds.getMin().getAngle());
		
		//TODO move all of this into a Graphics method and draw a real triangle with fill
		graphics.drawLine(peak, base0);
		graphics.drawLine(base0, base1);
		graphics.drawLine(base1, peak);
	}
	
	private static void renderChildren(INode node, ILayoutCircular layout, IGraphics graphics, Camera camera) {
		int numChildren = node.getNumberOfChildren();
		for ( int i = 0; i < numChildren; ++i ) {
			renderBranch(node, node.getChild(i), layout, graphics);
			
			renderNode(node.getChild(i), layout, graphics, camera);
		}
	}
	
	private static void renderBranch(INode parent, INode child, ILayoutCircular layout, IGraphics graphics) {
		PolarVector2 parentPosition = layout.getPosition(parent);
		PolarVector2 childPosition = layout.getPosition(child);
		
		//A straight line for now.  TODO: Draw the usual arcs and radii
		graphics.drawLine(parentPosition, childPosition);
	}
	
	private static double pixelsAvailableForLabel(INode node, ILayoutCircular layout, Camera camera) {
		AnnularSector polarBounds = layout.getPolarBoundingBox(node); 
		//double arcLength = (polarBounds.getMax().getAngle() - polarBounds.getMin().getAngle()) * polarBounds.getMax().getRadius();
		//TODO find out how many pixels of arc this is with the current zoom (can't assume xzoom == yzoom)
		
		//For now we'll just assume the labels are not rotated
		Box2D bounds = polarBounds.cartesianBounds();
		Box2D displayedBounds = camera.getMatrix().transform(bounds);
		return displayedBounds.getMax().getY() - displayedBounds.getMin().getY();
	}
}
