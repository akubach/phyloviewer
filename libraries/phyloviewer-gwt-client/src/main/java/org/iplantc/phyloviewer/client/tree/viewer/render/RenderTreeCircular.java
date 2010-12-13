package org.iplantc.phyloviewer.client.tree.viewer.render;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView.RequestRenderCallback;
import org.iplantc.phyloviewer.shared.layout.ILayout;
import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.IGraphics;


public class RenderTreeCircular extends RenderTree {

	public RenderTreeCircular() {
	}
	
	public RenderTreeCircular(IDocument document) {
		super(document);
	}
	
	/*@Override
	protected boolean canDrawChildLabels(INode node, ILayout layout, IGraphics graphics) {
		int pixelsPerLabel = 15;
		double pixelsNeeded = node.getNumberOfChildren() * pixelsPerLabel;
		double pixelsAvailable = pixelsAvailableForLabels(node, layout, graphics);
		return pixelsAvailable >= pixelsNeeded;
	}*/
	
	@Override
	protected void drawLabel(INode node, ILayout layout, IGraphics graphics) {
		if (document.getLabel(node) != null)
		{
			PolarVector2 labelPosition;
			if (node.isLeaf()) {
				labelPosition = getPolarPosition(node,layout);
			} else {
				AnnularSector bounds = getPolarBoundingBox(node,layout);
				labelPosition = new PolarVector2(bounds.getMax().getRadius(), (bounds.getMin().getAngle() + bounds.getMax().getAngle()) / 2.0);
			}
			
			graphics.setStyle(this.getStyle(node).getLabelStyle());
			graphics.drawTextRadial(labelPosition, document.getLabel(node));
		}
	}
	
	@Override
	protected void renderPlaceholder(INode node, ILayout layout, IGraphics graphics) {
		PolarVector2 peak = getPolarPosition(node,layout);
		AnnularSector bounds = getPolarBoundingBox(node,layout);
		PolarVector2 base0 = new PolarVector2(bounds.getMax().getRadius(), bounds.getMin().getAngle());
		PolarVector2 base1 = new PolarVector2(bounds.getMax());

		graphics.setStyle(this.getStyle(node).getGlyphStyle());
		graphics.drawWedge(peak.toCartesian(new Vector2(0.5,0.5)), base0, base1);
		
		drawLabel(node, layout, graphics);
	}
	
	@Override
	protected void renderChildren(INode parent, ILayout layout, IGraphics graphics, RequestRenderCallback renderCallback) {
		
		PolarVector2 parentPosition = getPolarPosition(parent,layout);
		AnnularSector childBounds = new AnnularSector(); //bounds of children, without descendants, for branch layout
		
		INode[] children = parent.getChildren();
		for ( int i = 0; i < children.length; ++i ) {
			INode child = children[i];
			PolarVector2 childPosition = getPolarPosition(child,layout);

			PolarVector2 branchStart = new PolarVector2(parentPosition.getRadius(), childPosition.getAngle());
			
			graphics.setStyle(this.getStyle(child).getBranchStyle());
			graphics.drawLine(branchStart.toCartesian(new Vector2(0.5,0.5)), childPosition.toCartesian(new Vector2(0.5,0.5)));
			
			renderNode(child, layout, graphics, renderCallback);
			
			childBounds.expandBy(childPosition);
		}

		//FIXME how do we want to style the parent arc?  When children are all the same: same as children. When children have different colors: some default (black? parent branch color?). 
		graphics.setStyle(this.getStyle(parent).getBranchStyle());
		graphics.drawArc(new PolarVector2(0.0,0.0).toCartesian(new Vector2(0.5,0.5)), parentPosition.getRadius(), childBounds.getMin().getAngle(), childBounds.getMax().getAngle());
	}
	
	private static double pixelsAvailableForLabels(INode node, ILayout layout, IGraphics graphics) {
		AnnularSector polarBounds = getPolarBoundingBox(node,layout); 
		double arcLength = (polarBounds.getMax().getAngle() - polarBounds.getMin().getAngle()) * polarBounds.getMax().getRadius();
		arcLength *= graphics.getViewMatrix().getScaleY(); //assuming xzoom == yzoom
		return arcLength;
	}
	
	private static PolarVector2 getPolarPosition(INode node, ILayout layout) {
		return convertToPolar(layout.getPosition(node));
	}
	
	private static AnnularSector getPolarBoundingBox(INode node, ILayout layout ) {
		AnnularSector bounds = new AnnularSector();

		Box2D bbox = layout.getBoundingBox(node);
		bounds.expandBy(convertToPolar(bbox.getMin()));
		bounds.expandBy(convertToPolar(bbox.getMax()));
		
		return bounds;
	}
	
	private static PolarVector2 convertToPolar(Vector2 vector) {
		double r = 0.5 * ( vector.getX() / 0.8 );
		double angle = (2 * Math.PI) * vector.getY();
		return new PolarVector2 (r, Math.min(angle,2 * Math.PI));
	}
	
	protected Vector2 getPosition(INode node,ILayout layout) {
		return getPolarPosition(node,layout).toCartesian(new Vector2(0.5,0.5));
	}
	
	@Override
	public Box2D getBoundingBox(INode node,ILayout layout) {
		Box2D bounds = getPolarBoundingBox(node,layout).cartesianBounds();
		//Vector2 position = this.getPosition(node,layout);
		Vector2 position = new Vector2(0.5,0.5);
		
		Vector2 min = bounds.getMin().add(position);
		Vector2 max = bounds.getMax().add(position);
		return new Box2D(min,max);
	}
}
