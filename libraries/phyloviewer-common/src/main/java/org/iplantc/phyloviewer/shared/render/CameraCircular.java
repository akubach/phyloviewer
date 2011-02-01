package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.layout.CircularCoordinates;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;

public class CameraCircular extends Camera
{
	static final double labelMargin = 150;

	public Camera create()
	{
		return new CameraCircular();
	}

	public Matrix33 getMatrix(int width, int height)
	{
		double scale = Math.min(width, height) - labelMargin;
		double tx = (width - height) / 2.0;
		tx = Math.max(tx, 0);
		tx += labelMargin / 2.0;
		double ty = (height - width) / 2.0;
		ty = Math.max(ty, 0);
		ty += labelMargin / 2.0;

		Matrix33 s = Matrix33.makeScale(scale, scale);
		Matrix33 t = Matrix33.makeTranslate(tx, ty);
		return t.multiply(s).multiply(this.getViewMatrix());
	}

	public void zoomToBoundingBox(Box2D boundingBox)
	{
		Box2D bounds = CircularCoordinates.convertBoundingBox(boundingBox);
		Vector2 position = bounds.getMin();

		double xFactor = 1.0 / bounds.getWidth();
		double yFactor = 1.0 / bounds.getHeight();
		double factor = Math.min(xFactor, yFactor);

		Matrix33 S = Matrix33.makeScale(factor, factor);
		Matrix33 T1 = Matrix33.makeTranslate(-position.getX(), -position.getY());
		this.setViewMatrix(S.multiply(T1));
	}
}
