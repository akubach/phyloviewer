package org.iplantc.iptol.client;

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

	@SuppressWarnings("unchecked")
	public void getServiceData(String serviceId,AsyncCallback callback) 
	{
		proxy.getServiceData(serviceId,callback);
	}
}
