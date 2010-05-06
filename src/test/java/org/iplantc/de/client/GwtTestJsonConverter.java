package org.iplantc.de.client;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.utils.JsonConverter;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestJsonConverter extends GWTTestCase
{
	@Override
	public String getModuleName()
	{
		return "org.iplantc.de.discoveryenvironment";
	}

	public void testBuildDeleteFolderNullId()
	{
		String result = JsonConverter.buildDeleteFolderString(null);
		assertNull(result);
	}

	public void testBuildDeleteFolderEmptyId()
	{
		String result = JsonConverter.buildDeleteFolderString("");
		assertNull(result);
	}

	public void testBuildDeleteFolderValidId()
	{
		String result = JsonConverter.buildDeleteFolderString("6");
		assertNotNull(result);
		assert (result.equals("{'folderIds':['6']}"));
	}

	public void testBuildDeleteFileNullId()
	{
		String result = JsonConverter.buildDeleteFileString(null);
		assertNull(result);
	}

	public void testBuildDeleteFileEmptyId()
	{
		String result = JsonConverter.buildDeleteFileString("");
		assertNull(result);
	}

	public void testBuildDeleteFileValidId()
	{
		String result = JsonConverter.buildDeleteFileString("6");
		assertNotNull(result);
		assert (result.equals("{'fileIds':['6']}"));
	}

	public void testBuildDeleteStringNullLists()
	{
		String result = JsonConverter.buildDeleteString(null, null);
		assertNull(result);
	}

	public void testBuildDeleteStringNullFolders()
	{
		String result = JsonConverter.buildDeleteString(null, new ArrayList<String>());
		assertNull(result);
	}

	public void testBuildDeleteStringNullFiles()
	{
		String result = JsonConverter.buildDeleteString(new ArrayList<String>(), null);
		assertNull(result);
	}

	public void testBuildDeleteStringEmptyLists()
	{
		String result = JsonConverter
				.buildDeleteString(new ArrayList<String>(), new ArrayList<String>());
		assertNull(result);
	}

	public void testBuildDeleteStringMultipleFiles()
	{
		List<String> files = new ArrayList<String>();
		files.add("6");
		files.add("7");
		String result = JsonConverter.buildDeleteString(new ArrayList<String>(), files);
		assertNotNull(result);
		assert (result.equals("{'fileIds':['6', '7']}"));
	}

	public void testBuildDeleteStringMultipleFolders()
	{
		List<String> folders = new ArrayList<String>();
		folders.add("6");
		folders.add("7");
		String result = JsonConverter.buildDeleteString(folders, new ArrayList<String>());
		assertNotNull(result);
		assert (result.equals("{'folderIds':['6', '7']}"));
	}

	public void testBuildDeleteStringMultipleFilesOneFolder()
	{
		List<String> folders = new ArrayList<String>();
		folders.add("8");
		List<String> files = new ArrayList<String>();
		files.add("6");
		files.add("7");
		String result = JsonConverter.buildDeleteString(folders, files);
		assertNotNull(result);
		assert (result.equals("{'fileIds':['6', '7'], 'folderIds':['8']}"));
	}

	public void testBuildDeleteStringMultipleFoldersOneFile()
	{
		List<String> folders = new ArrayList<String>();
		folders.add("8");
		folders.add("9");
		List<String> files = new ArrayList<String>();
		files.add("6");
		String result = JsonConverter.buildDeleteString(folders, files);
		assertNotNull(result);
		assert (result.equals("{'fileIds':['6'], 'folderIds':['8', '9']}"));
	}

	public void testBuildDeleteStringMultipleFilesMultipleFolders()
	{
		List<String> folders = new ArrayList<String>();
		folders.add("8");
		folders.add("9");
		List<String> files = new ArrayList<String>();
		files.add("6");
		files.add("7");
		String result = JsonConverter.buildDeleteString(folders, files);
		assertNotNull(result);
		assert (result.equals("{'fileIds':['6', '7'], 'folderIds':['8', '9']}"));
	}
}
