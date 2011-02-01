package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.PolarVector2;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public class Wedge extends Drawable
{
	Vector2 peak;
	PolarVector2 base0;
	PolarVector2 base1;
	
	public Wedge(Vector2 peak, PolarVector2 base0, PolarVector2 base1)
	{
		this.peak = peak;
		this.base0 = base0;
		this.base1 = base1;
	}
	
	@Override
	public void draw(IGraphics graphics)
	{
		if(graphics != null)
		{
			graphics.drawWedge(peak, base0, base1);
		}
	}
}
