package org.iplantc.iptol.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestJsonBuilder extends GWTTestCase 
{
	@Override
	public String getModuleName() 
	{
		return "org.iplantc.iptol.discoveryenvironment";
	}

	public void testBuildDeleteFolderNullId() 
	{
		String result = JsonBuilder.buildDeleteFolderString(null);
		assertNull(result);		
	}
	
	public void testBuildDeleteFolderEmptyId() 
	{
		String result = JsonBuilder.buildDeleteFolderString("");
		assertNull(result);		
	}
	
	public void testBuildDeleteFolderValidId() 
	{
		String result = JsonBuilder.buildDeleteFolderString("6");
		assertNotNull(result);
		assert(result.equals("{'folderIds':['6']}"));
	}
	
	public void testBuildDeleteFileNullId() 
	{
		String result = JsonBuilder.buildDeleteFileString(null);
		assertNull(result);		
	}
	
	public void testBuildDeleteFileEmptyId() 
	{
		String result = JsonBuilder.buildDeleteFileString("");
		assertNull(result);		
	}
	
	public void testBuildDeleteFileValidId() 
	{
		String result = JsonBuilder.buildDeleteFileString("6");
		assertNotNull(result);
		assert(result.equals("{'fileIds':['6']}"));
	}
	
	public void testBuildDeleteStringNullLists()
	{
		String result = JsonBuilder.buildDeleteString(null,null);
		assertNull(result);
	}
	
	public void testBuildDeleteStringNullFolders()
	{
		String result = JsonBuilder.buildDeleteString(null,new ArrayList<String>());
		assertNull(result);
	}
	
	public void testBuildDeleteStringNullFiles()
	{
		String result = JsonBuilder.buildDeleteString(new ArrayList<String>(),null);
		assertNull(result);
	}
	
	public void testBuildDeleteStringEmptyLists()
	{
		String result = JsonBuilder.buildDeleteString(new ArrayList<String>(),new ArrayList<String>());
		assertNull(result);
	}

	public void testBuildDeleteStringMultipleFiles()
	{
		List<String> files = new ArrayList<String>();
		files.add("6");
		files.add("7");
		String result = JsonBuilder.buildDeleteString(new ArrayList<String>(),files);
		assertNotNull(result);		
		assert(result.equals("{'fileIds':['6', '7']}"));
	}
	
	public void testBuildDeleteStringMultipleFolders()
	{
		List<String> folders = new ArrayList<String>();
		folders.add("6");
		folders.add("7");
		String result = JsonBuilder.buildDeleteString(folders,new ArrayList<String>());
		assertNotNull(result);		
		assert(result.equals("{'folderIds':['6', '7']}"));
	}
	
	public void testBuildDeleteStringMultipleFilesOneFolder()
	{		
		List<String> folders = new ArrayList<String>();
		folders.add("8");
		List<String> files = new ArrayList<String>();
		files.add("6");
		files.add("7");
		String result = JsonBuilder.buildDeleteString(folders,files);
		assertNotNull(result);		
		assert(result.equals("{'fileIds':['6', '7'], 'folderIds':['8']}"));
	}
	
	public void testBuildDeleteStringMultipleFoldersOneFile()
	{		
		List<String> folders = new ArrayList<String>();
		folders.add("8");
		folders.add("9");
		List<String> files = new ArrayList<String>();
		files.add("6");		
		String result = JsonBuilder.buildDeleteString(folders,files);
		assertNotNull(result);		
		assert(result.equals("{'fileIds':['6'], 'folderIds':['8', '9']}"));
	}
	
	public void testBuildDeleteStringMultipleFilesMultipleFolders()
	{		
		List<String> folders = new ArrayList<String>();
		folders.add("8");
		folders.add("9");
		List<String> files = new ArrayList<String>();
		files.add("6");
		files.add("7");
		String result = JsonBuilder.buildDeleteString(folders,files);
		assertNotNull(result);		
		assert(result.equals("{'fileIds':['6', '7'], 'folderIds':['8', '9']}"));
	}
}
