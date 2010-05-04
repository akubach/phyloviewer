package org.iplantc.de.client.services;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.FolderRenamedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderRenameCallback implements AsyncCallback<String>
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//constructor
	public FolderRenameCallback(String id,String name)
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
		ErrorHandler.post(errorStrings.renameFolderFailed());	
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String arg0) 
	{
		EventBus eventbus = EventBus.getInstance();
		FolderRenamedEvent event = new FolderRenamedEvent(id,name);
		eventbus.fireEvent(event);
	}
}
