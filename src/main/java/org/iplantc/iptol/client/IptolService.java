package org.iplantc.iptol.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface IptolService extends RemoteService{
	
	String getServiceData(String ServiceId);

}
