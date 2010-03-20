package org.iplantc.iptol.client.models;

import org.iplantc.iptol.client.IptolDisplayStrings;

import com.google.gwt.core.client.GWT;

public class Folder extends DiskResource 
{
	private static final long serialVersionUID = 2525604102944798997L;
	
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
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
