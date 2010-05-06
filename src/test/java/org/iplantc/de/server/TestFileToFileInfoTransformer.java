package org.iplantc.de.server;

import java.util.Date;

import junit.framework.TestCase;

import org.iplantc.treedata.info.FileInfo;
import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.FileStatus;
import org.iplantc.treedata.model.FileType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.transformer.TransformerException;

public class TestFileToFileInfoTransformer extends TestCase
{
	private FileType fileType;
	private File file;

	@Before
	public void setUp()
	{
		fileType = new FileType();
		fileType.setId(1L);
		fileType.setDescription("A great file!");

		file = new File();
		file.setId(650L);
		file.setName("simple.nex");
		file.setUploaded(new Date());
		file.setType(fileType);
		file.setStatus(FileStatus.COMPLETE);
	}

	@After
	public void tearDown()
	{
		file = null;
		fileType = null;
	}

	private static void fileRepresentationsMatch(File file, FileInfo fileInfo)
	{
		assertEquals(file.getId().toString(), fileInfo.getId());
		assertEquals(file.getName(), fileInfo.getName());
		assertEquals(file.getType().getDescription(), fileInfo.getType());
		assertTrue(file.getUploaded().toString().equals(fileInfo.getUploaded()));
		assertTrue(file.getStatus().name().equals(fileInfo.getStatus()));
	}

	@Test
	public void testFreshInstance()
	{
		File f = new File();
		try
		{
			FileInfo fileInfo = (FileInfo)new FileToFileInfoTransformer().transform(f);
			assertNotNull(fileInfo);
		}
		catch(TransformerException e)
		{
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}
	}

	@Test
	public void testSimpleInstance()
	{
		try
		{
			FileInfo fileInfo = (FileInfo)new FileToFileInfoTransformer().transform(file);
			assertNotNull(fileInfo);
			fileRepresentationsMatch(file, fileInfo);
		}
		catch(TransformerException e)
		{
			fail("Unexpected occurrence of TransformerException on input to transformer.");
		}
	}
}
