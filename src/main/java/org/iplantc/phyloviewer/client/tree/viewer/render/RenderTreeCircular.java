package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView.RequestRenderCallback;
import org.iplantc.phyloviewer.client.tree.viewer.render.style.INodeStyle.Element;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.layout.ILayoutCircular;
import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.INode;


public class RenderTreeCircular extends RenderTree {

	
	@Override
	protected boolean canDrawChildLabels(INode node, ILayout layout, IGraphics graphics) {
		int pixelsPerLabel = 15;
		double pixelsNeeded = node.getNumberOfChildren() * pixelsPerLabel;
		double pixelsAvailable = pixelsAvailableForLabels(node, (ILayoutCircular) layout, graphics);
		return pixelsAvailable >= pixelsNeeded;
	}
	
	@Override
	protected void drawLabel(INode node, ILayout layout, IGraphics graphics) {
		if (getLabel(node) != null)
		{
			PolarVector2 labelPosition;
			if (node.isLeaf()) {
				labelPosition = ((ILayoutCircular)layout).getPolarPosition(node);
			} else {
				AnnularSector bounds = ((ILayoutCircular)layout).getPolarBoundingBox(node);
				labelPosition = new PolarVector2(bounds.getMax().getRadius(), (bounds.getMin().getAngle() + bounds.getMax().getAngle()) / 2.0);
			}
			
			setStyle(node, graphics, Element.LABEL);
			graphics.drawTextRadial(labelPosition, getLabel(node));
		}
	}
	
	@Override
	protected void renderPlaceholder(INode node, ILayout layout, IGraphics graphics) {
		ILayoutCircular layoutCircular = (ILayoutCircular) layout;
		PolarVector2 peak = layoutCircular.getPolarPosition(node);
		AnnularSector bounds = layoutCircular.getPolarBoundingBox(node);
		PolarVector2 base0 = new PolarVector2(bounds.getMax().getRadius(), bounds.getMin().getAngle());
		PolarVector2 base1 = new PolarVector2(bounds.getMax());

		setStyle(node, graphics, Element.GLYPH);
		graphics.drawWedge(peak.toCartesian(new Vector2(0.5,0.5)), base0, base1);
		
		drawLabel(node, layout, graphics);
	}
	
	@Override
	protected void renderChildren(INode parent, ILayout layout, IGraphics graphics, RequestRenderCallback renderCallback) {
		ILayoutCircular layoutCircular = (ILayoutCircular) layout;
		
		PolarVector2 parentPosition = layoutCircular.getPolarPosition(parent);
		AnnularSector childBounds = new AnnularSector(); //bounds of children, without descendants, for branch layout
		int numChildren = parent.getNumberOfChildren();
		for ( int i = 0; i < numChildren; ++i ) {
			INode child = parent.getChild(i);
			PolarVector2 childPosition = layoutCircular.getPolarPosition(child);

			PolarVector2 branchStart = new PolarVector2(parentPosition.getRadius(), childPosition.getAngle());
			
			setStyle(child, graphics, Element.BRANCH);
			graphics.drawLine(branchStart.toCartesian(new Vector2(0.5,0.5)), childPosition.toCartesian(new Vector2(0.5,0.5)));
			
			renderNode(child, layout, graphics, renderCallback);
			
			childBounds.expandBy(childPosition);
		}

		//FIXME how do we want to style the parent arc?  When children are all the same: same as children. When children have different colors: some default (black? parent branch color?). 
		setStyle(parent, graphics, Element.BRANCH);
		graphics.drawArc(new PolarVector2(0.0,0.0).toCartesian(new Vector2(0.5,0.5)), parentPosition.getRadius(), childBounds.getMin().getAngle(), childBounds.getMax().getAngle());
	}
	
	private static double pixelsAvailableForLabels(INode node, ILayoutCircular layout, IGraphics graphics) {
		AnnularSector polarBounds = layout.getPolarBoundingBox(node); 
		double arcLength = (polarBounds.getMax().getAngle() - polarBounds.getMin().getAngle()) * polarBounds.getMax().getRadius();
		arcLength *= graphics.getViewMatrix().getScaleY(); //assuming xzoom == yzoom
		return arcLength;
	}
}
