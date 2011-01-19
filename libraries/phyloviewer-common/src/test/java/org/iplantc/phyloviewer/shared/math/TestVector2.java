package org.iplantc.phyloviewer.shared.math;

import org.junit.Test;

import junit.framework.TestCase;

public class TestVector2 extends TestCase {

	double delta = 10E-14;
	
	@Test
	public void testSubtract() {
		Vector2 v0 = new Vector2(1.0,1.0);
		Vector2 v1 = new Vector2(0.5,0.5);
		
		Vector2 answer = v0.subtract(v1);
		
		assertEquals(answer.getX(), 0.5, delta);
		assertEquals(answer.getY(), 0.5, delta);
	}
	
	@Test
	public void testAdd() {
		Vector2 v0 = new Vector2(1.0,1.0);
		Vector2 v1 = new Vector2(0.5,0.5);
		
		Vector2 answer = v0.add(v1);
		
		assertEquals(answer.getX(), 1.5, delta);
		assertEquals(answer.getY(), 1.5, delta);
	}
	
	@Test
	public void testRotate() {
		Vector2 v0 = new Vector2(1.0,0.0);
		Vector2 answer = v0.rotate(Math.PI / 2.0);
		
		assertEquals(0.0, answer.getX(), delta);
		assertEquals(1.0, answer.getY(), delta);
	}
	
	@Test
	public void testDistance() {
		Vector2 v0 = new Vector2(0.0,0.0);
		Vector2 v1 = new Vector2(1.0,1.0);
		
		assertEquals(Math.sqrt(2.0),v0.distance(v1),delta);
		assertEquals(Math.sqrt(2.0),v1.distance(v0),delta);
		
		Vector2 v2 = new Vector2(2.0,3.0);
		assertEquals(Math.sqrt(5.0),v1.distance(v2),delta);
		assertEquals(Math.sqrt(5.0),v2.distance(v1),delta);
	}
	
	@Test
	public void testDistanceSquared() {
		Vector2 v0 = new Vector2(0.0,0.0);
		Vector2 v1 = new Vector2(1.0,1.0);
		
		assertEquals(2.0,v0.distanceSquared(v1),delta);
		assertEquals(2.0,v1.distanceSquared(v0),delta);
		
		Vector2 v2 = new Vector2(2.0,3.0);
		assertEquals(5.0,v1.distanceSquared(v2),delta);
		assertEquals(5.0,v2.distanceSquared(v1),delta);
	}
}
