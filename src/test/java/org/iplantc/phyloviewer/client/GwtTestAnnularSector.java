package org.iplantc.phyloviewer.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.iplantc.phyloviewer.client.tree.viewer.math.AnnularSector;
import org.iplantc.phyloviewer.client.tree.viewer.math.PolarVector2;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestAnnularSector extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "org.iplantc.phyloviewer.GWTPhyloViewer";
	}
	
	@Test
	public void testExpandByPoint() {
		
		double r0 = 1.0;
		double a0 = Math.PI / 4;
		AnnularSector sector = new AnnularSector(new PolarVector2(r0,a0));
		assertEquals(r0, sector.getMin().getRadius());
		assertEquals(r0, sector.getMax().getRadius());
		assertEquals(a0, sector.getMin().getAngle());
		assertEquals(a0, sector.getMax().getAngle());
		assertTrue(sector.isValid());
		
		double r1 = 2.0;
		double a1 = Math.PI / 8;
		sector.expandBy(new PolarVector2(r1,a1));
		assertEquals(r0, sector.getMin().getRadius());
		assertEquals(r1, sector.getMax().getRadius());
		assertEquals(a1, sector.getMin().getAngle());
		assertEquals(a0, sector.getMax().getAngle());
		assertTrue(sector.isValid());
		
		double r2 = 0.5;
		double a2 = Math.PI / 2;
		sector.expandBy(new PolarVector2(r2,a2));
		assertEquals(r2, sector.getMin().getRadius());
		assertEquals(r1, sector.getMax().getRadius());
		assertEquals(a1, sector.getMin().getAngle());
		assertEquals(a2, sector.getMax().getAngle());	
		assertTrue(sector.isValid());
		
		double r3 = 11.0;
		double a3 = 3 * Math.PI / 2;
		sector.expandBy(new PolarVector2(r3,a3));
		assertEquals(r2, sector.getMin().getRadius());
		assertEquals(r3, sector.getMax().getRadius());
		assertEquals(a1, sector.getMin().getAngle());
		assertEquals(a3, sector.getMax().getAngle());	
		assertTrue(sector.isValid());
		
		double r4 = 0.0;
		double a4 = 0.0;
		sector.expandBy(new PolarVector2(r4,a4));
		assertEquals(r4, sector.getMin().getRadius());
		assertEquals(r3, sector.getMax().getRadius());
		assertEquals(a4, sector.getMin().getAngle());
		assertEquals(a3, sector.getMax().getAngle());	
		assertTrue(sector.isValid());
		
	}
	
	@Test 
	public void testContains() {
		AnnularSector sector = new AnnularSector(new PolarVector2(1.0,Math.PI / 4));
		
		ArrayList<PolarVector2> pointsOutside = new ArrayList<PolarVector2>();
		pointsOutside.add(new PolarVector2(2.0,Math.PI / 8));
		pointsOutside.add(new PolarVector2(0.5,Math.PI / 2));
		pointsOutside.add(new PolarVector2(11.0,3 * Math.PI / 2));
		pointsOutside.add(new PolarVector2(0.0,0.0));
		
		ArrayList<PolarVector2> pointsInside = new ArrayList<PolarVector2>();
		
		Iterator<PolarVector2> iterator = pointsOutside.iterator();
		while (iterator.hasNext()) {
			PolarVector2 point = iterator.next();
			sector.expandBy(point);
			pointsInside.add(point);
			iterator.remove();
			
			for (PolarVector2 outside : pointsOutside) {
				assertFalse(sector.contains(outside));
			}
			
			for (PolarVector2 inside : pointsInside) {
				assertTrue(sector.contains(inside));
			}
		}
	}
}
