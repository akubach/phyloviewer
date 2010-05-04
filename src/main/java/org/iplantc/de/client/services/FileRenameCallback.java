package org.iplantc.de.client.services;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileRenameCallback implements AsyncCallback<String>
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//constructor
	public FileRenameCallback(String id,String name)
	{
		this.id = id;
		this.name = name;
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);
		ErrorHandler.post(errorStrings.renameFileFailed());	
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String arg0) 
	{
		EventBus eventbus = EventBus.getInstance();
		FileRenamedEvent event = new FileRenamedEvent(id,name);
		eventbus.fireEvent(event);
	}
}
