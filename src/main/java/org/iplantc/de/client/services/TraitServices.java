package org.iplantc.de.client.services;

import org.iplantc.de.client.DEServiceFacade;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for operations related to trait data management.
 */
public class TraitServices
{

	public static void getMatrices(String workspaceId, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/workspaces/" + workspaceId + "/matrices");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void getSpeciesNames(String matrixid, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/matrices/" + matrixid + "/taxa");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void saveMatrices(String matrixid, String body, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, "http://"
				+ Window.Location.getHostName() + ":14444/matrices/" + matrixid, body);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve the trait data indices for a requested file
	 * 
	 * @param idWorkspace
	 * @param idFile
	 * @param callback
	 */
	public static void getTraitDataIds(String idWorkspace, String idFile, AsyncCallback<String> callback)
	{
		// the following line should be used once the trait service takes the workspace
		// into account
		// ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" +
		// Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/files/"
		// + idFile + "/matrices");
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/files/" + idFile + "/matrices");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve the trait data for a requested file
	 * 
	 * @param idMatrix
	 * @param callback
	 */
	public static void getTraitData(String idMatrix, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/matrices/" + idMatrix);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
}
