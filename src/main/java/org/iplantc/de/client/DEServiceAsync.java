package org.iplantc.de.client;

import org.iplantc.de.client.services.MultiPartServiceWrapper;
import org.iplantc.de.client.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DEServiceAsync 
{	
	void getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback);
	void getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback);
}
