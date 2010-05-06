package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * 
 * @author sriram
 * 
 */
public interface SessionManagementService extends RemoteService
{

	/**
	 * 
	 * @param key
	 * @return
	 * @throws SerializationException
	 */
	Object getAttribute(String key) throws SerializationException;;

	/**
	 * 
	 * @param key
	 * @param value
	 * @throws SerializationException
	 */
	void setAttribute(String key, Object value) throws SerializationException;;

	/**
	 * 
	 * @param key
	 * @throws SerializationException
	 */
	void removeAttribute(String key) throws SerializationException;;

	/**
	 * 
	 * @throws SerializationException
	 */
	void invalidate() throws SerializationException;;

}
