package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderCreateCallback implements AsyncCallback<String> 
{
	private HandlerManager eventbus;
	private String name;
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	public FolderCreateCallback(HandlerManager eventbus,String name)
	{
		this.eventbus = eventbus;
		this.name = name;
	}
	
	@Override
	public void onFailure(Throwable arg0) 
	{
		ErrorHandler.post(errorStrings.createFolderFailed());		
	}

	@Override
	public void onSuccess(String result) 
	{		
		if(result != null && result.length() > 0)
		{
			JSONObject json = (JSONObject)JSONParser.parse(result);				
			JSONValue val = json.get("id");
		
			if(val != null)
			{
				String id = val.isString().stringValue();
				
				FolderCreatedEvent event = new FolderCreatedEvent(id,name);
				eventbus.fireEvent(event);
			}			
		}				
	}
}
