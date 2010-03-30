package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JobServices {

	public static void saveContrastJob(String jsonParams,AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/contrast",jsonParams);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void runContrastJob(String jobid, AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/contrast/"+ jobid + "/run",jobid);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void getContrastJobs(AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET,"http://" + Window.Location.getHostName() + ":14444/contrast");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void getContrastJobConfig(String jobid, AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET,"http://" + Window.Location.getHostName() + ":14444/contrast/" + jobid);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	public static void deleteContrastJob(String jobid,AsyncCallback<String> callback) {
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,"http://" + Window.Location.getHostName() + ":14444/contrast/" + jobid);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
}
