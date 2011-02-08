package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.IGraphics;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public abstract class Drawable
{
	/**
	 * Context for what this drawable is representing
	 * 
	 * @author adamkubach
	 * 
	 */
	public enum Context
	{
		CONTEXT_UNKNOWN, CONTEXT_NODE, CONTEXT_BRANCH, CONTEXT_GLYPH, CONTEXT_LABEL
	}

	Box2D boundingBox = new Box2D();
	Context context = Context.CONTEXT_UNKNOWN;

	/**
	 * Constructor.
	 */
	public Drawable()
	{
	}

	public Box2D getBoundingBox()
	{
		return boundingBox;
	}

	public void setBoundingBox(Box2D boundingBox)
	{
		this.boundingBox = boundingBox;
	}

	public Context getContext()
	{
		return context;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	public boolean intersect(Vector2 position, double distanceSquared)
	{
		return false;
	}

	public abstract void draw(IGraphics graphics, IStyle style);
}
