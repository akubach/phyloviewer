package org.iplantc.iptol.client.services;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.DiskResourceDeletedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DiskResourceDeleteCallback implements AsyncCallback<String>
{
	//////////////////////////////////////////
	//private variables
	private List<String> folders = new ArrayList<String>();
	private List<String> files = new ArrayList<String>();

	//////////////////////////////////////////
	//constructors
	public DiskResourceDeleteCallback()
	{		
	}
	
	//////////////////////////////////////////
	public DiskResourceDeleteCallback(List<String> folders,List<String> files)
	{
		this.folders = folders;
		this.files = files;
	}
	
	//////////////////////////////////////////
	public void addFolder(String id)
	{
		if(id != null)
		{
			folders.add(id);
		}
	}
	
	//////////////////////////////////////////
	public void addFile(String id)
	{
		if(id != null)
		{
			files.add(id);
		}
	}
	
	//////////////////////////////////////////
	@Override
	public void onSuccess(String result) 
	{
		EventBus eventbus = EventBus.getInstance();
		DiskResourceDeletedEvent event = new DiskResourceDeletedEvent(folders,files);
		eventbus.fireEvent(event);	
	}

	//////////////////////////////////////////
	@Override
	public void onFailure(Throwable caught) 
	{
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
		
		ErrorHandler.post(errorStrings.deleteFolderFailed());	
	}
}
