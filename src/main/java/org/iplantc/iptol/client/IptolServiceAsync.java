package org.iplantc.iptol.client;

import org.iplantc.iptol.client.services.MultiPartServiceWrapper;
import org.iplantc.iptol.client.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IptolServiceAsync 
{	
	void getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback);
	void getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback);
}
