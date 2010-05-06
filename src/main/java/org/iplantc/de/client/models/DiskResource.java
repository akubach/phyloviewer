package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * Models the metadata related to a resource stored on disk (either a file or folder).  
 */
public abstract class DiskResource extends BaseTreeModel
{
	private static final long serialVersionUID = 557899228357342079L;

	// ////////////////////////////////////////
	// constructor
	protected DiskResource(String id, String name)
	{
		setId(id);
		setName(name);
	}

	// ////////////////////////////////////////
	// public methods
	public void setId(String id)
	{
		set("id", id);
	}

	// ////////////////////////////////////////
	public String getId()
	{
		return get("id");
	}

	// ////////////////////////////////////////
	public void setName(String name)
	{
		set("name", name);
	}

	// ////////////////////////////////////////
	public String getName()
	{
		return get("name");
	}

	// ////////////////////////////////////////
	public String toString()
	{
		return getName();
	}

	// ////////////////////////////////////////
	public abstract String getStatus();
}
