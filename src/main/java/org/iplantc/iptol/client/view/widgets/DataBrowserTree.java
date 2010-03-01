package org.iplantc.iptol.client.view.widgets;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.List;
import java.util.Set;

import org.iplantc.iptol.client.File;
import org.iplantc.iptol.client.FileInfo;
import org.iplantc.iptol.client.Folder;
import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolServiceFacade;
import org.iplantc.iptol.client.ServiceCallWrapper;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.FolderEvent;
import org.iplantc.iptol.client.events.FolderEventHandler;
import org.iplantc.iptol.client.images.Resources;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A widget that displays files/folders in tree structure.
 * provides context menu for uploading, downloading etc...
 * @author sriram
 *
 */
public class DataBrowserTree extends ContentPanel
{
	private static final String SERVLET_PATH = "servlet.gupld";

	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);

	private TreeStore<File> store = null;
	private TreePanel<File> treePanel = null;

	private Dialog upload_dialog = null;
	private String rootId = new String();

	private HandlerManager eventbus;
	private Button options;

	public DataBrowserTree(HandlerManager eventbus)
	{
		store = new TreeStore<File>();
		treePanel = new TreePanel<File>(store);
		this.eventbus = eventbus;

		options = new Button();
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
		treePanel.setIconProvider(new ModelIconProvider<File>()
		{
			@Override
			public AbstractImagePrototype getIcon(File model)
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
		getFilesInfo();

		//load info about the file on the status bar
		treePanel.addListener(Events.OnClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				File folder = treePanel.getSelectionModel().getSelectedItem();

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
				promptUpload(optionsMenu.getPosition(false));
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
				IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,new AddFolderDialogPanel(eventbus));
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
				File selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					promptUpload(ce.getXY());
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
				File selected = treePanel.getSelectionModel().getSelectedItem();

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
				File selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					FolderEvent event = new FolderEvent(FolderEvent.Action.DELETE,selected.getName(),selected.getId());
					eventbus.fireEvent(event);
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
				File selected = treePanel.getSelectionModel().getSelectedItem();

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
	private void promptUpload(Point p)
	{
		UploadPanel upload_panel = new UploadPanel(displayStrings.uploadYourData(),SERVLET_PATH,onFinishUploaderHandler);
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
				File folder = (File) treePanel.getSelectionModel().getSelectedItem();

				if(folder == null)
				{
					List<File> folders = store.getRootItems();

					//add to the default folder (Data)
					folder = folders.get(0);
				}

				String response = uploader.getServerResponse();

				if(response != null)
				{
					refreshTree();
					Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());

				/*	 JsArray<FileInfo> fileInfo = asArrayofFileData(response);

					 //there is always only one record
					 if(fileInfo != null)
					 {
						 FileInfo info = fileInfo.get(0);

						 if(info != null)
						 {
							 File child = new File(Integer.toString(info.getId()),info.getName());
							 if(!(folder instanceof Folder))
							 {
								 folder = (Folder) folder.getParent();
							 }

							 store.add(folder,child,true);
							 child.setParent(folder);
							 child.setInfo(info);
							 folder.add(child);

							 //highlight the newly added item and notify the application
							 treePanel.getSelectionModel().select(child,false);
							 DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(child);
							 eventbus.fireEvent(event);

							 Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());
						 }
					 } */
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
	 * Private method to retrieve list of all uploaded files
	 */
	private void getFilesInfo()
	{
		refreshTree();
	}

	/**
	 * Build a label from a json string
	 *
	 * @param label
	 *
	 * @return
	 */
	private String buildLabel(JSONValue label)
	{
		String temp = label.toString();

		//remove beginning and trailing quotes
		return temp.substring(1,temp.length() - 1);
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
	 * Add a folder to our tree view
	 * @param json
	 */
	private void addFolder(Folder parent,JSONObject json)
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
				label = buildLabel(json.get("label"));
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

		//create
		Folder folder = new Folder(id,label);
		folder.setParent(parent);

		if(parent == null)
		{
			rootId = id;
		}
		else
		{
			store.add(folder,true);
		}

		if(fileInfos != null)
		{
			for(int i = 0;i < fileInfos.length();i++)
			{
				 FileInfo info = fileInfos.get(i);
				 File child = new File(info.getId(),info.getName());

				 store.add(store.findModel(folder),child,true);
				 child.setParent(store.findModel(folder));
				 child.setInfo(info);
				 store.findModel(folder).add(child);
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

	/**
	 * Update the tree panel with retrieved results
	 * @param jsonResult
	 */
	private void updateStore(String jsonResult)
	{
		if(jsonResult != null)
		{
			JSONObject jsonRoot = (JSONObject)JSONParser.parse(jsonResult);

			//if we got this far, we have a tag for the root
			JSONObject root = (JSONObject) jsonRoot.get("rootFolder");

			if(root != null)
			{
				addFolder(null,root);
			}

			store.sort("name",SortDir.DESC);
			treePanel.expandAll();
		}
	}

	/**
	 * Call service to refresh our tree
	 * @param name
	 */
	private void refreshTree()
	{
		store.removeAll();
		IptolServiceFacade.getInstance().getServiceData(new ServiceCallWrapper(constants.fileTreeRetrievalService()),new FileTreeListUpdater());
	}

	/**
	 * Call service to create a new folder
	 * @param name
	 */
	private void createFolder(String name)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,constants.folderService() + rootId + "/folders","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,new TreeUpdater());
	}

	/**
	 * Call service to rename a folder
	 * @param id
	 * @param name
	 */
	private void renameFolder(String id,String name)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT,constants.folderService() + id + "/label","{\"label\":\"" + name + "\"}");
		IptolServiceFacade.getInstance().getServiceData(wrapper,new TreeUpdater());
	}

	/**
	 * Call service to delete a folder
	 * @param id
	 */
	private void deleteFolder(String id)
	{
		ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE,constants.folderService() + id,"");
		IptolServiceFacade.getInstance().getServiceData(wrapper,new TreeUpdater());
	}

	/**
	 * Setup our application event handlers
	 */
	private void initEventHandlers()
	{
        //register perspective change handler
		eventbus.addHandler(FolderEvent.TYPE,new FolderEventHandler()
        {
			@Override
			public void onCreate(FolderEvent event)
			{
				createFolder(event.getName());
			}

			@Override
			public void onRename(FolderEvent event)
			{
				renameFolder(event.getId(),event.getName());
			}

			@Override
			public void onDelete(FolderEvent event)
			{
				deleteFolder(event.getId());
			}
        });
	}

	class FileTreeListUpdater implements AsyncCallback<String>
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
	}

	class TreeUpdater implements AsyncCallback<String>
	{
		@Override
		public void onFailure(Throwable caught)
		{
			//TODO: handle failure
		}

		@Override
		public void onSuccess(String result)
		{
			//if we update our tree, we need to re-capture our tree
			refreshTree();
		}
	}
	
	class DoNothing implements AsyncCallback<String>
	{
		@Override
		public void onFailure(Throwable caught)
		{
			//TODO: handle failure
		}

		@Override
		public void onSuccess(String result)
		{
			// do absolutely nothing
		}
	}
}
