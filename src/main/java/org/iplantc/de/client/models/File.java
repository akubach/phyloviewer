package org.iplantc.de.client.models;

/**
 * Models the metadata related to a File. 
 */
public class File extends DiskResource
{
	private static final long serialVersionUID = 7502468326721721826L;

	public File(JsFile info)
	{
		super(info.getId(), info.getName());

		set("type", info.getType());
		set("uploaded", info.getUploaded());
	}

	public String getType()
	{
		return get("type");
	}

	public String getUploaded()
	{
		return get("uploaded");
	}

	@Override
	public String getStatus()
	{
		return getName() + " - " + getUploaded();
	}
}
