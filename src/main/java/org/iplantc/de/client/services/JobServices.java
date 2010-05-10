package org.iplantc.de.client.services;

import org.iplantc.phyloviewer.shared.DEServiceFacade;
import org.iplantc.phyloviewer.shared.ServiceCallWrapper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for job management operations. 
 */
public class JobServices
{

	public static void saveContrastJob(String jsonParams, String workspaceId,
			AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, "http://"
				+ Window.Location.getHostName() + ":14444/workspaces/" + workspaceId + "/contrast",
				jsonParams);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void runContrastJob(String jobid, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, "http://"
				+ Window.Location.getHostName() + ":14444/contrast/" + jobid + "?method=run", jobid);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void getContrastJobs(String workspaceId, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, "http://"
				+ Window.Location.getHostName() + ":14444/workspaces/" + workspaceId + "/contrast");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void getContrastJobConfig(String jobid, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, "http://"
				+ Window.Location.getHostName() + ":14444/contrast/" + jobid);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void deleteContrastJob(String jobid, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, "http://"
				+ Window.Location.getHostName() + ":14444/contrast/" + jobid);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

}
