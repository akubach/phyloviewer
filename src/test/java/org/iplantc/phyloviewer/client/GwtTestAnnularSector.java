package org.iplantc.phyloviewer.client;

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
	public void testExpandBy() {
		
		double r0 = 1.0;
		double a0 = Math.PI / 4;
		AnnularSector sector = new AnnularSector(new PolarVector2(r0,a0));
		assertEquals(r0, sector.getMin().getRadius());
		assertEquals(r0, sector.getMax().getRadius());
		assertEquals(a0, sector.getMin().getAngle());
		assertEquals(a0, sector.getMax().getAngle());
		
		double r1 = 2.0;
		double a1 = Math.PI / 8;
		sector.expandBy(new PolarVector2(r1,a1));
		assertEquals(r0, sector.getMin().getRadius());
		assertEquals(r1, sector.getMax().getRadius());
		assertEquals(a1, sector.getMin().getAngle());
		assertEquals(a0, sector.getMax().getAngle());
		
		double r2 = 0.5;
		double a2 = Math.PI / 2;
		sector.expandBy(new PolarVector2(r2,a2));
		assertEquals(r2, sector.getMin().getRadius());
		assertEquals(r1, sector.getMax().getRadius());
		assertEquals(a1, sector.getMin().getAngle());
		assertEquals(a2, sector.getMax().getAngle());	
		
		double r3 = 11.0;
		double a3 = 3 * Math.PI / 2;
		sector.expandBy(new PolarVector2(r3,a3));
		assertEquals(r2, sector.getMin().getRadius());
		assertEquals(r3, sector.getMax().getRadius());
		assertEquals(a1, sector.getMin().getAngle());
		assertEquals(a3, sector.getMax().getAngle());	
		
		double r4 = 0.0;
		double a4 = 0.0;
		sector.expandBy(new PolarVector2(r4,a4));
		assertEquals(r4, sector.getMin().getRadius());
		assertEquals(r3, sector.getMax().getRadius());
		assertEquals(a4, sector.getMin().getAngle());
		assertEquals(a3, sector.getMax().getAngle());	
		
	}
	
	@Test public void testContains() {
		
	}

}
