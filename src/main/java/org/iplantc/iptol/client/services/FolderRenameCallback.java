package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FolderRenamedEvent;

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
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
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
