package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
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
			if(style != null)
			{
				graphics.setStyle(style.getNodeStyle());
			}
			
			graphics.drawPoint(point);
		}
	}
	
	@Override
	public boolean intersect(Vector2 position, double distanceSquared)
	{
		double distance = position.distanceSquared(point);
		return distance < (distanceSquared * 25); // Need to get the point size.
	}
}
