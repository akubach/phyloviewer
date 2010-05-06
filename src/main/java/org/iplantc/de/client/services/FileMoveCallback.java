package org.iplantc.de.client.services;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.FileMovedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Defines an asynchronous callback for file move events. 
 */
public class FileMoveCallback implements AsyncCallback<String>
{
	// ////////////////////////////////////////
	// private variables
	private String idFolder;
	private String idFile;

	// ////////////////////////////////////////
	// constructor
	public FileMoveCallback(String idFolder, String idFile)
	{
		this.idFolder = idFolder;
		this.idFile = idFile;
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public void onFailure(Throwable arg0)
	{
		DEErrorStrings errorStrings = (DEErrorStrings)GWT.create(DEErrorStrings.class);
		ErrorHandler.post(errorStrings.moveFileFailed());
	}

	// ////////////////////////////////////////
	@Override
	public void onSuccess(String arg0)
	{
		EventBus eventbus = EventBus.getInstance();
		FileMovedEvent event = new FileMovedEvent(idFolder, idFile);
		eventbus.fireEvent(event);
	}
}
