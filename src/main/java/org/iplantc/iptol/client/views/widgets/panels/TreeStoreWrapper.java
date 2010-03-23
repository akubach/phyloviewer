package org.iplantc.iptol.client.views.widgets.panels;

import org.iplantc.iptol.client.models.DiskResource;

import com.extjs.gxt.ui.client.store.TreeStore;

public class TreeStoreWrapper 
{
	///////////////////////////////////////
	//private variables
	private String rootFolderId = "-1";
	private String uploadFolderId = "";
	private TreeStore<DiskResource> store = new TreeStore<DiskResource>();
	
	///////////////////////////////////////
	//public methods
	public void setRootFolderId(String id)
	{
		rootFolderId = id;
	}
	
	///////////////////////////////////////
	public String getRootFolderId()
	{
		return rootFolderId;
	}

	///////////////////////////////////////
	public void setUploadFolderId(String id)
	{
		uploadFolderId = id;
	}

	///////////////////////////////////////
	public String getUploadFolderId()
	{
		return uploadFolderId;
	}

	///////////////////////////////////////
	public void setStore(TreeStore<DiskResource> store)
	{
		this.store = store;
	}
	
	///////////////////////////////////////
	public TreeStore<DiskResource> getStore()
	{
		return store;
	}
	
	///////////////////////////////////////
	public void clearStore()
	{
		store.removeAll();
	}
}
