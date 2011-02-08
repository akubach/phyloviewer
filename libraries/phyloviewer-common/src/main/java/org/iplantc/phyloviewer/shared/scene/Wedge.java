package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public class Wedge extends Drawable
{
	Vector2 center;
	Vector2 peak;
	double radius;
	double startAngle;
	double endAngle;

	public Wedge(Vector2 center, Vector2 peak, double radius, double startAngle, double endAngle)
	{
		this.center = center;
		this.peak = peak;
		this.radius = radius;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
	}
	
	@Override
	public void draw(IGraphics graphics, IStyle style)
	{
		if(graphics != null)
		{
			if(style != null)
			{
				graphics.setStyle(style.getGlyphStyle());
			}
			
			graphics.drawWedge(center, peak, radius, startAngle, endAngle);
		}
	}
}
