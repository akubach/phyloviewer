package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolServiceFacade;
import org.iplantc.iptol.client.ServiceCallWrapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderServices 
{
	private static IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	
	
	/**
	 * Get the folders and files for a given workspace
	 * @param idWorkspace
	 * @param callback
	 */
	public static void getUserInfo(String username,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(constants.userInfoService() + username);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Get the folders and files for a given workspace
	 * @param idWorkspace
	 * @param callback
	 */
	public static void getFiletree(String idWorkspace,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(constants.fileTreeRetrievalService() + idWorkspace);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to create a new folder
	 * @param name
	 * @param idParent
	 * @param callback
	 */
	public static void createFolder(String idWorkspace,String name,String idParent,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,constants.folderService() + idWorkspace + "/folders/" + idParent + "/folders","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}

	/**
	 * Call service to rename a folder
	 * @param id
	 * @param name
	 * @param callback
	 */
	public static void renameFolder(String id,String name,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,constants.folderService() + id + "/label","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}

	/**
	 * Call service to delete a folder
	 * @param id
	 * @param callback
	 */
	public static void deleteFolder(String id,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,constants.folderService() + id);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
}
