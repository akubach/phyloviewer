package org.iplantc.iptol.client.view.widgets;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.AbstractAsyncHandler;
import org.iplantc.iptol.client.File;
import org.iplantc.iptol.client.FileInfo;
import org.iplantc.iptol.client.Folder;
import org.iplantc.iptol.client.IptolConstants;
import org.iplantc.iptol.client.IptolServiceFacade;
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
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A widget that displays files/folders in tree structure.
 * provides context menu for uploading, downloading etc...
 * @author sriram
 *
 */
public class DataBrowserTree extends ContentPanel 
{
	private static final String SERVLET_PATH = "servlet.gupld";

	private IptolConstants constants = (IptolConstants) GWT.create(IptolConstants.class);
	
	private TreeStore<File> store = null;
	private TreePanel<File> treePanel = null;
	
	private Dialog upload_dialog = null;
	
	private HandlerManager eventbus;
	private Button options;
	
	private Folder defaultParent = null;
		
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

		add(treePanel);
		setWidth(175);
		setHeading(constants.dataBrowser());
		
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
	
		//add default folder
		store.add(getTreeModel(),true);
		
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
		
		MenuItem uploadItem = new MenuItem(constants.upload());
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
		createFolder.setText(constants.createFolder());
		createFolder.setIcon(Resources.ICONS.add());
		createFolder.addSelectionListener(new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent ce) 
			{
				IPlantDialog dlg = new IPlantDialog(constants.newFolder(),320,new AddFolderDialogPanel(eventbus));					
				dlg.show();
			}			
		});
		
		optionsMenu.add(uploadItem);
		optionsMenu.add(createFolder);
		
		return optionsMenu;
	}
	
	/**
	 * A method to retrieve the folder structure for the data browser.
	 * This has to be rpc call. for now, mock it
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<File> getTreeModel() 
	{
		ArrayList folders = new ArrayList();
		Folder dataFolder = new Folder(constants.data());
		defaultParent = dataFolder;
		folders.add(dataFolder);
		return folders;
	}
	
	/**
	 * Attach context menu to DataBrowserTree
	 * @return
	 */
	private Menu buildFolderContextMenu() 
	{
		Menu contextMenu = new Menu();  
		MenuItem upload = new MenuItem(); 
		
		upload.setId("upload_menu_item");
		upload.setText(constants.upload());
		upload.setIcon(Resources.ICONS.upload());
		upload.addSelectionListener(new SelectionListener<MenuEvent>() 
		{  
			@Override
			public void componentSelected(MenuEvent ce) 
			{  
				File folder = treePanel.getSelectionModel().getSelectedItem();  
				
				if(folder != null) 
				{  
					promptUpload(ce.getXY());
				}  
			}  
		});
		
		MenuItem rename = new MenuItem();
		rename.setIcon(Resources.ICONS.edit());
		rename.setText(constants.rename());
		rename.addSelectionListener(new SelectionListener<MenuEvent>() 
		{  
			@Override
			public void componentSelected(MenuEvent ce) 
			{  
				File folder = treePanel.getSelectionModel().getSelectedItem();  
				
				if(folder != null) 
				{  					
					IPlantDialog dlg = new IPlantDialog(constants.rename(),320,new RenameFolderDialogPanel(folder.getName(),eventbus));					
					dlg.show();				
				}  
			}  
		});
		
		contextMenu.add(upload);
		contextMenu.add(rename);
		
		return contextMenu;
	}
	
	private Menu buildFileContextMenu() 
	{
		Menu contextMenu = new Menu();  
		MenuItem saveNexus = new MenuItem(); 
	
		saveNexus.setText(constants.saveNexusFile());
		saveNexus.addSelectionListener(new SelectionListener<MenuEvent>() 
		{  
			@Override
			public void componentSelected(MenuEvent ce) 
			{  
				File folder = treePanel.getSelectionModel().getSelectedItem();  
				
				if(folder != null) 
				{  
					Window.alert(constants.thisWillAllowUserToSaveAFile());
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
		UploadPanel upload_panel = new UploadPanel(constants.uploadYourData(),SERVLET_PATH,onFinishUploaderHandler);
		upload_panel.assembleComponents();
		
		upload_dialog = new Dialog();
		upload_dialog.add(upload_panel);
		upload_dialog.setPagePosition(p);
		upload_dialog.setHeaderVisible(true);
		upload_dialog.setHeading(constants.uploadAFile());
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
					 JsArray<FileInfo> fileInfo = asArrayofFileData(response);
					 
					 //there is always only one record
					 if(fileInfo != null) 
					 {
						 FileInfo info = fileInfo.get(0);

						 if(info != null) 
						 {
							 File child = new File(info.getName());
							 if(false == folder instanceof  Folder) 
							 {
								 folder = (Folder) folder.getParent();
							 }
							 
							 store.add(folder, child,true);
							 child.setParent(folder);
							 child.setInfo(info);
							 folder.add(child);
							 
							 //highlight the newly added item and notify the application
							 treePanel.getSelectionModel().select(child,false);
							 DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(child);
							 eventbus.fireEvent(event);
							 
							 Info.display(constants.fileUpload(),constants.fileUploadSuccess());
						 }
					 }
				}
				
				treePanel.setExpanded(folder,true);
			} 
			else 
			{
				MessageBox.alert(constants.fileUpload(),constants.fileUploadFailed(),null);
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
		IptolServiceFacade.getInstance().getServiceData(constants.filesListService(),new FilesListUpdater<String>());
	}
	
	/**
	 * Update the tree panel with retrieved results
	 * @param jsonResult
	 */
	private void updateStore(String jsonResult) 
	{
		if(jsonResult != null) 
		{
			JsArray<FileInfo> fileInfos =  asArrayofFileData(jsonResult);
		
			for(int i = 0;i < fileInfos.length();i++) 
			{
				 FileInfo info = fileInfos.get(i);
				 File child = new File(info.getName());
				 store.add(store.findModel(defaultParent), child,true);
				 child.setParent(store.findModel(defaultParent));
				 child.setInfo(info);
				 store.findModel(defaultParent).add(child);
			}
			
			treePanel.expandAll();
		}		
	}

	private void initEventHandlers()
	{
        //register perspective change handler
		eventbus.addHandler(FolderEvent.TYPE,new FolderEventHandler()
        {   
			@Override
			public void onCreate(FolderEvent event) 
			{
				Folder f = new Folder(event.getName());
				store.add(f,true);	
			}
			
			@Override
			public void onRename(FolderEvent event) 
			{
				Folder f = (Folder) treePanel.getSelectionModel().getSelectedItem();
				f.set("name",event.getName());
				store.update(f);								
			}			
        });					
	}
	
	/**
	 * 
	 * @author sriram
	 * @param <String>
	 * Callback for treeFilesListService
	 */
	@SuppressWarnings("hiding")
	class FilesListUpdater<String> extends AbstractAsyncHandler 
	{
		@Override
		public void handleFailure(Throwable caught) 
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void handleSuccess(Object result) 
		{
			updateStore((java.lang.String) result);
		}
	}
}
