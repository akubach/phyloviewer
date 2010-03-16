package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileRenameCallback implements AsyncCallback<String>
{
	//////////////////////////////////////////
	//private variables
	private HandlerManager eventbus;
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//constructor
	public FileRenameCallback(HandlerManager eventbus,String id,String name)
	{
		this.eventbus = eventbus;
		this.id = id;
		this.name = name;
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		// TODO handle failure		
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String arg0) 
	{
		FileRenamedEvent event = new FileRenamedEvent(id,name);
		eventbus.fireEvent(event);
	}
}
