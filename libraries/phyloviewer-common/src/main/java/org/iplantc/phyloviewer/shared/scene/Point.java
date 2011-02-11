package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.Defaults;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle.Shape;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public class Point extends Drawable
{
	Vector2 point;

	Point(Vector2 point)
	{
		this.point = point;

		Box2D box = this.getBoundingBox();
		box.expandBy(point);
	}

	@Override
	public void draw(IGraphics graphics, IStyle style)
	{
		if(graphics != null)
		{
			// Assume circle.
			Shape shape = Shape.SHAPE_CIRCLE;
			double pointSize = Defaults.POINT_RADIUS;
			
			if(style != null)
			{
				INodeStyle nodeStyle = style.getNodeStyle();
				if(nodeStyle != null)
				{
					shape = nodeStyle.getShape();
					pointSize = nodeStyle.getPointSize();
					
					graphics.setStyle(nodeStyle);
				}
			}
			
			if(shape == Shape.SHAPE_CIRCLE)
			{
				graphics.drawPoint(point);
			}
			else if(shape == Shape.SHAPE_SQUARE)
			{
				Matrix33 matrix = graphics.getViewMatrix();
				
				Vector2 center = matrix.transform(point);
				double halfSize = pointSize / 2.0;
				Vector2[] vertices = new Vector2[4];
				vertices[0] = new Vector2(center.getX() - halfSize, center.getY() - halfSize);
				vertices[1] = new Vector2(center.getX() + halfSize, center.getY() - halfSize);
				vertices[2] = new Vector2(center.getX() + halfSize, center.getY() + halfSize);
				vertices[3] = new Vector2(center.getX() - halfSize, center.getY() + halfSize);
				
				Matrix33 IM = matrix.inverse();
				vertices[0] = IM.transform(vertices[0]);
				vertices[1] = IM.transform(vertices[1]);
				vertices[2] = IM.transform(vertices[2]);
				vertices[3] = IM.transform(vertices[3]);
				
				graphics.drawPolygon(vertices);
			}
		}
	}
	
	@Override
	public boolean intersect(Vector2 position, double distanceSquared)
	{
		double distance = position.distanceSquared(point);
		return distance < (distanceSquared * 25); // Need to get the point size.
	}
}
