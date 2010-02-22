package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.io.Serializable;

public class Folder extends File implements Serializable 
{
	/**
	 * Folder model
	 */
	private static final long serialVersionUID = 1L;
		  
	public Folder() 
	{
		super();
	}

	public Folder(String id,String name) 
	{
		super(id,name);
	}

	public Folder(String id,String name,BaseTreeModel[] children) 
	{
		this(id,name);
	    
		for (int i = 0; i < children.length; i++) 
		{
			add(children[i]);
		}
	}
}
