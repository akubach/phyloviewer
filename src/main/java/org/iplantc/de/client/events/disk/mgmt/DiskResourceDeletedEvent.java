package org.iplantc.de.client.events.disk.mgmt;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a disk resource (a file or folder) be deleted. 
 */
public class DiskResourceDeletedEvent extends GwtEvent<DiskResourceDeletedEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<DiskResourceDeletedEventHandler> TYPE = new GwtEvent.Type<DiskResourceDeletedEventHandler>();

	// ////////////////////////////////////////
	// private variables
	private List<String> folders = new ArrayList<String>();
	private List<String> files = new ArrayList<String>();

	// ////////////////////////////////////////
	// constructor
	public DiskResourceDeletedEvent(List<String> folders, List<String> files)
	{
		if(folders != null)
		{
			this.folders = folders;
		}

		if(files != null)
		{
			this.files = files;
		}
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(DiskResourceDeletedEventHandler handler)
	{
		handler.onDeleted(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<DiskResourceDeletedEventHandler> getAssociatedType()
	{
		// TODO Auto-generated method stub
		return TYPE;
	}

	// ////////////////////////////////////////
	public List<String> getFolders()
	{
		return folders;
	}

	// ////////////////////////////////////////
	public List<String> getFiles()
	{
		return files;
	}
}
