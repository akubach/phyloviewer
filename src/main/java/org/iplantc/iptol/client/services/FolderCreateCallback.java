package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderCreateCallback implements AsyncCallback<String>
{
	private String name;
	
	public FolderCreateCallback(String name)
	{
		this.name = name;
	}
	
	@Override
	public void onFailure(Throwable arg0) 
	{
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
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

				EventBus eventbus = EventBus.getInstance();
				FolderCreatedEvent event = new FolderCreatedEvent(id,name);
				eventbus.fireEvent(event);
			}			
		}				
	}
}
