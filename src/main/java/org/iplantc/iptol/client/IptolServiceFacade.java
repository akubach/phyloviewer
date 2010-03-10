package org.iplantc.iptol.client;

import org.iplantc.iptol.client.services.ServiceCallWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class IptolServiceFacade 
{
	private static IptolServiceFacade iptolService;
	private IptolServiceAsync proxy;
	
	public static final String IPTOL_SERVICE = "iptolservice";

	private IptolServiceFacade() 
	{
		proxy = (IptolServiceAsync) GWT.create(IptolService.class);
		
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + IPTOL_SERVICE);
	}

	public static IptolServiceFacade getInstance() 
	{
		if(iptolService == null) 
		{
			iptolService = new IptolServiceFacade();
		}
		
		return iptolService;
	}
	
	public void getServiceData(ServiceCallWrapper wrapper,AsyncCallback<String> callback) 
	{
		proxy.getServiceData(wrapper,callback);
	}
}
