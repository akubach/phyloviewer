package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TraitServices {

	public static void getMatrices(String workspaceId, AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/workspaces/" + workspaceId + "/matrices");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void getSpeciesNames(String matrixid,AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/matrices/" + matrixid + "/taxa");
		IptolServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
	
	public static void saveMatrices(String matrixid,String body, AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"http://" + Window.Location.getHostName() + ":14444/matrices/" + matrixid,body);
		IptolServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
}
