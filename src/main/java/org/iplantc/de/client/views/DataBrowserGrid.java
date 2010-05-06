package org.iplantc.de.client.views;

import java.util.Arrays;
import java.util.List;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileMovedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileMovedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileUploadedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FolderCreatedEvent;
import org.iplantc.de.client.events.disk.mgmt.FolderCreatedEventHandler;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.DiskResource;
import org.iplantc.de.client.models.File;
import org.iplantc.de.client.models.Folder;
import org.iplantc.de.client.services.FolderServices;
import org.iplantc.de.client.utils.TreeStoreWrapper;
import org.iplantc.de.client.views.dialogs.IPlantDialog;
import org.iplantc.de.client.views.panels.AddFolderDialogPanel;
import org.iplantc.de.client.views.panels.RenameFileDialogPanel;
import org.iplantc.de.client.views.panels.RenameFolderDialogPanel;

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

/**
 * Provides a grid representation of disk resources for user interaction. 
 */
public class DataBrowserGrid
{
	private String idWorkspace;
	private TreeGrid<DiskResource> treeGrid;
	private ColumnModel columnModel;
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private DEDisplayStrings displayStrings = (DEDisplayStrings)GWT.create(DEDisplayStrings.class);
	private DEErrorStrings errorStrings = (DEErrorStrings)GWT.create(DEErrorStrings.class);

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

		ColumnConfig name = new ColumnConfig("name", displayStrings.name(), 240);
		name.setRenderer(new TreeGridCellRenderer<ModelData>());

		ColumnConfig date = new ColumnConfig("type", displayStrings.description(), 190);

		ColumnConfig size = new ColumnConfig("uploaded", displayStrings.uploaded(), 150);

		columnModel = new ColumnModel(Arrays.asList(name, date, size));

		treeGrid = new TreeGrid<DiskResource>(store, columnModel);
		treeGrid.setBorders(true);
		treeGrid.setHeight(260);
		treeGrid.setWidth(600);
		treeGrid.getView().setShowDirtyCells(false);
		treeGrid.setTrackMouseOver(false);
		treeGrid.getTreeView().setBufferEnabled(false);
		treeGrid.getTreeView().setCacheSize(10000);

		treeGrid.setIconProvider(new ModelIconProvider<DiskResource>()
		{
			@Override
			public AbstractImagePrototype getIcon(DiskResource model)
			{
				if(model instanceof Folder)
				{
					return (treeGrid.isExpanded(model)) ? treeGrid.getStyle().getNodeOpenIcon()
							: treeGrid.getStyle().getNodeCloseIcon();
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
		FolderServices.getFiletree(idWorkspace, new AsyncCallback<String>()
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
		storeWrapper.updateWrapper(jsonResult);
	}

	private void initEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();

		// folder added
		eventbus.addHandler(FolderCreatedEvent.TYPE, new FolderCreatedEventHandler()
		{
			@Override
			public void onCreated(FolderCreatedEvent event)
			{
				storeWrapper.createFolder(event.getId(), event.getName());
			}
		});

		// file uploaded
		eventbus.addHandler(FileUploadedEvent.TYPE, new FileUploadedEventHandler()
		{
			@Override
			public void onUploaded(FileUploadedEvent event)
			{
				storeWrapper.addFile(event.getParentId(), event.getFileInfo(), event.getDeleteIds());
			}
		});

		// file save as completed
		eventbus.addHandler(FileSaveAsEvent.TYPE, new FileSaveAsEventHandler()
		{
			@Override
			public void onSaved(FileSaveAsEvent event)
			{
				storeWrapper.addFile(event.getParentId(), event.getFileInfo(), null);
			}
		});

		// folder/file renamed
		eventbus.addHandler(DiskResourceRenamedEvent.TYPE, new DiskResourceRenamedEventHandler()
		{
			@Override
			public void onFolderRenamed(DiskResourceRenamedEvent event)
			{
				storeWrapper.renameFolder(event.getId(), event.getName());
			}

			@Override
			public void onFileRenamed(DiskResourceRenamedEvent event)
			{
				storeWrapper.renameFile(event.getId(), event.getName());
			}
		});

		// deletions
		eventbus.addHandler(DiskResourceDeletedEvent.TYPE, new DiskResourceDeletedEventHandler()
		{
			@Override
			public void onDeleted(DiskResourceDeletedEvent event)
			{
				storeWrapper.delete(event.getFolders(), event.getFiles());
			}
		});

		// file moved
		eventbus.addHandler(FileMovedEvent.TYPE, new FileMovedEventHandler()
		{
			@Override
			public void onMoved(FileMovedEvent event)
			{
				storeWrapper.moveFile(event.getFolderId(), event.getFileId());
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
		IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(), 320, new AddFolderDialogPanel(
				idWorkspace, storeWrapper.getRootFolderId()));
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
				dlg = new IPlantDialog(displayStrings.rename(), 320, new RenameFolderDialogPanel(
						idWorkspace, selected.getId(), selected.getName()));
			}
			else if(selected instanceof File)
			{
				dlg = new IPlantDialog(displayStrings.rename(), 320, new RenameFileDialogPanel(selected
						.getId(), selected.getName()));
			}

			// do we have a dialog to display
			if(dlg != null)
			{
				dlg.show();
			}
		}
	}

	/**
	 * Get the upload parent based on the user's selection(s)
	 * 
	 * @return
	 */
	public String getUploadParentId()
	{
		String ret = storeWrapper.getUploadFolderId(); // by default - let's return the
														// upload folder's id

		List<DiskResource> items = getSelectedItems();

		// do we only have one item selected
		if(items.size() == 1)
		{
			DiskResource selected = items.get(0);

			// if we have a file... let's return the parent's id
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
