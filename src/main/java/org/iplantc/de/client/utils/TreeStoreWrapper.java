package org.iplantc.de.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEvent;
import org.iplantc.de.client.models.DiskResource;
import org.iplantc.de.client.models.File;
import org.iplantc.de.client.models.JsFile;
import org.iplantc.de.client.models.Folder;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class TreeStoreWrapper 
{
	///////////////////////////////////////
	//private variables
	private String rootFolderId = "-1";
	private String uploadFolderId = "";
	private TreeStore<DiskResource> store = new TreeStore<DiskResource>();
	
	private boolean isEmpty(JSONValue in)
	{
		boolean ret = true;  //assume we have an empty value

		if(in != null)
		{
			String test = in.toString();

			if(test.length() > 0 && !test.equals("[]"))
			{
				ret = false;
			}
		}

		return ret;
	}

	private void addFolder(Folder parent,JSONObject json)
	{
		Set<String> keys = json.keySet();

		JsArray<JsFile> fileInfos = null;
		String label = new String();
		JSONArray subfolders = null;
		String id = new String();
		
		//parse
		for(String key : keys)
		{
			if(key.equals("files"))
			{
				JSONValue valFiles = json.get("files");

				if(!isEmpty(valFiles))
				{
					fileInfos =  JsonConverter.asArrayofFileData(valFiles.toString());
				}
			}
			else if(key.equals("id"))
			{
				id = json.get("id").isString().stringValue();
			}
			else if(key.equals("label"))
			{
				label = json.get("label").isString().stringValue();
			}
			else if(key.equals("uploadFolderId"))
			{
				uploadFolderId = json.get("uploadFolderId").isString().stringValue();
			}
			else if(key.equals("subfolders"))
			{
				JSONValue valSubFolders = json.get("subfolders");

				if(!isEmpty(valSubFolders))
				{
					subfolders = valSubFolders.isArray();
				}
			}
		}

		//create our folder
		Folder folder = new Folder(id,label);
		folder.setParent(parent);

		if(parent == null)
		{
			rootFolderId = id;
		}
		else
		{
			if(parent.getId().equals(rootFolderId))
			{
				store.add(folder,true);
			}
			else
			{
				store.add(parent,folder,true);
			}
		}

		//do we have any files to add?  We don't add files to our root folder.
		if(parent != null && fileInfos != null)
		{
			for(int i = 0;i < fileInfos.length();i++)
			{
				JsFile info = fileInfos.get(i);
				File child = new File(info);
				child.set("type",info.getType());
				child.set("uploaded",info.getUploaded());

				DiskResource parentFolder = store.findModel(folder);
				child.setParent(parentFolder);
				store.add(parentFolder,child,true);
				parentFolder.add(child);
			}
		}

		if(subfolders!= null)
		{
			//loop through our sub-folders and recursively add them
			int size = subfolders.size();

		    for(int i = 0; i < size; i++)
		    {
		    	JSONObject subfolder = (JSONObject)subfolders.get(i);
		    	addFolder(folder,subfolder);
		    }
		}
	}

	private Folder getFolder(String id)
	{
		Folder ret = null;  //assume failure

		for(DiskResource resource : store.getAllItems())
		{
			if(resource.getId().equals(id))
			{
				if(resource instanceof Folder)
				{
					ret = (Folder)resource;
					break;
				}
			}
		}
		return ret;
	}

	private File getFile(String id)
	{
		File ret = null;  //assume failure

		for(DiskResource resource : store.getAllItems())
		{
			if(resource.getId().equals(id))
			{
				if(resource instanceof File)
				{
					ret = (File)resource;
					break;
				}
			}
		}

		return ret;
	}

	private boolean isValidString(String in)
	{
		return (in != null && in.length() > 0);
	}
	
	private void removeChildren(Folder parent)
	{
		if(parent != null)
		{
			List<ModelData> files = parent.getChildren();
		
			for(ModelData item : files)
			{
				store.remove((DiskResource)item);
			}
					
			parent.removeAll();
		}
	}

	private void deleteFolders(List<String> ids)
	{
		if(ids != null)
		{
			for(String id : ids)
			{
				deleteFolder(id);
			}
		}
	}
	
	private void deleteFiles(List<String> ids)
	{
		if(ids != null)
		{
			for(String id : ids)
			{
				deleteFile(id);
			}
		}
	}
	
	/**
	 * Get the id of our root folder
	 * @return
	 */
	public String getRootFolderId()
	{
		return rootFolderId;
	}

	/**
	 * Get the id of our upload folder
	 * @return
	 */
	public String getUploadFolderId()
	{
		return uploadFolderId;
	}
	
	/**
	 * Get our treestore
	 * @return
	 */
	public TreeStore<DiskResource> getStore()
	{
		return store;
	}
	
	/**
	 * Rebuild our treestore from a json string
	 * @param json
	 */
	public void updateWrapper(String json)
	{
		store.removeAll();

		if(json != null)
		{
			JSONObject jsonRoot = (JSONObject)JSONParser.parse(json);
		
			//get our upload folder id
			if(jsonRoot.containsKey("uploadFolderId"))
			{
				uploadFolderId = jsonRoot.get("uploadFolderId").isString().stringValue();
			}
					
			//if we got this far, we have a tag for the root
			JSONObject root = (JSONObject) jsonRoot.get("homeFolder");

			if(root != null)
			{
				addFolder(null,root);
			}
		}
	}

	/**
	 * Rename a folder in our treestore
	 * @param id
	 * @param json
	 */
	public Folder renameFolder(String id,String name)
	{
		Folder ret = null;  //assume failure

		if(isValidString(id)  && isValidString(name))
		{			
			ret = getFolder(id);

			if(ret != null)
			{
				Record record = store.getRecord(ret);
				record.set("name",name);
				ret.setName(name);
			}
		}

		return ret;
	}

	/**
	 * Delete a folder in our treestore
	 * @param id
	 */
	public void deleteFolder(String id)
	{
		if(isValidString(id))
		{			
			Folder folder = getFolder(id);

			if(folder != null)
			{
				if(folder.getId().equals(uploadFolderId))
				{
					removeChildren(folder);						
				}
				else
				{
					store.remove(folder);
				}
			}			
		}
	}

	/**
	 * Delete a file in our treestore
	 * @param id
	 */
	public void deleteFile(String id)
	{
		if(isValidString(id))
		{
			File file = getFile(id);

			if(file != null)
			{
				Folder parent = (Folder)file.getParent();
				
				parent.remove(file);
				store.remove(file);				
			}
		}
	}

	/**
	 * Create a folder in our treestore
	 * @param wrapper
	 * @param id
	 * @param name
	 * @return
	 */
	public Folder createFolder(String id,String name)
	{
		Folder ret = null;  //assume failure

		if(isValidString(id)  && isValidString(name))
		{
			ret = new Folder(id,name);				
			ret.setParent(getFolder(rootFolderId));
				
			store.add(ret,false);
		}

		return ret;
	}

	/**
	 * Add a file to our treestore
	 * @param parentId
	 * @param info
	 * @param deleteIds 
	 * @return
	 */
	public File addFile(String parentId,JsFile info, ArrayList<String> deleteIds)
	{
		File ret = null;  //assume failure
		
		if (parentId == null || parentId.equals("")) 
		{
			parentId = uploadFolderId;
		}

		if(isValidString(parentId)  && info != null)
		{
			//make sure we don't already have this file in our store
			if(getFile(info.getId()) == null)
			{
				DiskResource parent = getFolder(parentId);

				if(parent != null)
				{
					ret = new File(info);

					//establish parent/child relationship
					ret.setParent(parent);
					parent.add(ret);

					//always insert at the top
					//store.add(parent,ret,false);
					store.insert(parent,ret,0,false);
				}
			}
		}

		//delete all duplicate files
		if(store != null && deleteIds!=null ) {
			
			for(String id : deleteIds)
			{
				File file = getFile(id);

				if(file != null)
				{
					Folder parent = (Folder)file.getParent();
					parent.remove(file);
					store.remove(file);	
				}
			}
			
			//remove relevant events
			DiskResourceDeletedEvent event = new DiskResourceDeletedEvent(null, deleteIds);
			EventBus eventbus = EventBus.getInstance();
			eventbus.fireEvent(event);
			
		}
			
		return ret;
	}

	/**
	 * Rename a file in our treestore
	 * @param id
	 * @param name
	 * @return
	 */
	public File renameFile(String id,String name)
	{
		File ret = null;

		if(isValidString(id)  && isValidString(name))
		{
			ret = getFile(id);
			DiskResource parent = (DiskResource)ret.getParent();

			if(ret != null && parent != null)
			{
				Record record = store.getRecord(ret);
				record.set("name",name);
				ret.setName(name);
			}
		}
		
		return ret;
	}
	
	/**
	 * Delete files and folders from our treestore
	 * @param folders
	 * @param files
	 * @return
	 */
	public void delete(List<String> folders,List<String> files)
	{
		deleteFiles(files);
		deleteFolders(folders);		
	}
	
	/**
	 * Retrieve the upload folder
	 * @return
	 */
	public Folder getUploadFolder()
	{		
		return getFolder(uploadFolderId);		
	}	
	
	/**
	 * Move a file to a folder
	 * @param idFolder
	 * @param idFile
	 * @return
	 */
	public File moveFile(String idFolder,String idFile)
	{
		File ret = null;  //assume failure
		
		if(isValidString(idFolder) && isValidString(idFile))
		{			
			Folder newParent = getFolder(idFolder);
			
			if(newParent != null)
			{
				ret = getFile(idFile);
			
				if(ret != null)
				{
					//first, we need to remove the original file (and all dependencies
					Folder parent = (Folder)ret.getParent();					
					parent.remove(ret);
					store.remove(ret);
					
					//now we will add it to the new folder
					ret.setParent(newParent);
					newParent.add(ret);
					store.add(newParent,ret,false);				
				}			
			}			
		}
		
		return ret;
	}	
}
