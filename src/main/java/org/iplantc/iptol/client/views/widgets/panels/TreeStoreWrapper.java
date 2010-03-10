package org.iplantc.iptol.client.views.widgets.panels;

import org.iplantc.iptol.client.models.DiskResource;

import com.extjs.gxt.ui.client.store.TreeStore;

public class TreeStoreWrapper 
{
	///////////////////////////////////////
	//private variables
	private String rootId = "-1";
	private TreeStore<DiskResource> store = new TreeStore<DiskResource>();
	
	///////////////////////////////////////
	//public methods
	public void setRootId(String id)
	{
		rootId = id;
	}
	
	///////////////////////////////////////
	public String getRootId()
	{
		return rootId;
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
