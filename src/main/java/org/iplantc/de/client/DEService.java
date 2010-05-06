package org.iplantc.de.client;

import org.iplantc.de.client.services.MultiPartServiceWrapper;
import org.iplantc.de.client.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Defines an interface for all remote services implemented in the application.
 */
public interface DEService extends RemoteService
{
	String getServiceData(ServiceCallWrapper wrapper) throws SerializationException;

	String getServiceData(MultiPartServiceWrapper wrapper) throws SerializationException;
}
