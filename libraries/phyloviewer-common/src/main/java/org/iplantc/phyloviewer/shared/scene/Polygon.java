package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public class Polygon extends Drawable
{
	Vector2 vertices[];

	public Polygon(Vector2 vertices[])
	{
		this.vertices = vertices;
	}

	@Override
	public void draw(IGraphics graphics)
	{
		if(graphics != null && vertices != null)
		{
			graphics.drawPolygon(vertices);
		}
	}
}
