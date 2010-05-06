package org.iplantc.de.client.services;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Defines an asynchronous callback for DiskResource (a file or folder) rename event. 
 */
public class DiskResourceRenameCallback implements AsyncCallback<String>
{
	// ////////////////////////////////////////
	// private variables
	private DiskResourceRenamedEvent.ResourceType type;
	private String id;
	private String name;

	// ////////////////////////////////////////
	// constructor
	public DiskResourceRenameCallback(DiskResourceRenamedEvent.ResourceType type, String id, String name)
	{
		this.type = type;
		this.id = id;
		this.name = name;
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public void onFailure(Throwable arg0)
	{
		DEErrorStrings errorStrings = (DEErrorStrings)GWT.create(DEErrorStrings.class);
		ErrorHandler.post(errorStrings.renameFileFailed());
	}

	// ////////////////////////////////////////
	@Override
	public void onSuccess(String arg0)
	{
		EventBus eventbus = EventBus.getInstance();
		DiskResourceRenamedEvent event = new DiskResourceRenamedEvent(type, id, name);
		eventbus.fireEvent(event);
	}
}
