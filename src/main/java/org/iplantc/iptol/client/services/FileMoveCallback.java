package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FileMovedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileMoveCallback implements AsyncCallback<String> 
{
	//////////////////////////////////////////
	//private variables
	private String idFolder;
	private String idFile;
	
	//////////////////////////////////////////
	//constructor
	public FileMoveCallback(String idFolder,String idFile)
	{
		this.idFolder = idFolder;
		this.idFile = idFile;
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
		ErrorHandler.post(errorStrings.moveFileFailed());			
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String arg0) 
	{
		EventBus eventbus = EventBus.getInstance();		
		FileMovedEvent event = new FileMovedEvent(idFolder,idFile);
		eventbus.fireEvent(event);
	}
}
