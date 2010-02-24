package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.FileType;
import org.iplantc.treedata.model.Matrix;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.transformer.TransformerException;

public class TestExtractMatrixInfoTransformer extends TestCase {

	private ExtractMatrixInfoTransformer transformer;
	private File file;
	private Matrix matrix1, matrix2;

	@Before
	public void setUp() {
		file = createFile();
		transformer = new ExtractMatrixInfoTransformer();

		matrix1 = new Matrix();
		matrix1.setId(2L);
		matrix1.setFile(file);

		matrix2 = new Matrix();
		matrix2.setId(3L);
		matrix2.setFile(file);
	}

	private File createFile() {
		File f = new File();
		f.setId(99L);
		f.setName("foo.nex");
		f.setUploaded(new Date());
		f.setType(createFileType());
		return f;
	}

	private FileType createFileType() {
		FileType fileType = new FileType();
		fileType.setId(1L);
		fileType.setDescription("A great file!");
		return fileType;
	}

	@Test
	public void testInvalidInput() {
		try {
		    transformer.doTransform(null, null);
		    fail("Did not get expected exception");
		}
		catch (TransformerException e) {
			assertTrue(true);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testListOfMatrices() {
		try {
			List<Matrix> matrices = new ArrayList<Matrix>();
			matrices.add(matrix1);
			matrices.add(matrix2);

			List<MatrixInfo> infoList = (List<MatrixInfo>) transformer.doTransform(matrices, null);

			assertEquals(2, infoList.size());

			if (infoList.get(0).getId().equals("2")) {
				assertEquals(file.getName(), infoList.get(0).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(0).getUploaded());
				assertEquals("2", infoList.get(0).getId());

				assertEquals(file.getName(), infoList.get(1).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(1).getUploaded());
				assertEquals("3", infoList.get(1).getId());
			}
			else {
				assertEquals(file.getName(), infoList.get(0).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(0).getUploaded());
				assertEquals("3", infoList.get(0).getId());

				assertEquals(file.getName(), infoList.get(1).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(1).getUploaded());
				assertEquals("2", infoList.get(1).getId());
			}
		}
		catch (TransformerException e) {
			fail("Exception was thrown");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFileWithMatrices() {
		try {
			file.addMatrix(matrix1);
			file.addMatrix(matrix2);

			List<MatrixInfo> infoList = (List<MatrixInfo>) transformer.doTransform(file, null);

			assertEquals(2, infoList.size());

			if (infoList.get(0).getId().equals("2")) {
				assertEquals(file.getName(), infoList.get(0).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(0).getUploaded());
				assertEquals("2", infoList.get(0).getId());

				assertEquals(file.getName(), infoList.get(1).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(1).getUploaded());
				assertEquals("3", infoList.get(1).getId());
			}
			else {
				assertEquals(file.getName(), infoList.get(0).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(0).getUploaded());
				assertEquals("3", infoList.get(0).getId());

				assertEquals(file.getName(), infoList.get(1).getFilename());
				assertEquals(file.getUploaded().toString(), infoList.get(1).getUploaded());
				assertEquals("2", infoList.get(1).getId());
			}
		}
		catch (TransformerException e) {
			fail("Exception was thrown");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFileWithNoMatrices() {
		try {
			List<MatrixInfo> infoList = (List<MatrixInfo>) transformer.doTransform(file, null);
			assertEquals(0, infoList.size());
		}
		catch (TransformerException e) {
			fail("Exception was thrown");
		}
	}
}
