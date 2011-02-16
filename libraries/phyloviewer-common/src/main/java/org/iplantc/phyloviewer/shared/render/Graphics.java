package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;

public abstract class Graphics implements IGraphics
{
	private Matrix33 viewMatrix = new Matrix33();
	private Matrix33 projection = new Matrix33();
	private Viewport viewport = new Viewport();

	protected Matrix33 objectToScreenMatrix = new Matrix33();
	protected Matrix33 screenToObjectMatrix = new Matrix33();
	
	private Box2D screenBounds = new Box2D();
	
	private static Matrix33 createProjectionMatrix(double left, double right, double bottom, double top)
	{
		Matrix33 m = new Matrix33();

		// From OpenGL documentation.
		double tx = (-(right + left) / (right - left));
		double ty = (-(top + bottom) / (top - bottom));

		m.set(0, 0, 2 / (right - left));
		m.set(1, 1, 2 / (top - bottom));

		m.setTranslationX(tx);
		m.setTranslationY(ty);

		return m;
	}

	@Override
	public void setViewport(int x, int y, int width, int height)
	{
		viewport.setX(x);
		viewport.setY(y);
		viewport.setWidth(width);
		viewport.setHeight(height);

		updateMatrix();
	}

	@Override
	public void setProjection(double left, double right, double bottom, double top)
	{
		projection = createProjectionMatrix(left, right, bottom, top);

		updateMatrix();
	}

	@Override
	public int getWidth()
	{
		return viewport.getWidth();
	}

	@Override
	public int getHeight()
	{
		return viewport.getHeight();
	}
	
	@Override
	public Matrix33 getObjectToScreenMatrix()
	{
		return objectToScreenMatrix;
	}

	@Override
	public Matrix33 getScreenToObjectMatrix()
	{
		return screenToObjectMatrix;
	}
	
	/**
	 * Set the view matrix
	 */
	@Override
	public void setViewMatrix(Matrix33 matrix)
	{
		this.viewMatrix = matrix;

		updateMatrix();
	}

	private void updateMatrix()
	{
		Vector2 min = new Vector2(-1, -1);
		Vector2 max = new Vector2(1, 1);
		Matrix33 VP = projection.multiply(viewMatrix);
		Matrix33 IVP = VP.inverse();
		min = IVP.transform(min);
		max = IVP.transform(max);

		screenBounds.setMin(min);
		screenBounds.setMax(max);
		
		Matrix33 window = viewport.computeWindowMatrix();
		objectToScreenMatrix = window.multiply(projection.multiply(viewMatrix));

		try
		{
			screenToObjectMatrix = objectToScreenMatrix.inverse();
		}
		catch(Exception e)
		{
			screenToObjectMatrix = new Matrix33();
		}
	}

	/**
	 * Get the view matrix.
	 */
	@Override
	public Matrix33 getViewMatrix()
	{
		return viewMatrix;
	}

	/**
	 * Check to see if the given bounding box is visible.
	 */
	@Override
	public Boolean isCulled(Box2D bbox)
	{
		if(!bbox.valid())
			return false;

		return !screenBounds.intersects(bbox);
	}
}
