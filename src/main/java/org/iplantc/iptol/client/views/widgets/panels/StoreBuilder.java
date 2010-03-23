package org.iplantc.iptol.client.views.widgets.panels;

import java.util.Set;

import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.models.File;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.models.Folder;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class StoreBuilder 
{
	private static StoreBuilder instance;
	
	private StoreBuilder()
	{
		
	}
	
	/**
	 * Get a singleton instance
	 * @param in
	 */
	public static StoreBuilder getInstance() 
	{
		if(instance == null) 
		{
			instance = new StoreBuilder();
		}
		
		return instance;
	}

	/**
	 * Determine if a json array is empty
	 * @param in
	 *
	 * @return
	 */
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

	/**
	 * A native method to eval returned json
	 * @param json
	 * @return
	 */
	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;
	
	/**
	 * Add a folder to our store
	 * @param json
	 */
	private void addFolder(TreeStoreWrapper wrapper,Folder parent,JSONObject json)
	{
		Set<String> keys = json.keySet();

		JsArray<FileInfo> fileInfos = null;
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
					fileInfos =  asArrayofFileData(valFiles.toString());
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

		//temp variable for readability
		TreeStore<DiskResource> store = wrapper.getStore();
		
		if(parent == null)
		{
			wrapper.setRootFolderId(id);
		}
		else
		{
			if(parent.getId().equals(wrapper.getRootFolderId()))
			{
				store.add(folder,true);
			}
			else
			{
				store.add(parent,folder,true);
			}
		}

		//do we have any files to add?
		if(fileInfos != null)
		{
			for(int i = 0;i < fileInfos.length();i++)
			{
				 FileInfo info = fileInfos.get(i);
				 File child = new File(info);
				 child.set("type",info.getType());
				 child.set("uploaded",info.getUploaded());
				 
				 DiskResource parentFolder = store.findModel(folder);
				 child.setParent(parentFolder);
				 store.add(parentFolder,child,true);				 
				 parentFolder.add(child);
			}
		}

		//save updated store
		wrapper.setStore(store);
		
		if(subfolders!= null)
		{
			//loop through our sub-folders and recursively add them
			int size = subfolders.size();
			
		    for(int i = 0; i < size; i++)
		    {
		    	 JSONObject subfolder = (JSONObject)subfolders.get(i);
		    	 addFolder(wrapper,folder,subfolder);
		    }
		}
	}

	/**
	 * Add a folder to our tree view
	 * @param json
	 * @return
	 */
	public void updateWrapper(TreeStoreWrapper wrapper,String json)
	{	
		wrapper.clearStore();
		
		if(json != null)
		{
			JSONObject jsonRoot = (JSONObject)JSONParser.parse(json);

			//if we got this far, we have a tag for the root
			JSONObject root = (JSONObject) jsonRoot.get("rootFolder");

			if(root != null)
			{
				addFolder(wrapper,null,root);
			}

			wrapper.getStore().sort("name",SortDir.DESC);
		}
		
	}
}
