package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author sriram
 * 
 */
public interface SessionManagementServiceAsync
{

	void getAttribute(String key, AsyncCallback<Object> callback);

	void setAttribute(String key, Object value, AsyncCallback<Void> callback);

	void removeAttribute(String key, AsyncCallback<Void> callback);

	void invalidate(AsyncCallback<Void> callback);

}
