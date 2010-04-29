package org.iplantc.de.client;

import org.iplantc.de.client.services.MultiPartServiceWrapper;
import org.iplantc.de.client.services.ServiceCallWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class DEServiceFacade 
{
	private static DEServiceFacade srvFacade;
	private DEServiceAsync proxy;
	
	public static final String DE_SERVICE = "deservice";

	private DEServiceFacade() 
	{
		proxy = (DEServiceAsync) GWT.create(DEService.class);
		
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + DE_SERVICE);
	}

	public static DEServiceFacade getInstance() 
	{
		if(srvFacade == null) 
		{
			srvFacade = new DEServiceFacade();
		}
		
		return srvFacade;
	}
	
	public void getServiceData(ServiceCallWrapper wrapper,AsyncCallback<String> callback) 
	{
		proxy.getServiceData(wrapper,new AsyncCallbackWrapper<String>(callback));
	}
	
	public void getServiceData(MultiPartServiceWrapper wrapper,AsyncCallback<String> callback) 
	{
		proxy.getServiceData(wrapper,new AsyncCallbackWrapper<String>(callback));
	}
}
