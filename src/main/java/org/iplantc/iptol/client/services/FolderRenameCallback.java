package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FolderRenamedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

public class FolderRenameCallback extends ServiceCallback
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//constructor
	public FolderRenameCallback(HandlerManager eventbus,String id,String name)
	{
		super(eventbus);
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
		FolderRenamedEvent event = new FolderRenamedEvent(id,name);
		eventbus.fireEvent(event);
	}
}
