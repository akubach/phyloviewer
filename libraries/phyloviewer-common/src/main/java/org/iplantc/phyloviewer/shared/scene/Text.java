package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;

public class Text extends Drawable
{
	String text;
	Vector2 position;
	Vector2 pixelOffset;
	double angle = 0.0;

	public Text(String text, Vector2 position, Vector2 pixelOffset)
	{
		this.text = text;
		this.position = position;
		this.pixelOffset = pixelOffset;
	}
	
	public Text(String text, Vector2 position, Vector2 pixelOffset, double angle)
	{
		this.text = text;
		this.position = position;
		this.pixelOffset = pixelOffset;
		this.angle = angle;
	}

	@Override
	public void draw(IGraphics graphics)
	{
		if(graphics != null)
		{
			graphics.drawText(position, pixelOffset, text, angle);
		}
	}
}
