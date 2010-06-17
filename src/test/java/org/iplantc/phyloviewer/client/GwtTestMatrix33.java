/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client;

import org.iplantc.phyloviewer.client.tree.viewer.math.Matrix33;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestMatrix33 extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "org.iplantc.phyloviewer.GWTPhyloViewer";
	}
	
	@Test 
	public void testDefaultConstruction() {
		Matrix33 matrix = new Matrix33();
		assertTrue(1==matrix.get(0,0));	assertTrue(0==matrix.get(0,1));	assertTrue(0==matrix.get(0,2));
		assertTrue(0==matrix.get(1,0));	assertTrue(1==matrix.get(1,1));	assertTrue(0==matrix.get(1,2));
		assertTrue(0==matrix.get(2,0));	assertTrue(0==matrix.get(2,1));	assertTrue(1==matrix.get(2,2));
	}
	
	@Test
	public void testSet() {
		Matrix33 matrix = new Matrix33();
		matrix.set(1,2,3,4,5,6,7,8,9);
		assertTrue(1==matrix.get(0,0));
		assertEquals(2.0,matrix.get(0,1),0.01);
		assertTrue(3==matrix.get(0,2));
		assertTrue(4==matrix.get(1,0));	assertTrue(5==matrix.get(1,1));	assertTrue(6==matrix.get(1,2));
		assertTrue(7==matrix.get(2,0));	assertTrue(8==matrix.get(2,1));	assertTrue(9==matrix.get(2,2));
	}

	@Test
	public void testTransformWithIdentity() {
		Matrix33 matrix= new Matrix33();
		Vector2 vector = new Vector2(3,4);
		Vector2 v = matrix.transform(vector);
		assertTrue(3==v.getX());
		assertTrue(4==v.getY());
	}
	
	@Test
	public void testTransform() {
		Matrix33 matrix=Matrix33.makeTranslate(2, 5);
		Vector2 vector = new Vector2(3,4);
		Vector2 v = matrix.transform(vector);
		assertTrue(5==v.getX());
		assertTrue(9==v.getY());
	}

	@Test
	public void testMultiply() {
		Matrix33 S0=Matrix33.makeScale(5, 10);
		Matrix33 S1=Matrix33.makeScale(5, 10);
		
		Matrix33 result0=S0.multiply(S1);
		assertTrue(25==result0.get(0,0));
		assertTrue(100==result0.get(1,1));
	}

	@Test
	public void testMakeTranslate() {
		Matrix33 matrix=Matrix33.makeTranslate(5, 10);
		assertTrue(5==matrix.getTranslationX());
		assertTrue(10==matrix.getTranslationY());
	}

	@Test
	public void testMakeScale() {
		Matrix33 matrix=Matrix33.makeScale(5, 10);
		assertTrue(5==matrix.get(0,0));
		assertTrue(10==matrix.get(1,1));
	}

	@Test
	public void testInverse() {
		Matrix33 matrix=Matrix33.makeTranslate(3, 4).multiply(Matrix33.makeScale(10,3));
		Matrix33 IM=matrix.inverse();
		Matrix33 R=matrix.multiply(IM);
		
		assertTrue(1==R.get(0,0));	assertTrue(0==R.get(0,1));	assertTrue(0==R.get(0,2));
		assertTrue(0==R.get(1,0));	assertTrue(1==R.get(1,1));	assertTrue(0==R.get(1,2));
		assertTrue(0==R.get(2,0));	assertTrue(0==R.get(2,1));	assertTrue(1==R.get(2,2));
	}
}
