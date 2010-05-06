package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a disk resource (a file or folder) being renamed.  
 */
public class DiskResourceRenamedEvent extends GwtEvent<DiskResourceRenamedEventHandler>
{
	/**
	 * Indicates if the resource is a file or folder.
	 */
	public enum ResourceType
	{
		FOLDER, FILE
	}

	// ////////////////////////////////////////
	// private variables
	private String id;
	private String name;
	private ResourceType type;

	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<DiskResourceRenamedEventHandler> TYPE = new GwtEvent.Type<DiskResourceRenamedEventHandler>();

	// ////////////////////////////////////////
	// constructor
	public DiskResourceRenamedEvent(ResourceType type, String id, String name)
	{
		this.type = type;
		this.id = id;
		this.name = name;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(DiskResourceRenamedEventHandler handler)
	{
		switch (type)
		{
		case FOLDER:
			handler.onFolderRenamed(this);
			break;

		case FILE:
			handler.onFileRenamed(this);
			break;

		default:
			break;
		}
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<DiskResourceRenamedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getId()
	{
		return id;
	}

	// ////////////////////////////////////////
	public String getName()
	{
		return name;
	}
}
