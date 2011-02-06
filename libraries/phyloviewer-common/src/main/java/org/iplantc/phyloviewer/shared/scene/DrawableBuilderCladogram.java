package org.iplantc.phyloviewer.shared.scene;

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
	public Drawable[] buildBranch(INode parent, INode child, ILayoutData layout)
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
		Drawable[] drawables = new Drawable[] { line };
		return drawables;
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

		Vector2 vertices[] = new Vector2[3];
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;

		Polygon triangle = new Polygon(vertices);
		triangle.setContext(Drawable.Context.CONTEXT_GLYPH);

		String text = document.getLabel(node);
		Vector2 position = new Vector2(max.getX(), (min.getY() + max.getY()) / 2.0);

		Text drawable = new Text(text, position, pixelOffset);
		drawable.setContext(Drawable.Context.CONTEXT_LABEL);

		return new Drawable[] { triangle, drawable };
	}
}
