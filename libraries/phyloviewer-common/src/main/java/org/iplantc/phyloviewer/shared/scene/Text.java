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
	boolean dirtyBoundingBox;

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

		dirtyBoundingBox = true;
	}

	public String getText()
	{
		return text;
	}

	public Vector2 getPosition()
	{
		return position;
	}

	public Vector2 getPixelOffset()
	{
		return pixelOffset;
	}

	public double getAngle()
	{
		return angle;
	}

	@Override
	public void draw(IGraphics graphics)
	{
		if(graphics != null)
		{
			if(dirtyBoundingBox)
			{
				Box2D box = graphics.calculateBoundingBox(this);
				this.setBoundingBox(box);
				dirtyBoundingBox = false;
			}

			graphics.drawText(position, pixelOffset, text, angle);
		}
	}

	@Override
	public boolean intersect(Vector2 position, double distanceSquared)
	{
		Box2D box = getBoundingBox();
		if(box != null)
		{
			return box.contains(position);
		}

		return false;
	}
}
