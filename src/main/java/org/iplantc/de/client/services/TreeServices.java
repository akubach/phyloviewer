package org.iplantc.de.client.services;

import org.iplantc.de.client.DEServiceFacade;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for the management of phylogenetic trees. 
 */
public class TreeServices
{

	public static void getTreesInWorkspace(String workspaceId, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/workspaces/" + workspaceId + "/trees");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	public static void getTreeSpecies(String request, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, "http://"
				+ Window.Location.getHostName() + ":14444/trees/taxa", request);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to get the trees associated with a file
	 * 
	 * @param idFile
	 * @param callback
	 */
	public static void getTreesFromFile(String idFile, AsyncCallback<String> callback)
	{
		String address = "http://" + Window.Location.getHostName() + ":14444/files/" + idFile + "/trees";
		ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve an individual tree's data as json
	 * 
	 * @param idFile
	 * @param callback
	 */
	public static void getTreeData(String idTree, AsyncCallback<String> callback)
	{
		String address = "http://" + Window.Location.getHostName() + ":14444/trees/" + idTree;
		ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve an individual tree image
	 * 
	 * @param json
	 * @param callback
	 */
	public static void getTreeImage(String json, AsyncCallback<String> callback)
	{
		String address = "http://genji.iplantcollaborative.org/cgi-bin/create_image?use_branch_lengths=off";
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, json);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve an individual tree image
	 * 
	 * @param json
	 * @param width
	 * @param height
	 * @param callback
	 */
	public static void getTreeImage(String json, int width, int height, Boolean showTaxonLabels,
			AsyncCallback<String> callback)
	{
		String showText = showTaxonLabels ? "on" : "off";
		String address = "http://genji.iplantcollaborative.org/cgi-bin/v2/create_image?use_branch_lengths=off&show_text="
				+ showText;
		address += "&width=" + width + "&height=" + height;

		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, json);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
}
