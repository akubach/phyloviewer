/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;

public abstract class Camera
{
	private Matrix33 _matrix = new Matrix33();
	private boolean allowZoom = true;
	private boolean panX = true;
	private boolean panY = true;

	protected Camera()
	{
	}

	public abstract Camera create();

	public abstract void zoomToBoundingBox(Box2D bbox);

	public Matrix33 getMatrix(int width, int height)
	{
		return _matrix;
	}

	public Matrix33 getViewMatrix()
	{
		return _matrix;
	}

	public void setViewMatrix(Matrix33 matrix)
	{
		_matrix = matrix;
	}

	public void zoom(double xCenter, double yCenter, double xZoom, double yZoom)
	{
		if(allowZoom)
		{
			Matrix33 T0 = Matrix33.makeTranslate(xCenter, yCenter);
			Matrix33 S = Matrix33.makeScale(xZoom, yZoom);
			Matrix33 T1 = Matrix33.makeTranslate(-xCenter, -yCenter);

			Matrix33 delta = T0.multiply(S.multiply(T1));
			Matrix33 matrix = delta.multiply(_matrix);
			this.setViewMatrix(matrix);
		}
	}

	public void zoom(double factor)
	{
		zoom(0.5, 0.5, factor, factor);
	}

	public void pan(double x, double y)
	{
		x = isXPannable() ? x : 0.0;
		y = isYPannable() ? y : 0.0;
		
		Matrix33 matrix = _matrix.multiply(Matrix33.makeTranslate(x, y));
		this.setViewMatrix(matrix);
	}

	public void reset()
	{
		this.setViewMatrix(new Matrix33());
	}

	/**
	 * Set the zoom values and don't allow any further zooming.
	 * @param xZoom
	 * @param yZoom
	 */
	public void lockToZoom(double xZoom, double yZoom)
	{
		double x = _matrix.getTranslationX();
		double y = _matrix.getTranslationY();
		
		// Reset the matrix.
		this.reset();
		
		_matrix.setTranslationX(x);
		_matrix.setTranslationY(y);
		
		this.zoom(0.5, 0.5, xZoom, yZoom);
		
		allowZoom = false;
	}

	public boolean isAllowZoom()
	{
		return allowZoom;
	}

	public void setAllowZoom(boolean allowZoom)
	{
		this.allowZoom = allowZoom;
	}
	
	public void setPannable(boolean x, boolean y)
	{
		this.panX = x;
		this.panY = y;
	}

	public boolean isXPannable()
	{
		return this.panX;
	}

	public boolean isYPannable()
	{
		return this.panY;
	}
}
