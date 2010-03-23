package org.iplantc.iptol.client.views.widgets.panels;

import java.util.Arrays;
import java.util.List;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFileDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderDeletedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderRenamedEventHandler;
import org.iplantc.iptol.client.images.Resources;
import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.models.File;
import org.iplantc.iptol.client.models.Folder;
import org.iplantc.iptol.client.services.FolderServices;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataBrowserGrid 
{	
	private String idWorkspace;
	private HandlerManager eventbus;
	private TreeGrid<DiskResource> treeGrid;
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
		
		ColumnConfig name = new ColumnConfig("name",displayStrings.name(),100);  
	    name.setRenderer(new TreeGridCellRenderer<ModelData>());  
		
	    ColumnConfig date = new ColumnConfig("type",displayStrings.description(),150);  	 	    
	    
	    ColumnConfig size = new ColumnConfig("uploaded",displayStrings.uploaded(),150);  
	   	
	   	final ColumnModel columnModel = new ColumnModel(Arrays.asList(name, date, size));  
	   	   	
	   	treeGrid = new TreeGrid<DiskResource>(store,columnModel);
		treeGrid.setBorders(true);  
	    treeGrid.setHeight(260);
	    treeGrid.setWidth(600);
	    treeGrid.getView().setShowDirtyCells(false);
	    treeGrid.setAutoExpandColumn("name");  
	    treeGrid.setTrackMouseOver(false);  
	      
	    treeGrid.setIconProvider(new ModelIconProvider<DiskResource>()
		{
			@Override
			public AbstractImagePrototype getIcon(DiskResource model)
			{
				 if(model instanceof Folder)
				 {
					 return (treeGrid.isExpanded(model)) ? treeGrid.getStyle().getNodeOpenIcon() : treeGrid.getStyle().getNodeCloseIcon();
				 }
				 else
				 {
					 return Resources.ICONS.green();
				 }
			}
		});
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

	private void updateStore(String jsonResult)
	{
		TreeStoreManager builder = TreeStoreManager.getInstance();
		
		builder.updateWrapper(storeWrapper,jsonResult);	
	}
	
	private void initEventHandlers()
	{	
		eventbus.addHandler(FolderCreatedEvent.TYPE,new FolderCreatedEventHandler()
		{
			@Override
			public void onCreated(FolderCreatedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFolderCreate(storeWrapper,event.getId(),event.getName());
			}
		});
		
		eventbus.addHandler(FolderRenamedEvent.TYPE,new FolderRenamedEventHandler()
		{
			@Override
			public void onRenamed(FolderRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFolderRename(storeWrapper,event.getId(),event.getName());			
			}
		});
		
		eventbus.addHandler(FolderDeletedEvent.TYPE,new FolderDeletedEventHandler()
		{
			@Override
			public void onDeleted(FolderDeletedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFolderDelete(storeWrapper,event.getId());				
			}
		});	
		
		eventbus.addHandler(FileUploadedEvent.TYPE,new FileUploadedEventHandler()
		{
			@Override
			public void onUploaded(FileUploadedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFileAdd(storeWrapper,event.getParentId(),event.getFileInfo());
			}
		});	

		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFileRename(storeWrapper,event.getId(),event.getName());
			}
		});	
		
		eventbus.addHandler(FileDeletedEvent.TYPE,new FileDeletedEventHandler()
		{
			@Override
			public void onDeleted(FileDeletedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.doFileDelete(storeWrapper,event.getId());				
			}
		});	
	}
	
	public TreeStoreWrapper getTreeStoreWrapper()
	{
		return storeWrapper;
	}
	
	public List<DiskResource> getSelectedItems()
	{
		List<DiskResource> ret = null;
		
		if(treeGrid != null)
		{
			ret = treeGrid.getSelectionModel().getSelectedItems();
		}
		
		return ret;
	}
	
	public void promptForFolderCreate()
	{
		IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,new AddFolderDialogPanel(idWorkspace,storeWrapper.getRootFolderId(),eventbus));
		dlg.show();
	}
	
	
	public void promptForRename()
	{
		DiskResource selected = treeGrid.getSelectionModel().getSelectedItem();

		if(selected != null)
		{
			IPlantDialog dlg = null;
			
			if(selected instanceof Folder)
			{
				dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFolderDialogPanel(idWorkspace,selected.getId(),selected.getName(),eventbus));
			}
			else if(selected instanceof File)
			{
				dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFileDialogPanel(selected.getId(),selected.getName(),eventbus));
			}
			
			//do we have a dialog to display
			if(dlg != null)
			{
				dlg.show();
			}
		}
	}
}
