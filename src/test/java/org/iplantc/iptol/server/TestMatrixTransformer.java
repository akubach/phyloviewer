package org.iplantc.iptol.server;

import static org.junit.Assert.*;

import org.iplantc.exporttree.exception.TransformException;
import org.iplantc.treedata.model.Matrix;
import org.iplantc.treedata.model.Taxa;
import org.iplantc.treedata.model.Thing;
import org.junit.Test;

public class TestMatrixTransformer {

	private MatrixTransformer matrixTransformer = new MatrixTransformer();

	@Test
	public void testNullMatrix() {
		try {
			matrixTransformer.transform(null);
			fail("Expected exception");
		} catch (TransformException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testNoTaxons() {
		try {
			Matrix matrix = new Matrix();
			MatrixData matrixData = matrixTransformer.transform(matrix);
			assertEquals(0, matrixData.getHeaders().size());
			assertEquals(0, matrixData.getData().size());
		} catch (TransformException e) {
			fail("Could not transform");
		}
	}

	@Test
	public void testNoTraits() {
		try {
			Taxa taxa = new Taxa();
			Thing taxon1 = taxa.addTaxon("foo1");
			Thing taxon2 = taxa.addTaxon("foo2");

			Matrix matrix = new Matrix();
			matrix.addTaxon(taxon1);
			matrix.addTaxon(taxon2);

			MatrixData matrixData = matrixTransformer.transform(matrix);
			assertEquals(1, matrixData.getHeaders().size());
			assertEquals(2, matrixData.getData().size());
		} catch (TransformException e) {
			fail("Could not transform");
		}
	}

	@Test
	public void testWithNoData() {
		try {
			Taxa taxa = new Taxa();
			Thing taxon1 = taxa.addTaxon("foo1");
			Thing taxon2 = taxa.addTaxon("foo2");

			Matrix matrix = new Matrix();
			matrix.addTaxon(taxon1);
			matrix.addTaxon(taxon2);

			matrix.addCharacter("bar1");
			matrix.addCharacter("bar2");

			MatrixData matrixData = matrixTransformer.transform(matrix);
			assertEquals(3, matrixData.getHeaders().size());
			assertEquals(2, matrixData.getData().size());

			assertNull(matrixData.getData().get(0).getValues().get(1));
			assertNull(matrixData.getData().get(0).getValues().get(2));

			assertNull(matrixData.getData().get(1).getValues().get(1));
			assertNull(matrixData.getData().get(1).getValues().get(2));
		} catch (TransformException e) {
			fail("Could not transform");
		}
	}

	@Test
	public void testWithData() {
		try {
			Taxa taxa = new Taxa();
			Thing taxon1 = taxa.addTaxon("foo1");
			taxon1.setId(9L);
			Thing taxon2 = taxa.addTaxon("foo2");
			taxon2.setId(10L);

			Matrix matrix = new Matrix();
			matrix.addTaxon(taxon1);
			matrix.addTaxon(taxon2);

			Thing trait1 = matrix.addCharacter("bar1");
			trait1.setId(11L);
			Thing trait2 = matrix.addCharacter("bar2");
			trait2.setId(12L);

			matrix.addCharacterDatum(taxon1, trait1, 1.0);
			matrix.addCharacterDatum(taxon1, trait2, 2.0);
			matrix.addCharacterDatum(taxon2, trait1, 3.0);
			matrix.addCharacterDatum(taxon2, trait2, 4.0);

			MatrixData matrixData = matrixTransformer.transform(matrix);
			assertEquals(3, matrixData.getHeaders().size());
			assertEquals(2, matrixData.getData().size());

			assertEquals("Species", matrixData.getHeaders().get(0).getLabel());
			assertEquals(new Long(0L), matrixData.getHeaders().get(0).getId());

			assertEquals("bar1", matrixData.getHeaders().get(1).getLabel());
			assertEquals(new Long(11L), matrixData.getHeaders().get(1).getId());

			assertEquals("bar2", matrixData.getHeaders().get(2).getLabel());
			assertEquals(new Long(12L), matrixData.getHeaders().get(2).getId());

			assertEquals(new Long(9L), matrixData.getData().get(0).getId());
			assertEquals("foo1", matrixData.getData().get(0).getValues().get(0));
			assertEquals("1.0", matrixData.getData().get(0).getValues().get(1));
			assertEquals("2.0", matrixData.getData().get(0).getValues().get(2));

			assertEquals(new Long(10L), matrixData.getData().get(1).getId());
			assertEquals("foo2", matrixData.getData().get(1).getValues().get(0));
			assertEquals("3.0", matrixData.getData().get(1).getValues().get(1));
			assertEquals("4.0", matrixData.getData().get(1).getValues().get(2));
		} catch (TransformException e) {
			fail("Could not transform");
		}
	}
}
