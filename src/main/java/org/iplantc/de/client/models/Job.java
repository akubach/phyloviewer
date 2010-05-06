package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Represents a job in JobPanelGrid
 * 
 * @author sriram 
 */
public class Job extends BaseModelData
{

	private static final long serialVersionUID = 1L;

	public Job(String id, String name, String created, String status)
	{
		set("id", id);
		set("name", name);
		set("created", created);
		set("status", status);
	}

	public String getId()
	{
		return this.get("id").toString();
	}

	public String getName()
	{
		return this.get("name").toString();
	}

	public String getCreated()
	{
		return this.get("created").toString();
	}

	public String getStatus()
	{
		return this.get("status").toString();
	}

}
