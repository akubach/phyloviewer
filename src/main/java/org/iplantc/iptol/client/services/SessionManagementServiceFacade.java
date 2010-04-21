package org.iplantc.iptol.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
/**
 * 
 * @author sriram
 *
 */
public class SessionManagementServiceFacade {

	private static SessionManagementServiceFacade sessionMgmt = null;
	
	private SessionManagementServiceAsync proxy;
	
	public static final String SESSION_SERVICE = "sessionservice";
	
	private SessionManagementServiceFacade() {
		proxy = (SessionManagementServiceAsync) GWT.create(SessionManagementService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + SESSION_SERVICE);
	}
	
	public static SessionManagementServiceFacade getInstance() {
		if(sessionMgmt == null) {
			sessionMgmt = new SessionManagementServiceFacade();
		}
		
		return sessionMgmt;
	}
	
	
	public void getAttribute(String key, AsyncCallback<Object> callback) {
		proxy.getAttribute(key, callback);
	}

	public void setAttribute(String key, Object value, AsyncCallback<Void> callback){
		proxy.setAttribute(key, value, callback);
	}

	public void removeAttribute(String key, AsyncCallback<Void> callback) {
		proxy.removeAttribute(key, callback);
	}

	public void invalidate(AsyncCallback<Void> callback) {
		proxy.invalidate(callback);
	}
	
	
}
