package org.iplantc.de.server;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.treedata.info.FileInfo;

public class FolderInfo
{
	private String id;
	private String label;
	private List<FolderInfo> subfolders = new LinkedList<FolderInfo>();
	private List<FileInfo> files = new LinkedList<FileInfo>();

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}

	public void setSubfolders(List<FolderInfo> subfolders)
	{
		this.subfolders = subfolders;
	}

	public List<FolderInfo> getSubfolders()
	{
		return subfolders;
	}

	public void setFiles(List<FileInfo> files)
	{
		this.files = files;
	}

	public List<FileInfo> getFiles()
	{
		return files;
	}
}
