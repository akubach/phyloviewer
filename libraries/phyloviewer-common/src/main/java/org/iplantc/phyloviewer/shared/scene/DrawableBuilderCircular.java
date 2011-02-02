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
	public Drawable[] buildBranch(INode parent, INode child, ILayoutData layout)
	{
		PolarVector2 parentPosition = CircularCoordinates.getPolarPosition(parent, layout);
		PolarVector2 childPosition = CircularCoordinates.getPolarPosition(child, layout);

		PolarVector2 branchStart = new PolarVector2(parentPosition.getRadius(), childPosition.getAngle());

		Vector2 lineStart = CircularCoordinates.convertToCartesian(branchStart);
		Vector2 lineEnd = CircularCoordinates.convertToCartesian(childPosition);
		Vector2 vertices[] = new Vector2[] { lineStart, lineEnd };
		Line line = new Line(vertices);

		double angle0 = childPosition.getAngle();
		double angle1 = parentPosition.getAngle();
		double min = Math.min(angle0, angle1);
		double max = Math.max(angle0, angle1);

		Arc arc = new Arc(CircularCoordinates.getCenter(), parentPosition.getRadius(), min, max);

		// TODO Auto-generated method stub
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

		return new Text(text, textPosition, offset, angle);
	}

	@Override
	public Drawable[] buildNodeAbstraction(INode node, IDocument document, ILayoutData layout)
	{
		PolarVector2 peak = CircularCoordinates.getPolarPosition(node, layout);
		AnnularSector bounds = CircularCoordinates.getPolarBoundingBox(node, layout);
		PolarVector2 base0 = new PolarVector2(bounds.getMax().getRadius(), bounds.getMin().getAngle());
		PolarVector2 base1 = new PolarVector2(bounds.getMax());

		double radius = bounds.getMax().getRadius();

		Wedge wedge = new Wedge(CircularCoordinates.getCenter(),
				CircularCoordinates.convertToCartesian(peak), radius, base0.getAngle(), base1.getAngle());

		PolarVector2 labelPosition = new PolarVector2(bounds.getMax().getRadius(), (bounds.getMin()
				.getAngle() + bounds.getMax().getAngle()) / 2.0);

		Drawable textDrawable = buildText(labelPosition, document.getLabel(node));
		return new Drawable[] { wedge, textDrawable };
	}

}
