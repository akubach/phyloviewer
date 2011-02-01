package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public class Arc extends Drawable
{
	Vector2 center;
	double radius;
	double startAngle;
	double endAngle;

	public Arc(Vector2 center, double radius, double startAngle, double endAngle)
	{
		this.center = center;
		this.radius = radius;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
	}

	@Override
	public void draw(IGraphics graphics)
	{
		if(graphics != null)
		{
			graphics.drawArc(center, radius, startAngle, endAngle);
		}
	}
}
