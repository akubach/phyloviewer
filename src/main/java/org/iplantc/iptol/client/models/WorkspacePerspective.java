package org.iplantc.iptol.client.models;

import com.extjs.gxt.ui.client.data.BaseModel;

public class WorkspacePerspective extends BaseModel
{
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////
	//constructors
	public WorkspacePerspective(String id,String name) 
	{
		setId(id);
		setName(name);
	}
	
	//////////////////////////////////////////
	public WorkspacePerspective(WorkspacePerspectiveInfo info) 
	{
		setId(info.getId());
		setName(info.getName());
	}
	
	//////////////////////////////////////////
	//public methods
	public void setId(String id)
	{
		set("id",id);
	}
	
	//////////////////////////////////////////
	public String getId() 
	{
		return get("id");
	}

	//////////////////////////////////////////
	public void setName(String name)
	{
		set("name",name);
	}
	
	//////////////////////////////////////////
	public String getName() 
	{
		return get("name");
	}
}
