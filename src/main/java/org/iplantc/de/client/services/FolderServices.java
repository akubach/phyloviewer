package org.iplantc.de.client.services;

import org.iplantc.phyloviewer.shared.DEServiceFacade;
import org.iplantc.phyloviewer.shared.MultiPartServiceWrapper;
import org.iplantc.phyloviewer.shared.ServiceCallWrapper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for folder operations.  
 */
public class FolderServices
{
	/**
	 * Call service to retrieve the the user information
	 * 
	 * @param callback
	 * @param idWorkspace
	 */
	public static void getUserInfo(AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/bootstrap");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to retrieve the folders and files for a given workspace
	 * 
	 * @param idWorkspace
	 * @param callback
	 */
	public static void getFiletree(String idWorkspace, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/workspace/" + idWorkspace);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to create a new folder
	 * 
	 * @param name
	 * @param idParent
	 * @param callback
	 */
	public static void createFolder(String idWorkspace, String name, String idParent,
			AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, "http://"
				+ Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/folders/"
				+ idParent + "/folders", "{\"label\":\"" + name + "\"}");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to rename a folder
	 * 
	 * @param id
	 * @param name
	 * @param callback
	 */
	public static void renameFolder(String idWorkspace, String idFolder, String name,
			AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, "http://"
				+ Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace + "/folders/"
				+ idFolder + "/label", "{\"label\":\"" + name + "\"}");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to rename a folder
	 * 
	 * @param id
	 * @param name
	 * @param callback
	 */
	public static void renameFile(String idFile, String name, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, "http://"
				+ Window.Location.getHostName() + ":14444/files/" + idFile + "/name", "{\"label\":\""
				+ name + "\"}");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);

	}

	/**
	 * Call service to delete multiple files/folders
	 * 
	 * @param idWorkspace
	 * @param body
	 * @param callback
	 */
	public static void deleteDiskResources(String idWorkspace, String body,
			AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, "http://"
				+ Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace
				+ "/resources?method=multiDelete", body);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to upload a file to our server
	 * 
	 * @param idWorkspace
	 * @param filename
	 * @param idFolder
	 * @param idWorkspace
	 * @param body
	 * @param callback
	 */
	public static void uploadFile(String idWorkspace, String filename, String idFolder, String body,
			AsyncCallback<String> callback)
	{
		String address = "http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace
				+ "/folders/" + idFolder + "/files";

		MultiPartServiceWrapper wrapper = new MultiPartServiceWrapper(MultiPartServiceWrapper.Type.POST,
				address);
		String disposition = "name=\"file\"; filename=\"" + filename + "\"";
		wrapper.addPart(body, disposition);

		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call service to get a list of files
	 * 
	 * @param idWorkspace
	 * @param callback
	 */
	public static void getListofFiles(String idWorkspace, AsyncCallback<String> callback)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper("http://" + Window.Location.getHostName()
				+ ":14444/workspaces/" + idWorkspace + "/files");
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}

	/**
	 * Call move a file to a desired folder
	 * 
	 * @param idWorkspace
	 * @param idFolder
	 * @param idFile
	 * @param callback
	 */
	public static void moveFile(String idWorkspace, String idFolder, String idFile,
			AsyncCallback<String> callback)
	{
		String address = "http://" + Window.Location.getHostName() + ":14444/workspaces/" + idWorkspace
				+ "/files/" + idFile + "/parentFolder";
		String body = "{\"id\" : \"" + idFolder + "\"}";
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, address, body);
		DEServiceFacade.getInstance().getServiceData(wrapper, callback);
	}
}