package org.iplantc.iptol.client;

import org.iplantc.iptol.client.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.RemoteService;

public interface IptolService extends RemoteService
{
	String getServiceData(ServiceCallWrapper wrapper);
}
