package org.iplantc.iptol.client.views.widgets.panels;

import java.util.Arrays;
import java.util.List;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFileDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.iptol.client.events.disk.mgmt.DiskResourceDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.DiskResourceDeletedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FolderCreatedEventHandler;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataBrowserGrid 
{	
	private String idWorkspace;
	private TreeGrid<DiskResource> treeGrid;
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	public DataBrowserGrid(String idWorkspace) 
	{	
		this.idWorkspace = idWorkspace;
		initEventHandlers();
		disableBrowserContextMenu();
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
	
	private static native void disableBrowserContextMenu() /*-{
    	$doc.oncontextmenu = function() { return false; };
	}-*/; 
	
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
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());	
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
		EventBus eventbus = EventBus.getInstance();
		
		//folder added
		eventbus.addHandler(FolderCreatedEvent.TYPE,new FolderCreatedEventHandler()
		{
			@Override
			public void onCreated(FolderCreatedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.createFile(storeWrapper,event.getId(),event.getName());
			}
		});
		
		//folder renamed
		eventbus.addHandler(FolderRenamedEvent.TYPE,new FolderRenamedEventHandler()
		{
			@Override
			public void onRenamed(FolderRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.renameFolder(storeWrapper,event.getId(),event.getName());			
			}
		});
				
		//file uploaded
		eventbus.addHandler(FileUploadedEvent.TYPE,new FileUploadedEventHandler()
		{
			@Override
			public void onUploaded(FileUploadedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.addFile(storeWrapper,event.getParentId(),event.getFileInfo());
			}
		});	
		
		//file save as completed
		eventbus.addHandler(FileSaveAsEvent.TYPE,new FileSaveAsEventHandler()
		{
			@Override
			public void onSaved(FileSaveAsEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.addFile(storeWrapper,event.getParentId(),event.getFileInfo());				
			}
		});

		//file renamed
		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.renameFile(storeWrapper,event.getId(),event.getName());
			}
		});	
		
		//deletions
		eventbus.addHandler(DiskResourceDeletedEvent.TYPE,new DiskResourceDeletedEventHandler()
		{
			@Override
			public void onDeleted(DiskResourceDeletedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.delete(storeWrapper,event.getFolders(),event.getFiles());			
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
		IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,new AddFolderDialogPanel(idWorkspace,storeWrapper.getRootFolderId()));
		dlg.show();
	}
		
	/**
	 * Rename prompt for either file or folder
	 */
	public void promptForRename()
	{
		DiskResource selected = treeGrid.getSelectionModel().getSelectedItem();

		if(selected != null)
		{
			IPlantDialog dlg = null;
			
			if(selected instanceof Folder)
			{
				dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFolderDialogPanel(idWorkspace,selected.getId(),selected.getName()));
			}
			else if(selected instanceof File)
			{
				dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFileDialogPanel(selected.getId(),selected.getName()));
			}
			
			//do we have a dialog to display
			if(dlg != null)
			{
				dlg.show();
			}
		}
	}
	
	/**
	 * Get the upload parent based on the user's selection(s)
	 * @return
	 */
	public String getUploadParentId()
	{
		String ret = storeWrapper.getUploadFolderId();  //by default - let's return the upload folder's id
		
		List<DiskResource> items = getSelectedItems();
		
		//do we only have one item selected
		if(items.size() == 1)
		{
			DiskResource selected = items.get(0);
			
			//if we have a file... let's return the parent's id
			if(selected instanceof File)
			{
				File file = (File)selected;
				Folder parent = (Folder)file.getParent();
				
				ret = parent.getId();
			}
			else
			{
				ret = selected.getId();
			}
		}
		
		return ret;
	}
}
