package org.iplantc.iptol.client.services;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ServiceCallback implements AsyncCallback<String> 
{
	//////////////////////////////////////////
	//private variables
	protected HandlerManager eventbus;

	//////////////////////////////////////////
	//constructor
	protected ServiceCallback(HandlerManager eventbus)
	{
		this.eventbus = eventbus;
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public abstract void onFailure(Throwable caught);
	
	@Override
	public abstract void onSuccess(String result); 
}
