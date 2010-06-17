/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestBox2D extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "org.iplantc.phyloviewer.GWTPhyloViewer";
	}
	
	@Test
	public void testValid() {
		Box2D invalid=new Box2D();
		assertFalse(invalid.valid());
		
		Box2D valid=new Box2D(new Vector2(0,0), new Vector2(1,1));
		assertTrue(valid.valid());
	}

	@Test
	public void testExpandByVector2() {
		Box2D box=new Box2D();
		box.expandBy(new Vector2(0,0));
		box.expandBy(new Vector2(1,1));
		
		assertTrue(0==box.getMin().getX());
		assertTrue(0==box.getMin().getY());
		
		assertTrue(1==box.getMax().getX());
		assertTrue(1==box.getMax().getY());
	}

	@Test
	public void testExpandByBox2D() {
		Box2D box0=new Box2D(new Vector2(0,0), new Vector2(1,1));
		Box2D box1=new Box2D(new Vector2(0,0), new Vector2(2,2));
		
		box0.expandBy(box1);
		
		assertTrue(0==box0.getMin().getX());
		assertTrue(0==box0.getMin().getY());
		
		assertTrue(2==box0.getMax().getX());
		assertTrue(2==box0.getMax().getY());
	}

	@Test
	public void testIntersects() {
		Box2D box0=new Box2D(new Vector2(0,0), new Vector2(1,1));
		Box2D box1=new Box2D(new Vector2(0.25,0.25), new Vector2(0.5,0.5));
		Box2D box2=new Box2D(new Vector2(1.25,1.25), new Vector2(1.5,1.5));
		
		assertTrue(box0.intersects(box1));
		assertFalse(box0.intersects(box2));
	}
	
	@Test
	public void testContains() {
		Box2D box=new Box2D(new Vector2(0,0),new Vector2(10,10));
		
		assertFalse(box.contains(new Vector2(-5,-5)));
		assertTrue(box.contains(new Vector2(5,5)));
		assertTrue(box.contains(new Vector2(0,10)));
		assertFalse(box.contains(new Vector2(20,20)));
	}

}
