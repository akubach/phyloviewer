package org.iplantc.phyloviewer.client.services;

import org.iplantc.de.shared.SharedServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class TreeImageServices 
{
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
		SharedServiceFacade.getInstance().getServiceData(wrapper, callback);
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
		SharedServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
}
