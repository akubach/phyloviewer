package org.iplantc.de.server;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.iplantc.treedata.info.FileInfo;
import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.FileType;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.transformer.TransformerException;

public class TestExtractFileInfoTransformer extends TestCase {

	private static FileType fileType;

	public static File create(Long id, String name, Date uploaded, FileType type) {
		File f = new File();
		f.setId(id);
		f.setName(name);
		f.setUploaded(uploaded);
		f.setType(type);
		return f;
	}

	@Before
	public void setUp() {
		fileType = new FileType();
		fileType.setId(1L);
		fileType.setDescription("A great file!");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmptySource() {
		Collection<File> collection = new LinkedList<File>();
		try {
			List<FileInfo> fileInfos =
				(List<FileInfo>)new ExtractFileInfoTransformer().transform(collection);
			assertNotNull(fileInfos);
			assertTrue(fileInfos.size() == 0);
			assertTrue(fileInfos.isEmpty());
		} catch (TransformerException e) {
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSingleFile() {
		try {
			List<FileInfo> fileInfos =
				(List<FileInfo>) new ExtractFileInfoTransformer().transform(
						create(Long.valueOf(650), "foo.ndy", new Date(), fileType));
			assertNotNull(fileInfos);
			assertTrue(fileInfos.size() == 1);
			assertFalse(fileInfos.isEmpty());
			FileInfo fi = fileInfos.get(0);
			assertEquals("650", fi.getId());
		} catch (TransformerException e) {
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSeveralFiles() {
		Collection<File> collection = new LinkedList<File>();
		collection.add(create(Long.valueOf(650), "qux.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(651), "baz.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(652), "bar.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(653), "foo.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(654), "goo.ndy", new Date(), fileType));
		try {
			List<FileInfo> fileInfos =
				(List<FileInfo>) new ExtractFileInfoTransformer().transform(collection);
			assertNotNull(fileInfos);
			assertTrue(fileInfos.size() == 5);
			assertFalse(fileInfos.isEmpty());
			for (FileInfo fi : fileInfos) {
				assertNotNull(fi.getId());
				assertNotNull(fi.getName());
				assertNotNull(fi.getUploaded());
				assertEquals("A great file!", fi.getType());
			}
		} catch (TransformerException e) {
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFileWithNulls() {
		// test with null date object
		Collection<File> collection = new LinkedList<File>();
		collection.add(create(Long.valueOf(650), "qux.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(651), "baz.ndy", null, fileType));
		collection.add(create(Long.valueOf(652), "bar.ndy", new Date(), fileType));
		try {
			List<FileInfo> fileInfos =
				(List<FileInfo>) new ExtractFileInfoTransformer().transform(collection);
			assertNotNull(fileInfos);
			assertTrue(fileInfos.size() == 3);
			assertFalse(fileInfos.isEmpty());
		} catch (TransformerException e) {
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}

		collection = new LinkedList<File>();
		collection.add(create(Long.valueOf(650), "qux.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(651), "baz.ndy", new Date(), fileType));
		collection.add(create(Long.valueOf(652), "bar.ndy", new Date(), null));
		try {
			List<FileInfo> fileInfos =
				(List<FileInfo>) new ExtractFileInfoTransformer().transform(collection);
			assertNotNull(fileInfos);
			assertTrue(fileInfos.size() == 3);
			assertFalse(fileInfos.isEmpty());
		} catch (TransformerException e) {
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}
	}

	@Test
	public void testInvalidInput() {
		try {
			new ExtractFileInfoTransformer().transform(new Object());
			fail("Expected org.mule.api.transformer.TransformerException on null input to transformer");
		} catch (TransformerException e) {
			// This is what we expect to happen.
		}
	}
}
