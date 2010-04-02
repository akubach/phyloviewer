package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.IptolServiceFacade;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderServices 
{
	/**
	 * Call service to retrieve the the user information
	 * @param callback
	 * @param idWorkspace
	 */
	public static void getUserInfo(AsyncCallback<String> callback)
	{		
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName() + ":14444/bootstrap");
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
	public static void renameFolder(String idWorkspace,String idFolder,String name,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,"http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/folders/" + idFolder + "/label","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}

	/**
	 * Call service to rename a folder
	 * @param id
	 * @param name
	 * @param callback
	 */
	public static void renameFile(String idFile,String name,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/files/" +  idFile + "/name",name);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
		
	/**
	 * Call service to delete multiple files/folders
	 * @param idWorkspace
	 * @param body
	 * @param callback
	 */
	public static void deleteDiskResources(String idWorkspace,String body,AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,"http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/resources?method=multiDelete",body);
		IptolServiceFacade.getInstance().getServiceData(wrapper,callback);
	}
}
