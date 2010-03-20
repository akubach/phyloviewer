package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderServices 
{
	/**
	 * Call service to retrieve the the user information
	 * @param idWorkspace
	 * @param callback
	 */
	public static void getUserInfo(String username,AsyncCallback<String> callback)
	{		
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/users/" + username);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to retrieve the folders and files for a given workspace
	 * @param idWorkspace
	 * @param callback
	 */
	public static void getFiletree(String idWorkspace,AsyncCallback<String> callback)
	{		
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/workspace/" + idWorkspace);
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
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/workspaces/"  + idWorkspace + "/folders/" + idParent + "/folders","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}

	/**
	 * Call service to rename a folder
	 * @param id
	 * @param name
	 * @param callback
	 */
	public static void renameFolder(String idWorkspace,String id,String name,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/folders/" + id + "/label","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}

	/**
	 * Call service to rename a folder
	 * @param id
	 * @param name
	 * @param callback
	 */
	public static void renameFile(String id,String name,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/files/" +  id + "/name",name);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to delete a folder
	 * @param id
	 * @param callback
	 */
	public static void deleteFolder(String idWorkspace,String id,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,"http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/folders/" + id);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
	
	/**
	 * Call service to delete a file
	 * @param id
	 * @param callback
	 */
	public static void deleteFile(String id,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,"http://" + Window.Location.getHostName() + ":14444/files/" + id);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
}
