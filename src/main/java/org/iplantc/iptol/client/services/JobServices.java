package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JobServices {

	public static void saveJob(String jsonParams,AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/contrast",jsonParams);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void runJob(String jobid, AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/contrast/"+ jobid + "/run",jobid);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
}
