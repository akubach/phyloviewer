package org.iplantc.de.client.models;

import org.iplantc.de.client.DEDisplayStrings;

import com.google.gwt.core.client.GWT;

public class Folder extends DiskResource 
{
	private static final long serialVersionUID = 2525604102944798997L;
	
	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	public Folder(String id,String name) 
	{
		super(id,name);
	}

	public Folder(String id,String name,DiskResource[] children) 
	{
		this(id,name);
	    
		for (int i = 0; i < children.length; i++) 
		{
			add(children[i]);
		}
	}

	@Override
	public String getStatus()
	{
		return getChildren().size() + " " + displayStrings.files();
	}	
}
