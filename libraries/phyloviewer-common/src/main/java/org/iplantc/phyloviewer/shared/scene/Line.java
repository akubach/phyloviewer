package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public class Line extends Drawable
{

	Vector2 vertices[];

	public Line(Vector2 vertices[])
	{
		this.vertices = vertices;
		Box2D box = new Box2D();
		for(Vector2 v:vertices)
		{
			box.expandBy(v);
		}
		this.setBoundingBox(box);
	}
	
	public Line(Vector2 vertices[],Box2D box)
	{
		this.vertices = vertices;
		this.setBoundingBox(box);
	}

	@Override
	public void draw(IGraphics graphics)
	{
		if(vertices != null && graphics != null)
		{
			graphics.drawLineStrip(vertices);
		}

	}

	public boolean intersect(Vector2 position, double distanceSquared)
	{
		if(vertices == null || vertices.length < 2)
		{
			return false;
		}

		for(int i = 1;i < vertices.length;++i)
		{
			Vector2 start = vertices[i - 1];
			Vector2 end = vertices[i];

			double dSquared = distanceSquared(start, end, position);
			if(isPointInSegment(start, end, position) && dSquared < distanceSquared)
			{
				return true;
			}
		}

		return false;
	}

	public static boolean isPointInSegment(Vector2 start, Vector2 end, Vector2 position)
	{
		Vector2 w = position.subtract(start);
		Vector2 v = end.subtract(start);
		double t = w.dot(v) / v.dot(v);
		
		return t >= 0 && t <= 1;
	}

	public static double distanceSquared(Vector2 start, Vector2 end, Vector2 position)
	{
		// Turn the line segment into point, vector form of line.
		Vector2 p = start.clone();
		Vector2 v = end.subtract(start);
		v.normalize();

		Vector2 w = position.subtract(p);

		double vsq = v.dot(v);
		double wsq = w.dot(w);
		double proj = w.dot(v);
		return wsq - proj * proj / vsq;
	}
}
