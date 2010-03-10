package org.iplantc.iptol.client.models;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public abstract class DiskResource extends BaseTreeModel 
{
	private static final long serialVersionUID = 557899228357342079L;
	
	protected DiskResource(String id,String name) 
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
	
	public abstract String getStatus();
}
