package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileDeleteCallback implements AsyncCallback<String> 
{
	//////////////////////////////////////////
	//private methods
	private HandlerManager eventbus;
	private String id;
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public FileDeleteCallback(HandlerManager eventbus,String id)
	{
		this.eventbus = eventbus;
		this.id = id;
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		ErrorHandler.post(errorStrings.deleteFileFailed());	
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String result) 
	{
		FileDeletedEvent event = new FileDeletedEvent(id);
		eventbus.fireEvent(event);	
	}
}