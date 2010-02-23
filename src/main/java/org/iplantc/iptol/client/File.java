package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.io.Serializable;

public class File extends BaseTreeModel implements Serializable 
{
	private static final long serialVersionUID = 1L;
		
	private FileInfo info;
	  
	public File() 
	{
		set("id",-1);
	}

	public File(String id,String name) 
	{
		set("id",id);
		set("name", name);
	}

	public String getId() 
	{
		return get("id");
	}

	public String getName() 
	{
		return get("name");
	}

	public String toString() 
	{
		return getName();
	}

	public void setInfo(FileInfo info) 
	{
		this.info = info;
	}

	public FileInfo getInfo() 
	{
		return info;
	}
}
