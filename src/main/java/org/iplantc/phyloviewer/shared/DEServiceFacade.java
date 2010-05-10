package org.iplantc.phyloviewer.shared;

import org.iplantc.de.client.AsyncCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * A singleton service that provides an asynchronous proxy to data services. 
 */
public class DEServiceFacade
{
	public static final String DE_SERVICE = "deservice";
	
	private static DEServiceFacade srvFacade;
	private DEServiceAsync proxy;

	private DEServiceFacade()
	{
		proxy = (DEServiceAsync)GWT.create(DEService.class);

		((ServiceDefTarget)proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + DE_SERVICE);
	}

	public static DEServiceFacade getInstance()
	{
		if(srvFacade == null)
		{
			srvFacade = new DEServiceFacade();
		}

		return srvFacade;
	}

	public void getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback)
	{
		proxy.getServiceData(wrapper, new AsyncCallbackWrapper<String>(callback));
	}

	public void getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback)
	{
		proxy.getServiceData(wrapper, new AsyncCallbackWrapper<String>(callback));
	}
}
