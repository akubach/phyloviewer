package org.iplantc.iptol.client;

import org.iplantc.iptol.client.services.MultiPartServiceWrapper;
import org.iplantc.iptol.client.services.ServiceCallWrapper;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

public interface IptolService extends RemoteService
{
	String getServiceData(ServiceCallWrapper wrapper) throws SerializationException;
	String getServiceData(MultiPartServiceWrapper wrapper) throws SerializationException; 
}
