package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.layout.CircularCoordinates;
import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.AnnularSector;
import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

public class DrawableBuilderCircular implements IDrawableBuilder
{
	@Override
	public Drawable[] buildNode(INode node, IDocument document, ILayoutData layout)
	{
		Vector2 position = CircularCoordinates.getCartesianPosition(node, layout);
		Point point = new Point(position);
		point.setContext(Drawable.Context.CONTEXT_NODE);
		return new Drawable[] { point };
	}
	
	@Override
	public Drawable[] buildBranch(INode parent, INode child, IDocument document, ILayoutData layout)
	{
		PolarVector2 parentPosition = CircularCoordinates.getPolarPosition(parent, layout);
		PolarVector2 childPosition = CircularCoordinates.getPolarPosition(child, layout);

		PolarVector2 branchStart = new PolarVector2(parentPosition.getRadius(), childPosition.getAngle());

		Vector2 lineStart = CircularCoordinates.convertToCartesian(branchStart);
		Vector2 lineEnd = CircularCoordinates.convertToCartesian(childPosition);
		Vector2 vertices[] = new Vector2[] { lineStart, lineEnd };
		Line line = new Line(vertices);
		line.setContext(Drawable.Context.CONTEXT_BRANCH);

		double angle0 = childPosition.getAngle();
		double angle1 = parentPosition.getAngle();
		double min = Math.min(angle0, angle1);
		double max = Math.max(angle0, angle1);

		Arc arc = new Arc(CircularCoordinates.getCenter(), parentPosition.getRadius(), min, max);
		arc.setContext(Drawable.Context.CONTEXT_BRANCH);

		return new Drawable[] { line, arc };
	}

	@Override
	public Drawable buildText(INode node, IDocument document, ILayoutData layout)
	{
		PolarVector2 position = CircularCoordinates.getPolarPosition(node, layout);
		String text = document.getLabel(node);
		return buildText(position, text);
	}

	private static Drawable buildText(PolarVector2 position, String text)
	{
		double height = 10;

		PolarVector2 relativePosition = new PolarVector2(0.0, 0.0);

		final int margin = 8;
		relativePosition.setRadius(margin);

		double angleHeight = 2 * Math.sin(height / (2 * relativePosition.getRadius()));
		relativePosition.setAngle(position.getAngle() + angleHeight / 2);

		Vector2 textPosition = position.toCartesian(new Vector2(0.5, 0.5));
		double angle = position.getAngle();

		Vector2 offset = relativePosition.toCartesian(new Vector2(0.5, 0.5));

		Text textDrawable = new Text(text, textPosition, offset, angle); 
		textDrawable.setContext(Drawable.Context.CONTEXT_LABEL);
		return textDrawable;
	}

	@Override
	public Drawable[] buildNodeAbstraction(INode node, IDocument document, ILayoutData layout)
	{
		AnnularSector bounds = CircularCoordinates.getPolarBoundingBox(node, layout);
		double radius = bounds.getMax().getRadius();
		double minAngle = bounds.getMin().getAngle();
		double maxAngle = bounds.getMax().getAngle();
		Vector2 center = CircularCoordinates.getCenter();
		
		PolarVector2 peak = CircularCoordinates.getPolarPosition(node, layout);
		Vector2 cPeak = CircularCoordinates.convertToCartesian(peak);

		Wedge wedge = new Wedge(center, cPeak, radius, minAngle, maxAngle);
		wedge.setContext(Drawable.Context.CONTEXT_GLYPH);

		double midAngle = (minAngle + maxAngle) / 2.0; 
		PolarVector2 labelPosition = new PolarVector2(radius, midAngle);

		Drawable textDrawable = buildText(labelPosition, document.getLabel(node));
		return new Drawable[] { wedge, textDrawable };
	}
}
