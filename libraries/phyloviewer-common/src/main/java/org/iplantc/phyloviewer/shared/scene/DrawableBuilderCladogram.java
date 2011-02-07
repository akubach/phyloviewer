package org.iplantc.phyloviewer.shared.scene;

import java.util.ArrayList;

import org.iplantc.phyloviewer.shared.layout.ILayoutData;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.model.IDocument;
import org.iplantc.phyloviewer.shared.model.INode;

public class DrawableBuilderCladogram implements IDrawableBuilder
{
	Vector2 pixelOffset = new Vector2(7, 2);

	@Override
	public Drawable[] buildNode(INode node, IDocument document, ILayoutData layout)
	{
		Vector2 position = layout.getPosition(node);
		Point point = new Point(position);
		point.setContext(Drawable.Context.CONTEXT_NODE);
		return new Drawable[] { point };
	}

	@Override
	public Drawable[] buildBranch(INode parent, INode child, IDocument document, ILayoutData layout)
	{
		Vector2 start = layout.getPosition(parent);
		Vector2 end = layout.getPosition(child);

		Vector2 vertices[] = new Vector2[3];
		vertices[0] = start;
		vertices[1] = new Vector2(start.getX(), end.getY());
		vertices[2] = end;

		Box2D box = new Box2D();
		box.expandBy(start);
		box.expandBy(end);

		Line line = new Line(vertices, box);
		line.setContext(Drawable.Context.CONTEXT_BRANCH);

		ArrayList<Drawable> drawables = new ArrayList<Drawable>();
		drawables.add(line);

		if(document != null && document.hasBranchDecoration(child.getId()))
		{
			double halfBase = 0.015;
			Vector2 horizontalLine = end.subtract(vertices[1]);
			double halfLength = horizontalLine.length() / 2.0;
			Vector2 midPoint = new Vector2(start.getX() + halfLength, end.getY());
			Vector2 v0 = midPoint.subtract(new Vector2(halfBase, -halfBase));
			Vector2 v1 = midPoint.add(new Vector2(0.0, -halfBase));
			Vector2 v2 = midPoint.add(new Vector2(halfBase, halfBase));

			Polygon triangle = Polygon.createTriangle(v0, v1, v2);
			triangle.setContext(Drawable.Context.CONTEXT_BRANCH);
			drawables.add(triangle);
		}

		return (Drawable[])drawables.toArray(new Drawable[drawables.size()]);
	}

	@Override
	public Drawable buildText(INode node, IDocument document, ILayoutData layout)
	{
		String text = document.getLabel(node);
		Vector2 position = layout.getPosition(node);

		Text drawable = new Text(text, position, pixelOffset);
		drawable.setContext(Drawable.Context.CONTEXT_LABEL);
		return drawable;
	}

	@Override
	public Drawable[] buildNodeAbstraction(INode node, IDocument document, ILayoutData layout)
	{
		Box2D boundingBox = layout.getBoundingBox(node);
		Vector2 min = boundingBox.getMin();
		Vector2 max = boundingBox.getMax();

		double x = max.getX();
		double y0 = min.getY();
		double y1 = max.getY();

		Vector2 v0 = layout.getPosition(node);
		Vector2 v1 = new Vector2(x, y0);
		Vector2 v2 = new Vector2(x, y1);

		Polygon triangle = Polygon.createTriangle(v0, v1, v2);
		triangle.setContext(Drawable.Context.CONTEXT_GLYPH);

		String text = document.getLabel(node);
		Vector2 position = new Vector2(max.getX(), (min.getY() + max.getY()) / 2.0);

		Text drawable = new Text(text, position, pixelOffset);
		drawable.setContext(Drawable.Context.CONTEXT_LABEL);

		return new Drawable[] { triangle, drawable };
	}
}
