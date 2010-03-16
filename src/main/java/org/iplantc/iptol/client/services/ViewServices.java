package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ViewServices 
{
	private static IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	
	/**
	 * Call service to retrieve the raw data for a requested file
	 * @param fileId
	 * @param callback
	 */
	public static void getRawData(String fileId,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(constants.rawDataRetreivalService() + fileId + "/rawcontent");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to retrieve the provenance data for a requested file
	 * @param fileId
	 * @param callback
	 */
	public static void getRawDataProvenance(String fileId,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(constants.rawDataRetreivalService() + fileId + "/provenance.text.all");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}	
}
