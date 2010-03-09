package org.iplantc.iptol.client.views.widgets;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.List;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.FolderUpdateEvent;
import org.iplantc.iptol.client.events.FolderUpdateEventHandler;
import org.iplantc.iptol.client.images.Resources;
import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.models.File;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.models.Folder;
import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.services.FolderUpdater;
import org.iplantc.iptol.client.views.widgets.panels.StoreBuilder;
import org.iplantc.iptol.client.views.widgets.panels.TreeStoreWrapper;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A widget that displays files/folders in tree structure.
 * provides context menu for uploading, downloading etc...
 * @author sriram
 *
 */
public class DataBrowserTree extends ContentPanel
{
	private Dialog upload_dialog;
	private HandlerManager eventbus;
	private Button options = new Button();
	private String idWorkspace;
	private static final String SERVLET_PATH = "servlet.gupld";
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private TreePanel<DiskResource> treePanel = new TreePanel<DiskResource>(storeWrapper.getStore());
	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);

	public DataBrowserTree(String idWorkspace,HandlerManager eventbus)
	{
		this.idWorkspace = idWorkspace;
		this.eventbus = eventbus;

		setScrollMode(Scroll.AUTO);

		initEventHandlers();
	}

	/**
	 * Put together the widget
	 */
	public void assembleView()
	{
		treePanel.setBorders(true);
		treePanel.setDisplayProperty("name");
		treePanel.setContextMenu(buildFolderContextMenu());
		treePanel.setAutoHeight(true);
		treePanel.setWidth(195);
		
		add(treePanel);
		
		setHeading(displayStrings.dataBrowser());

		options.setScale(ButtonScale.SMALL);
		options.setArrowAlign(ButtonArrowAlign.RIGHT);
		options.setIcon(Resources.ICONS.listItems());
		options.setMenu(buildOptionsMenu());

		getHeader().addTool(options);

		//provide icons to the tree nodes
		treePanel.setIconProvider(new ModelIconProvider<DiskResource>()
		{
			@Override
			public AbstractImagePrototype getIcon(DiskResource model)
			{
				 if(model instanceof Folder)
				 {
					 return (treePanel.isExpanded(model)) ? treePanel.getStyle().getNodeOpenIcon() : treePanel.getStyle().getNodeCloseIcon();
				 }
				 else
				 {
					 return Resources.ICONS.green();
				 }
			}
		});

		//retrieve all the files that have been uploaded already
		refreshTree();

		//load info about the file on the status bar
		treePanel.addListener(Events.OnClick,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				DiskResource folder = treePanel.getSelectionModel().getSelectedItem();

				//set context menu
				if(folder instanceof Folder)
				{
					treePanel.setContextMenu(buildFolderContextMenu());
				}
				else
				{
					treePanel.setContextMenu(buildFileContextMenu());
				}

				DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(folder);
	 			eventbus.fireEvent(event);
			}
		});
	}

	/**
	 * Build menu for the data browser tree
	 * @return
	 */
	private Menu buildOptionsMenu()
	{
		final Menu optionsMenu = new Menu();

		MenuItem uploadItem = new MenuItem(displayStrings.upload());
		uploadItem.setIcon(Resources.ICONS.upload());
		uploadItem.setId("upload_menu_item_option");
		uploadItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				promptUpload(null,optionsMenu.getPosition(false));
			}
		});

		MenuItem createFolder = new MenuItem();
		createFolder.setText(displayStrings.createFolder());
		createFolder.setIcon(Resources.ICONS.add());
		createFolder.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,new AddFolderDialogPanel(idWorkspace,storeWrapper.getRootId(),eventbus));
				dlg.show();
			}
		});

		optionsMenu.add(uploadItem);
		optionsMenu.add(createFolder);

		return optionsMenu;
	}

	/**
	 * Helper method to build upload menu item
	 * @return
	 */
	private MenuItem buildFileUploadMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setId("upload_menu_item");
		ret.setText(displayStrings.upload());
		ret.setIcon(Resources.ICONS.upload());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					promptUpload(selected.getId(),ce.getXY());
				}
			}
		});

		return ret;
	}

	/**
	 * Helper method to build rename menu item
	 * @return
	 */
	private MenuItem buildFolderRenameMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.rename());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					IPlantDialog dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFolderDialogPanel(selected.getId(),selected.getName(),eventbus));
					dlg.show();
				}
			}
		});

		return ret;
	}

	/**
	 * Helper method to build delete menu item
	 * @return
	 */
	private MenuItem buildFolderDeleteMenuItem()
	{
		MenuItem ret = new MenuItem();

		ret.setIcon(Resources.ICONS.edit());
		ret.setText(displayStrings.delete());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					FolderServices.deleteFolder(selected.getId(),new FolderUpdater(eventbus));
				}
			}
		});

		return ret;
	}

	/**
	 * Attach context menu to DataBrowserTree
	 * @return
	 */
	private Menu buildFolderContextMenu()
	{
		Menu contextMenu = new Menu();

		contextMenu.add(buildFileUploadMenuItem());
		contextMenu.add(buildFolderRenameMenuItem());
		contextMenu.add(buildFolderDeleteMenuItem());

		return contextMenu;
	}
	
	/**
	 * Call service to save a nexus file to the desktop
	 * @param id
	 * @return
	 */
	private void downloadFileToDesktop(String id)
	{
		String address = constants.fileDownloadService() + id + "/content";
		Window.open(address,null,null);
	}
	
	private Menu buildFileContextMenu()
	{
		Menu contextMenu = new Menu();
		MenuItem saveNexus = new MenuItem();

		saveNexus.setText(displayStrings.saveNexusFile());
		saveNexus.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					downloadFileToDesktop(selected.getId());
				}
			}
		});

		contextMenu.add(saveNexus);

		return contextMenu;
	}

	/**
	 * Display dialog for file upload
	 * @param p XY coordinate at which the prompt should be displayed
	 */
	private void promptUpload(String parentId,Point p)
	{
		UploadPanel upload_panel = new UploadPanel(parentId,displayStrings.uploadYourData(),SERVLET_PATH,onFinishUploaderHandler);
		upload_panel.assembleComponents();

		upload_dialog = new Dialog();
		upload_dialog.add(upload_panel);
		upload_dialog.setPagePosition(p);
		upload_dialog.setHeaderVisible(true);
		upload_dialog.setHeading(displayStrings.uploadAFile());
		upload_dialog.setButtons(Dialog.CANCEL);
		upload_dialog.setHideOnButtonClick(true);
		upload_dialog.setWidth(375);
		upload_dialog.show();
	}

	/**
	 * Call back method for file upload submit
	 */
	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler()
	{
		public void onFinish(IUploader uploader)
		{
			if(uploader.getStatus() == Status.SUCCESS)
			{
				TreeStore<DiskResource> store = storeWrapper.getStore();
				DiskResource folder = treePanel.getSelectionModel().getSelectedItem();

				if(folder == null)
				{
					List<DiskResource> folders = store.getRootItems();

					//add to the default folder (Data)
					folder = folders.get(0);
				}

				String response = uploader.getServerResponse();

				if(response != null)
				{
					JsArray<FileInfo> fileInfo = asArrayofFileData(response);

					//there is always only one record
					if(fileInfo != null)
					{
						FileInfo info = fileInfo.get(0);

						if(info != null)
						{
							File child = new File(info);
							
							if(!(folder instanceof Folder))
							{
								folder = (Folder) folder.getParent();
							}

							store.add(folder,child,true);
							child.setParent(folder);
							folder.add(child);

							//highlight the newly added item and notify the application
							treePanel.getSelectionModel().select(child,false);
							DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(child);
							eventbus.fireEvent(event);

							//ajm - we need a mechanism for updating everything but us
							//FolderEvent event = new FolderEvent(FolderEvent.Action.UPDATE_COMPLETE);
							//eventbus.fireEvent(event);
							
							Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());;
						}
					} 
				}

				treePanel.setExpanded(folder,true);
			}
			else
			{
				MessageBox.alert(displayStrings.fileUpload(),displayStrings.fileUploadFailed(),null);
			}

			if(upload_dialog != null)
			{
				upload_dialog.hide();
			}
		}
	};

	/**
	 * A native method to eval returned json
	 * @param json
	 * @return
	 */
	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;


	/**
	 * Update the tree panel with retrieved results
	 * @param jsonResult
	 */
	private void updateStore(String jsonResult)
	{
		StoreBuilder builder = StoreBuilder.getInstance();
		
		builder.updateWrapper(storeWrapper,jsonResult);	
		
		treePanel.expandAll();
	}

	/**
	 * Call service to refresh our tree
	 * @param name
	 */
	private void refreshTree()
	{
		FolderServices.getFiletree(idWorkspace,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				//TODO: handle failure
			}

			@Override
			public void onSuccess(String result)
			{
				updateStore(result);
			}
		});
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
				refreshTree();
			}
        });
	}
}
