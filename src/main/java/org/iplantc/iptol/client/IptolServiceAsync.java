package org.iplantc.iptol.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IptolServiceAsync 
{
	void getServiceData(String ServiceId, AsyncCallback<String> callback);
}
