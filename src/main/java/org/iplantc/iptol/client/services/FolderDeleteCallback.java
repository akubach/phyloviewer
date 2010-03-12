package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.events.disk.mgmt.FolderDeletedEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderDeleteCallback implements AsyncCallback<String> 
{
	//////////////////////////////////////////
	//private methods
	private HandlerManager eventbus;
	private String id;
	
	//////////////////////////////////////////
	//constructor
	public FolderDeleteCallback(HandlerManager eventbus,String id)
	{
		this.eventbus = eventbus;
		this.id = id;
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		//TODO handle failure		
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String result) 
	{
		FolderDeletedEvent event = new FolderDeletedEvent(id);
		eventbus.fireEvent(event);	
	}
}
