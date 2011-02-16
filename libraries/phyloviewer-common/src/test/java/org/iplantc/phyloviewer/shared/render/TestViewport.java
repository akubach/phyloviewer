package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

public class TestViewport extends TestCase
{
	double delta = 10E-14;

	private Viewport createViewport(int x, int y, int width, int height)
	{
		Viewport viewport = new Viewport();

		viewport.setX(x);
		viewport.setY(y);
		viewport.setWidth(width);
		viewport.setHeight(height);

		return viewport;
	}

	@Test
	public void testCreateViewport()
	{
		int x = 0;
		int y = 0;
		int width = 100;
		int height = 200;

		Viewport viewport = createViewport(x, y, width, height);

		assertEquals(x, viewport.getX());
		assertEquals(y, viewport.getY());
		assertEquals(width, viewport.getWidth());
		assertEquals(height, viewport.getHeight());
	}

	@Test
	public void testComputeWindowMatrix()
	{
		int x = 0;
		int y = 0;
		int width = 100;
		int height = 200;
		
		Viewport viewport = createViewport(x, y, width, height);

		Matrix33 windowMatrix = viewport.computeWindowMatrix();
		Vector2 v0 = new Vector2(-1, -1);
		Vector2 v1 = new Vector2(1, 1);

		v0 = windowMatrix.transform(v0);
		v1 = windowMatrix.transform(v1);

		assertEquals(x, v0.getX(), delta);
		assertEquals(y, v0.getY(), delta);
		assertEquals(width, v1.getX(), delta);
		assertEquals(height, v1.getY(), delta);
	}

	@Test
	public void testComputeWindowMatrix_nonZeroXAndY()
	{
		int x = 10;
		int y = 20;
		int width = 100;
		int height = 200;
		
		Viewport viewport = createViewport(x, y, width, height);

		Matrix33 windowMatrix = viewport.computeWindowMatrix();
		Vector2 v0 = new Vector2(-1, -1);
		Vector2 v1 = new Vector2(1, 1);

		v0 = windowMatrix.transform(v0);
		v1 = windowMatrix.transform(v1);

		assertEquals(x, v0.getX(), delta);
		assertEquals(y, v0.getY(), delta);
		assertEquals(width + x, v1.getX(), delta);
		assertEquals(height + y, v1.getY(), delta);
	}
}
