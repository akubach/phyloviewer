package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ViewServices 
{	
	/**
	 * Call service to retrieve the raw data for a requested file
	 * @param fileId
	 * @param callback
	 */
	public static void getRawData(String idFile,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/files/" + idFile + "/rawcontent");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to retrieve the provenance data for a requested file
	 * @param fileId
	 * @param callback
	 */
	public static void getFileProvenance(String idFile,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/files/" + idFile + "/provenance.text.all");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to retrieve the trait data indices for a requested file
	 * @param fileId
	 * @param callback
	 */
	public static void getTraitDataIds(String idWorkspace,String idFile,AsyncCallback<String> callback)
	{
		//the following line should be used once the trait service takes the workspace into account 
		//ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/files/" + idFile + "/matrices");
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/files/" + idFile + "/matrices");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to retrieve the trait data for a requested file
	 * @param fileId
	 * @param callback
	 */
	public static void getTraitData(String idMatrix,AsyncCallback<String> callback)
	{		
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/matrices/" + idMatrix);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
}
