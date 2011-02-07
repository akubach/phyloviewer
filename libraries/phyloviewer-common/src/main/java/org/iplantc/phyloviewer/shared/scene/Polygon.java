package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public class Polygon extends Drawable
{
	Vector2 vertices[];

	public Polygon(Vector2 vertices[])
	{
		this.vertices = vertices;
		Box2D box = new Box2D();
		for(Vector2 v : vertices)
		{
			box.expandBy(v);
		}
		this.setBoundingBox(box);
	}

	/**
	 * Create a triangle from the 3 vertices.
	 * 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Polygon createTriangle(Vector2 v0, Vector2 v1, Vector2 v2)
	{
		Vector2 vertices[] = new Vector2[3];
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;

		Polygon triangle = new Polygon(vertices);
		return triangle;
	}

	@Override
	public void draw(IGraphics graphics)
	{
		if(graphics != null && vertices != null)
		{
			graphics.drawPolygon(vertices);
		}
	}

	@Override
	public boolean intersect(Vector2 position, double distanceSquared)
	{
		if(vertices == null)
		{
			return false;
		}

		// Specialize for a triangle.
		// Should we have a shape classes in addition to just polygon (ie Triangle)?
		if(vertices.length == 3)
		{
			// Compute the barycentric coordinates to determine if the point is inside the triangle.
			Vector2 t0 = vertices[1].subtract(vertices[0]);
			Vector2 t1 = vertices[2].subtract(vertices[0]);
			Vector2 t2 = position.subtract(vertices[0]);

			// Compute dot products.
			double dot00 = t0.dot(t0);
			double dot01 = t0.dot(t1);
			double dot11 = t1.dot(t1);
			double dot20 = t2.dot(t0);
			double dot21 = t2.dot(t1);

			// Compute barycentric coordinates using ratio of the triangle areas.
			// This is twice the area of the triangle, but since we are using ratios, this is ok.
			double triangleArea = (dot00 * dot11 - dot01 * dot01);
			double invDenom = 1.0 / triangleArea;
			double u = (dot11 * dot20 - dot01 * dot21) * invDenom;
			double v = (dot00 * dot21 - dot01 * dot20) * invDenom;

			// The point is inside the triangle if barycentric coordinates u,v,w are all between 0 and 1.
			return (u >= 0) && (v >= 0) && ((u + v) <= 1);
		}

		return false;
	}
}
