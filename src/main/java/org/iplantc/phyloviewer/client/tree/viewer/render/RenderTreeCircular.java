package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.iplantc.phyloviewer.client.tree.viewer.model.INode;

public class RenderTreeCircular extends RenderTree {

	@Override
	protected void renderNode(INode node, ILayout layout, IGraphics graphics, Camera camera) {
		if ( layout instanceof ILayoutCircular ) {
			this.renderNode(node, (ILayoutCircular)layout, graphics, camera);
		}
	}
	
	protected void renderNode(INode node, ILayoutCircular layout, IGraphics graphics, Camera camera) {

		if ( graphics.isCulled(layout.getBoundingBox(node))) {
			return;
		}
		
		graphics.drawPoint(layout.getPosition(node));
		
		if (node.isLeaf()) {
			drawLabel(node, layout, graphics);
		} else if (canDrawChildLabels(node, layout, camera)) {
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
	
	private static boolean canDrawChildLabels(INode node, ILayoutCircular layout, Camera camera) {
		int pixelsPerLabel = 15;
		double pixelsNeeded = node.getNumberOfChildren() * pixelsPerLabel;
		double pixelsAvailable = pixelsAvailableForLabels(node, layout, camera);
		return pixelsAvailable >= pixelsNeeded;
	}
	
	private static void drawLabel(INode node, ILayoutCircular layout, IGraphics graphics) {
		PolarVector2 labelPosition;
		if (node.isLeaf()) {
			labelPosition = layout.getPolarPosition(node);
		} else {
			AnnularSector bounds = layout.getPolarBoundingBox(node);
			labelPosition = new PolarVector2(bounds.getMax().getRadius(), (bounds.getMin().getAngle() + bounds.getMax().getAngle()) / 2.0);
		}
		
		graphics.drawTextRadial(labelPosition, node.getLabel());
	}
	
	private static void renderPlaceholder(INode node, ILayoutCircular layout, IGraphics graphics) {
		PolarVector2 peak = layout.getPolarPosition(node);
		AnnularSector bounds = layout.getPolarBoundingBox(node);
		PolarVector2 base0 = new PolarVector2(bounds.getMax().getRadius(), bounds.getMin().getAngle());
		PolarVector2 base1 = new PolarVector2(bounds.getMax());
		
		graphics.drawWedge(peak.toCartesian(new Vector2(0.5,0.5)), base0, base1);
	}
	
	private void renderChildren(INode parent, ILayoutCircular layout, IGraphics graphics, Camera camera) {
		PolarVector2 parentPosition = layout.getPolarPosition(parent);
		AnnularSector childBounds = new AnnularSector(); //bounds of children, without descendants, for branch layout
		int numChildren = parent.getNumberOfChildren();
		for ( int i = 0; i < numChildren; ++i ) {
			INode child = parent.getChild(i);
			PolarVector2 childPosition = layout.getPolarPosition(child);
			PolarVector2 branchStart = new PolarVector2(parentPosition.getRadius(), childPosition.getAngle());
			graphics.drawLine(branchStart.toCartesian(new Vector2(0.5,0.5)), childPosition.toCartesian(new Vector2(0.5,0.5)));
			renderNode(child, layout, graphics, camera);
			childBounds.expandBy(childPosition);
		}
		graphics.drawArc(new PolarVector2(0.0,0.0).toCartesian(new Vector2(0.5,0.5)), parentPosition.getRadius(), childBounds.getMin().getAngle(), childBounds.getMax().getAngle());
	}
	
	private static double pixelsAvailableForLabels(INode node, ILayoutCircular layout, Camera camera) {
		AnnularSector polarBounds = layout.getPolarBoundingBox(node); 
		double arcLength = (polarBounds.getMax().getAngle() - polarBounds.getMin().getAngle()) * polarBounds.getMax().getRadius();
		arcLength *= camera.getMatrix().getScaleY(); //assuming xzoom == yzoom
		return arcLength;
	}
}
