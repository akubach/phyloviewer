package org.iplantc.iptol.client.views.widgets.panels;

import java.util.Arrays;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.events.FolderUpdateEvent;
import org.iplantc.iptol.client.events.FolderUpdateEventHandler;
import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.services.FolderServices;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DataBrowserGrid 
{	
	private String idWorkspace;
	private HandlerManager eventbus;
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	public DataBrowserGrid(String idWorkspace,HandlerManager eventbus) 
	{	
		this.idWorkspace = idWorkspace;
		this.eventbus = eventbus;
		
		initEventHandlers();
	}
		
	public TreeGrid<DiskResource> assembleView() 
	{
		TreeStore<DiskResource> store = storeWrapper.getStore();
		
		getFilesInfo();
		
		CheckBoxSelectionModel<DiskResource> checkbox = new CheckBoxSelectionModel<DiskResource>(); 

		ColumnConfig name = new ColumnConfig("name",displayStrings.name(),100);  
	    name.setRenderer(new TreeGridCellRenderer<ModelData>());  
		   
	    ColumnConfig date = new ColumnConfig("type",displayStrings.description(),150);  
	   
	    ColumnConfig size = new ColumnConfig("uploaded",displayStrings.uploaded(),150);  
	   
	    final ColumnModel columnModel = new ColumnModel(Arrays.asList(checkbox.getColumn(),name, date, size));  

	    TreeGrid<DiskResource> treeGrid = new TreeGrid<DiskResource>(store,columnModel);  
	    treeGrid.setBorders(true);  
	    treeGrid.setHeight(260);
	    treeGrid.setWidth(600);
	    treeGrid.setSelectionModel(checkbox);
	    treeGrid.addPlugin(checkbox);
	    treeGrid.setAutoExpandColumn("name");  
	    treeGrid.setTrackMouseOver(false);  
	    
	    return treeGrid;
	}
	
	/**
	 * Private method to retrieve list of all uploaded files
	 */
	private void getFilesInfo() 
	{
		FolderServices.getFiletree(idWorkspace,new AsyncCallback<String>() 
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				// TODO handle failure			
			}

			@Override
			public void onSuccess(String result) 
			{
				updateStore(result);			
			}
		});
	}

	/**
	 * Update the tree panel with retrieved results
	 * @param jsonResult
	 */
	private void updateStore(String jsonResult)
	{
		StoreBuilder builder = StoreBuilder.getInstance();
		
		builder.updateWrapper(storeWrapper,jsonResult);	
	}
	
	/**
	 * Setup our application event handlers
	 */
	private void initEventHandlers()
	{
        //register folder event handler
		eventbus.addHandler(FolderUpdateEvent.TYPE,new FolderUpdateEventHandler()
        {
			@Override
			public void onUpdateComplete() 
			{
				getFilesInfo();
			}
        });
	}
}
