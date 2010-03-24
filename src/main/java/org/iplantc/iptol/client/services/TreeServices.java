package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TreeServices {

	public static void getTrees(AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/trees");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void getTreeSpecies(String request,AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/trees/taxa",request);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
}
