package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Matrix33;

public class Viewport
{
	private int x;
	private int y;
	private int width;
	private int height;

	public Viewport()
	{
	}

	/**
	 * This matrix converts from clip-space (after projection is applied) to window space.
	 */
	public Matrix33 computeWindowMatrix()
	{
		return Matrix33.makeTranslate(x, y)
				.multiply(Matrix33.makeScale(0.5 * (double)width, 0.5 * (double)height))
				.multiply(Matrix33.makeTranslate(1.0, 1.0));
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
}
