package org.iplantc.iptol.client.views.widgets.panels;

import java.util.List;
import java.util.Set;

import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.models.File;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.models.Folder;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class TreeStoreManager
{
	private static TreeStoreManager instance;

	private TreeStoreManager()
	{
	}

	/**
	 * Get a singleton instance
	 * @return
	 */
	public static TreeStoreManager getInstance()
	{
		if(instance == null)
		{
			instance = new TreeStoreManager();
		}

		return instance;
	}

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

	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;

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
			else if(key.equals("uploadFolderId"))
			{
				wrapper.setUploadFolderId(json.get("uploadFolderId").isString().stringValue());
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

		//do we have any files to add?  We don't add files to our root folder.
		if(parent != null && fileInfos != null)
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

	private Folder getFolder(TreeStore<DiskResource> store,String id)
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

	private File getFile(TreeStore<DiskResource> store,String id)
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
	
	private void removeChildren(TreeStoreWrapper wrapper,Folder parent)
	{
		if(parent != null)
		{
			TreeStore<DiskResource> store = wrapper.getStore();
			if(store != null)
			{
				List<ModelData> files = parent.getChildren();
			
				for(ModelData item : files)
				{
					store.remove((DiskResource)item);
				}
			}
		}
	}
		/**
	 * Rebuild our treestore from a json string
	 * @param wrapper
	 * @param json
	 */
	public void updateWrapper(TreeStoreWrapper wrapper,String json)
	{
		wrapper.clearStore();

		if(json != null)
		{
			JSONObject jsonRoot = (JSONObject)JSONParser.parse(json);
		
			//get our upload folder id
			if(jsonRoot.containsKey("uploadFolderId"))
			{
				wrapper.setUploadFolderId(jsonRoot.get("uploadFolderId").isString().stringValue());
			}
					
			//if we got this far, we have a tag for the root
			JSONObject root = (JSONObject) jsonRoot.get("homeFolder");

			if(root != null)
			{
				addFolder(wrapper,null,root);
			}

			wrapper.getStore().sort("name",SortDir.DESC);
		}
	}

	/**
	 * Rename a folder in our treestore
	 * @param wrapper
	 * @param id
	 * @param json
	 */
	public Folder doFolderRename(TreeStoreWrapper wrapper,String id,String name)
	{
		Folder ret = null;  //assume failure

		if(wrapper != null && isValidString(id)  && isValidString(name))
		{
			TreeStore<DiskResource> store = wrapper.getStore();
			if(store != null)
			{
				ret = getFolder(store,id);

				if(ret != null)
				{
					Record record = store.getRecord(ret);
					record.set("name",name);
					ret.setName(name);
				}
			}
		}

		return ret;
	}

	/**
	 * Delete a folder in our treestore
	 * @param wrapper
	 * @param id
	 */
	public void doFolderDelete(TreeStoreWrapper wrapper,String id)
	{
		if(wrapper != null && isValidString(id))
		{
			TreeStore<DiskResource> store = wrapper.getStore();

			if(store != null)
			{
				DiskResource resource = getFolder(store,id);

				if(resource != null)
				{
					if(resource.getId().equals(wrapper.getUploadFolderId()))
					{
						removeChildren(wrapper,(Folder)resource);						
					}
					else
					{
						store.remove(resource);
					}
				}
			}
		}

	}

	/**
	 * Delete a file in our treestore
	 * @param wrapper
	 * @param id
	 */
	public void doFileDelete(TreeStoreWrapper wrapper,String id)
	{
		if(wrapper != null && isValidString(id))
		{
			TreeStore<DiskResource> store = wrapper.getStore();

			if(store != null)
			{
				DiskResource resource = getFile(store,id);

				if(resource != null)
				{
					store.remove(resource);
				}
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
	public Folder doFolderCreate(TreeStoreWrapper wrapper,String id,String name)
	{
		Folder ret = null;  //assume failure

		if(wrapper != null && isValidString(id)  && isValidString(name))
		{
			TreeStore<DiskResource> store = wrapper.getStore();

			if(store != null)
			{
				ret = new Folder(id,name);
				store.add(ret,false);
			}
		}

		return ret;
	}

	/**
	 * Add a file to our treestore
	 * @param wrapper
	 * @param parentId
	 * @param info
	 * @return
	 */
	public File doFileAdd(TreeStoreWrapper wrapper,String parentId,FileInfo info)
	{
		File ret = null;  //assume failure

		if(wrapper != null && isValidString(parentId)  && info != null)
		{
			TreeStore<DiskResource> store = wrapper.getStore();

			if(store != null)
			{
				DiskResource parent = getFolder(store,parentId);

				if(parent != null)
				{
					ret = new File(info);

					//establish parent/child relationship
					ret.setParent(parent);
					parent.add(ret);

					store.add(parent,ret,false);
				}
			}
		}

		return ret;
	}

	/**
	 * Rename a file in our treestore
	 * @param wrapper
	 * @param id
	 * @param name
	 * @return
	 */
	public File doFileRename(TreeStoreWrapper wrapper,String id,String name)
	{
		File ret = null;

		if(wrapper != null && isValidString(id)  && isValidString(name))
		{
			TreeStore<DiskResource> store = wrapper.getStore();

			if(store != null)
			{
				ret = getFile(store,id);
				DiskResource parent = (DiskResource)ret.getParent();

				if(ret != null && parent != null)
				{
					Record record = store.getRecord(ret);
					record.set("name",name);
					ret.setName(name);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Retrieve an upload folder
	 * @param wrapper
	 * @param id
	 * @return
	 */
	public Folder getUploadFolder(TreeStoreWrapper wrapper)
	{
		Folder ret = null;  //assume failure
		
		if(wrapper != null)
		{
			ret = getFolder(wrapper.getStore(),wrapper.getUploadFolderId());
		}
		
		return ret;
	}	
}
