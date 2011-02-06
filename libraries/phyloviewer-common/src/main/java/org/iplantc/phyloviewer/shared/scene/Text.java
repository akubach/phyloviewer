package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
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
		this.init(text, position, pixelOffset, 0.0);
	}

	public Text(String text, Vector2 position, Vector2 pixelOffset, double angle)
	{
		this.init(text, position, pixelOffset, angle);
	}

	private void init(String text, Vector2 position, Vector2 pixelOffset, double angle)
	{
		this.text = text;
		this.position = position;
		this.pixelOffset = pixelOffset;
		this.angle = angle;

		// TODO: Need a better bounding box.
		// Probably the best way to do this is to ask the canvas and the first call to draw.
		Box2D box = new Box2D();
		box.expandBy(position);
		this.setBoundingBox(box);
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
