package org.iplantc.phyloviewer.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.iplantc.phyloviewer.server.render.Java2DGraphics;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Matrix33;
import org.iplantc.phyloviewer.shared.math.Vector2;
import org.iplantc.phyloviewer.shared.render.Camera;
import org.iplantc.phyloviewer.shared.render.CameraCladogram;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestJava2DGraphics
{
	double delta = 10E-14;

	@Test
	public void testAffineTransformFrom() throws NoninvertibleTransformException
	{
		// Tests creating an AffineTransform from a Matrix33. Also, compares results of Matrix33
		// transform to AffineTransform

		Matrix33 matrix = new Matrix33();
		AffineTransform transform = Java2DGraphics.affineTransformFrom(matrix);
		compareTransform(transform, matrix);
		compareTransform(transform.createInverse(), matrix.inverse());

		double x = 23;
		double y = -29;
		matrix = Matrix33.makeScale(x, y);
		compareTransform(AffineTransform.getScaleInstance(x, y), matrix);
		compareTransform(Java2DGraphics.affineTransformFrom(matrix), matrix);

		matrix = Matrix33.makeTranslate(x, y);
		compareTransform(AffineTransform.getTranslateInstance(x, y), matrix);
		compareTransform(Java2DGraphics.affineTransformFrom(matrix), matrix);

	}

	@Test
	public void testIsCulled()
	{
		int width = 100;
		int height = 100;

		Camera camera = new CameraCladogram();
		camera.setPannable(true, true);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		Java2DGraphics javaGraphics = new Java2DGraphics(graphics);
		javaGraphics.setSize(width, height);
		javaGraphics.setViewMatrix(camera.getMatrix(width, height));

		Box2D topBox = new Box2D(new Vector2(0, 0), new Vector2(1.0, 0.2));
		Box2D bottomBox = new Box2D(new Vector2(0, 0.6), new Vector2(1.0, 0.8));
		Box2D leftBox = new Box2D(new Vector2(0, 0), new Vector2(0.4, 1.0));
		assertFalse(javaGraphics.isCulled(topBox));
		assertFalse(javaGraphics.isCulled(bottomBox));
		assertFalse(javaGraphics.isCulled(leftBox));

		camera.pan(0, -0.5); // pan down
		javaGraphics.setViewMatrix(camera.getMatrix(width, height));
		assertTrue(javaGraphics.isCulled(topBox));
		assertFalse(javaGraphics.isCulled(bottomBox));
		assertFalse(javaGraphics.isCulled(leftBox));

		camera.reset();
		camera.zoom(0.5, 0.5, 1.0, 2.0); // reset and zoom 2x in y direction, keeping the center of the
											// image stationary
		javaGraphics.setViewMatrix(camera.getMatrix(width, height));
		assertTrue(javaGraphics.isCulled(topBox));
		assertFalse(javaGraphics.isCulled(bottomBox));
		assertFalse(javaGraphics.isCulled(leftBox));

		camera.pan(0, 0.5); // pan up
		javaGraphics.setViewMatrix(camera.getMatrix(width, height));
		assertFalse(javaGraphics.isCulled(topBox));
		assertTrue(javaGraphics.isCulled(bottomBox));
		assertFalse(javaGraphics.isCulled(leftBox));

		camera.pan(-0.5, 0); // pan right
		javaGraphics.setViewMatrix(camera.getMatrix(width, height));
		assertFalse(javaGraphics.isCulled(topBox));
		assertTrue(javaGraphics.isCulled(bottomBox));
		assertTrue(javaGraphics.isCulled(leftBox));
	}

	private void compareTransform(AffineTransform transform, Matrix33 matrix)
	{
		assertEquals(transform.getScaleX(), matrix.getScaleX(), Double.MIN_VALUE);
		assertEquals(transform.getScaleY(), matrix.getScaleY(), Double.MIN_VALUE);
		assertEquals(transform.getShearX(), matrix.getShearX(), Double.MIN_VALUE);
		assertEquals(transform.getShearY(), matrix.getShearY(), Double.MIN_VALUE);
		assertEquals(transform.getTranslateX(), matrix.getTranslationX(), Double.MIN_VALUE);
		assertEquals(transform.getTranslateY(), matrix.getTranslationY(), Double.MIN_VALUE);

		double x = -42;
		double y = 11;
		Vector2 mxPoint = matrix.transform(new Vector2(x, y));
		Point2D txPoint = transform.transform(new Point2D.Double(x, y), null);
		assertEquals(txPoint.getX(), mxPoint.getX(), Double.MIN_VALUE);
		assertEquals(txPoint.getY(), mxPoint.getY(), Double.MIN_VALUE);
	}

	@Test
	public void testGetColor()
	{
		Color color = Java2DGraphics.getColorFromHtmlString("#ffffff");
		assertEquals(255, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(255, color.getBlue());

		color = Java2DGraphics.getColorFromHtmlString("#ff0000");
		assertEquals(255, color.getRed());
		assertEquals(0, color.getGreen());
		assertEquals(0, color.getBlue());

		color = Java2DGraphics.getColorFromHtmlString("#00ff00");
		assertEquals(0, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(0, color.getBlue());
		
		color = Java2DGraphics.getColorFromHtmlString("#0000ff");
		assertEquals(0, color.getRed());
		assertEquals(0, color.getGreen());
		assertEquals(255, color.getBlue());
	}
}
