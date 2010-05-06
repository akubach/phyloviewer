package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class Trait extends BaseModelData
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Trait(String id, String filename, String uploaded)
	{
		set("id", id);
		set("filename", filename);
		set("uploaded", uploaded);
	}

	public String getFilename()
	{
		return get("filename").toString();
	}

	public String getUploaded()
	{
		return get("uploded").toString();
	}

	public String getId()
	{
		return get("id").toString();
	}
}
