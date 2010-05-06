package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a file being moved. 
 */
public class FileMovedEvent extends GwtEvent<FileMovedEventHandler>
{
	// ////////////////////////////////////////
	// private variables
	private String idFolder;
	private String idFile;

	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<FileMovedEventHandler> TYPE = new GwtEvent.Type<FileMovedEventHandler>();

	// ////////////////////////////////////////
	// constructor
	public FileMovedEvent(String idFolder, String idFile)
	{
		this.idFolder = idFolder;
		this.idFile = idFile;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(FileMovedEventHandler handler)
	{
		handler.onMoved(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<FileMovedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getFolderId()
	{
		return idFolder;
	}

	// ////////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}
}
