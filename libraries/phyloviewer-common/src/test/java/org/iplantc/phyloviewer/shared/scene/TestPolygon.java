package org.iplantc.phyloviewer.shared.scene;

import org.iplantc.phyloviewer.shared.math.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

public class TestPolygon extends TestCase
{
	@Test
	public void testInterect()
	{
		Vector2 v0 = new Vector2(0.0,0.0);
		Vector2 v1 = new Vector2(0.5,1.0);
		Vector2 v2 = new Vector2(1.0,0.0);
		
		Polygon triangle = Polygon.createTriangle(v0, v1, v2);
		
		assertTrue(triangle.intersect(new Vector2(0.5,0.5), 0));
		assertFalse(triangle.intersect(new Vector2(1.5,0.5), 0));
		assertFalse(triangle.intersect(new Vector2(0.5,-0.5), 0));
	}
}
