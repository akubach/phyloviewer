package org.iplantc.phyloviewer.client;

import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestPolarVector2 extends GWTTestCase {
	static final double pi = Math.PI;

	@Override
	public String getModuleName() {
		return "org.iplantc.phyloviewer.Phyloviewer";
	}

	@Test
	public void testCartesianConstructor() {
		double delta = 10E-14;
		
		Vector2 v = new Vector2(0,0);
		PolarVector2 pv = new PolarVector2(v);
		assertEquals(0.0, pv.getRadius(), delta);
		assertEquals(0.0, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setX(1.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(0.0, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setY(1.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(pi / 4, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setX(0.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(pi / 2, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setX(-1.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(3 * pi / 4, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setY(0.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(pi, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setY(-1.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(5 * pi / 4, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setX(0.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(3 * pi / 2, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
		
		v.setX(1.0);
		pv = new PolarVector2(v);
		assertEquals(v.length(), pv.getRadius(), delta);
		assertEquals(7 * pi / 4, pv.getAngle(), delta);
		assertEquals(v.getX(), pv.getX(), delta);
		assertEquals(v.getY(), pv.getY(), delta);
		assertTrue(pv.isValid());
	}
}
