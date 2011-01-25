package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

public class TestLine extends TestCase
{
	double delta = 10E-14;
	
	@Test
	public void testInterect()
	{
		Vector2 vertices[] = new Vector2[2];
		vertices[0] = new Vector2(0,0);
		vertices[1] = new Vector2(0,1);
		
		Line line = new Line(vertices);

		double distance = 0.1;
		
		Vector2 test = new Vector2(0,0.0099);
		
		assertTrue(line.intersect(test, distance * distance));
	}
	
	@Test
	public void testDistanceSquared()
	{
		Vector2 start = new Vector2(0,0);
		Vector2 end = new Vector2(0,1);
		
		Vector2 p0 = new Vector2(0,0);
		Vector2 p1 = new Vector2(1,0);
		
		assertEquals(0.0, Line.distanceSquared(start, end, p0), delta);
		assertEquals(1.0, Line.distanceSquared(start, end, p1), delta);
	}
}
