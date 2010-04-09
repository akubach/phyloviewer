package org.iplantc.iptol.client.views.widgets;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.JsonBuilder;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.ImportDialog;
import org.iplantc.iptol.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFileDialogPanel;
import org.iplantc.iptol.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.events.GetDataEvent;
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
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.models.Folder;
import org.iplantc.iptol.client.services.DiskResourceDeleteCallback;
import org.iplantc.iptol.client.services.FolderServices;
import org.iplantc.iptol.client.views.widgets.panels.TreeStoreManager;
import org.iplantc.iptol.client.views.widgets.panels.TreeStoreWrapper;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
	private Button options = new Button();
	private String idWorkspace;
	private Menu contextMenuFile;
	private Menu contextMenuFolder;
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private TreePanel<DiskResource> treePanel = new TreePanel<DiskResource>(storeWrapper.getStore());
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	public DataBrowserTree(String idWorkspace)
	{
		this.idWorkspace = idWorkspace;
		
		setScrollMode(Scroll.AUTOY);

		//create our context menus
		contextMenuFile = buildFileContextMenu();
		contextMenuFolder = buildFolderContextMenu();
		
		initEventHandlers();
		initDragAndDrop();
	}

	/**
	 * Put together the widget
	 */
	public void assembleView()
	{
		treePanel.setBorders(true);
		treePanel.setDisplayProperty("name");
		treePanel.setContextMenu(buildFolderContextMenu());
		treePanel.setWidth("100%");
		treePanel.setHeight("99%");
		//disable multi-select
		TreePanelSelectionModel<DiskResource> sm = new TreePanelSelectionModel<DiskResource>();
		sm.setSelectionMode(SelectionMode.SINGLE);
		treePanel.setSelectionModel(sm);  
		
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

		//handle our context menu events
		treePanel.getSelectionModel().addListener(Events.Select,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				showContextMenu();
			}
		});
		
		treePanel.getSelectionModel().addListener(Events.SelectionChange,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				showContextMenu();
			}
		});
	}

	private String getFolderId()
	{
		String ret = null;
	
		DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
		
		//do we have an item selected?
		if(selected != null)
		{
			//do we have a folder selected?
			if(selected instanceof Folder)
			{
				ret = selected.getId();				
			}
			else
			{	
				//we have a file selected - let's upload to the parent
				Folder parent = (Folder)selected.getParent();
				
				ret = parent.getId();
			}			
		}
		else
		{
			ret = storeWrapper.getUploadFolderId();
		}
		
		return ret;
	}
	
	private void showContextMenu()
	{
		DiskResource folder = treePanel.getSelectionModel().getSelectedItem();

		//set context menu
		if(folder instanceof Folder)
		{
			treePanel.setContextMenu(contextMenuFolder);
		}
		else
		{
			treePanel.setContextMenu(contextMenuFile);
		}
		
		EventBus eventbus = EventBus.getInstance();
		DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(folder);
		eventbus.fireEvent(event);
	}
	
	private MenuItem buildCreateFolderMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.createFolder());
		
		ret.setIcon(Resources.ICONS.add());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,new AddFolderDialogPanel(idWorkspace,storeWrapper.getRootFolderId()));
				dlg.show();
			}
		});
		
		return ret;
	}

	private MenuItem buildHelpMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.help());
				
		ret.setIcon(Resources.ICONS.user());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				//TODO: implement me
			}
		});
		
		return ret;
	}
	
	private Menu buildOptionsMenu()
	{
		final Menu optionsMenu = new Menu();
		
		optionsMenu.add(buildImportMenuItem());
		optionsMenu.add(buildFileUploadMenuItem());
		optionsMenu.add(buildCreateFolderMenuItem());
		optionsMenu.add(buildHelpMenuItem());
		
		return optionsMenu;
	}

	private MenuItem buildFileUploadMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.upload());

		ret.setId("upload_menu_item");
		ret.setIcon(Resources.ICONS.upload());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{				
				promptUpload(getFolderId(),ce.getXY());
			}
		});

		return ret;
	}

	private MenuItem buildContextFileUploadMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.upload());

		ret.setId("upload_menu_item");		
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

	private MenuItem buildFolderRenameMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.rename());

		ret.setIcon(Resources.ICONS.edit());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					IPlantDialog dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFolderDialogPanel(idWorkspace,selected.getId(),selected.getName()));
					dlg.show();
				}
			}
		});

		return ret;
	}

	private void deleteFolder(String id)
	{
		String json = JsonBuilder.buildDeleteFolderString(id);
		
		if(json != null)
		{
			DiskResourceDeleteCallback callback = new DiskResourceDeleteCallback();
			callback.addFolder(id);
			
			FolderServices.deleteDiskResources(idWorkspace,json,callback);
		}	
	}

	private void deleteFile(String id)
	{

		String json = JsonBuilder.buildDeleteFileString(id);
		
		if(json != null)
		{
			DiskResourceDeleteCallback callback = new DiskResourceDeleteCallback();
			callback.addFile(id);
			
			FolderServices.deleteDiskResources(idWorkspace,json,callback);
		}	
	}
	
	private MenuItem buildFolderDeleteMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.delete());

		ret.setIcon(Resources.ICONS.edit());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				final DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
				
				if(selected != null)
				{
					if(selected instanceof Folder)
					{
						Folder folder = (Folder)selected;
						
						//does this folder have files?
						if(folder.getChildCount() > 0)
						{
							//warn the user they are about to delete a folder with files
							MessageBox.confirm(displayStrings.warning(),displayStrings.folderDeleteWarning(),new Listener<MessageBoxEvent>() 
							{  
								public void handleEvent(MessageBoxEvent ce) 
								{  
									Button btn = ce.getButtonClicked();  
									
									//did the user click yes?
									if(btn.getItemId().equals("yes"))
									{									
										deleteFolder(selected.getId());																				
									}	
								}  
							});						
						}
						else
						{
							//we have an empty folder selected - proceed with delete							
							deleteFolder(selected.getId());
						}						
					}
				}
						
			}
		});

		return ret;
	}

	private MenuItem buildFileDeleteMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.delete());

		ret.setIcon(Resources.ICONS.edit());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					deleteFile(selected.getId());
				}
			}
		});

		return ret;
	}
	
	private MenuItem buildFileDownloadMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.downloadFile());
		
		ret.setIcon(Resources.ICONS.edit());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				final DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
				
				if(selected != null)
				{
					String address = "http://" + Window.Location.getHostName() + ":14444/files/" + selected.getId() + "/content";
					Window.open(address,null,null);			
				}
			}
		});	
		
		return ret;
	}
	
	private MenuItem buildFileEditMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.edit());
		
		ret.setIcon(Resources.ICONS.edit());
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
				
				if(selected != null)
				{
					if(selected instanceof File)
					{
						File file = (File)selected;
						Folder parent = (Folder)file.getParent();
						
						EventBus eventbus = EventBus.getInstance();
						GetDataEvent event = new GetDataEvent(new FileIdentifier(selected.getName(),parent.getId(),selected.getId()));
						eventbus.fireEvent(event);
					}
				}
			}
		});	
		
		return ret;
	}
	
	private MenuItem buildFileRenameMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.rename());

		ret.setIcon(Resources.ICONS.edit());		
		ret.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				DiskResource selected = treePanel.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
					IPlantDialog dlg = new IPlantDialog(displayStrings.rename(),320,new RenameFileDialogPanel(selected.getId(),selected.getName()));
					dlg.show();
				}
			}
		});

		return ret;	
	}
	
	private void promptForImport(Point p)
	{
		String idFolder = getFolderId();
		
		//do we have an item selected?
		if(idFolder != null)
		{
			ImportDialog dlg = new ImportDialog(p,idWorkspace,idFolder);
			dlg.show();
		}
	}
	
	private MenuItem buildImportMenuItem()
	{
		MenuItem ret = new MenuItem(displayStrings.tagImport());

		ret.setIcon(Resources.ICONS.upload());
			
		Menu sub = new Menu();
		
		MenuItem item = new MenuItem(displayStrings.phylota());
		item.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				promptForImport(ce.getXY());
			}
		});
		
		//add our item to our sub-menu
		sub.add(item);
		ret.setSubMenu(sub);
		
		return ret;	
	}
	
	private Menu buildFolderContextMenu()
	{
		Menu contextMenu = new Menu();

		contextMenu.add(buildImportMenuItem());
		contextMenu.add(buildContextFileUploadMenuItem());
		contextMenu.add(buildFolderRenameMenuItem());
		contextMenu.add(buildFolderDeleteMenuItem());

		return contextMenu;
	}
	
	private Menu buildFileContextMenu()
	{
		Menu contextMenu = new Menu();
		
		contextMenu.add(buildFileEditMenuItem());
		contextMenu.add(buildFileRenameMenuItem());
		contextMenu.add(buildFileDeleteMenuItem());
		contextMenu.add(buildFileDownloadMenuItem());
		
		return contextMenu;
	}

	private void promptUpload(String idParent,Point p)
	{
		UploadPanel upload_panel = new UploadPanel(idWorkspace,idParent,displayStrings.uploadYourData(),onFinishUploaderHandler);
		upload_panel.assembleComponents();

		upload_dialog = new Dialog();
		upload_dialog.add(upload_panel);
		upload_dialog.setPagePosition(p);
		upload_dialog.setHeaderVisible(true);
		upload_dialog.setHeading(displayStrings.uploadAFile());
		upload_dialog.setButtons(Dialog.CANCEL);
		upload_dialog.setHideOnButtonClick(true);
		upload_dialog.setResizable(false);
		upload_dialog.setWidth(375);
		upload_dialog.setModal(true);		
		upload_dialog.show();
	}

	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler()
	{
		private Folder getFolder()
		{
			TreeStoreManager mgr = TreeStoreManager.getInstance();			
			Folder ret = mgr.getUploadFolder(storeWrapper); 
					
			DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
			
			if(selected != null)
			{
				//if we have a file... let's return the parent's id
				if(selected instanceof File)
				{
					File file = (File)selected;
					ret = (Folder)file.getParent();					
				}
				else if(selected instanceof Folder)
				{
					ret = (Folder)selected;
				}	
			}
			
			return ret;
		}
		
		public void onFinish(IUploader uploader)
		{
			if(uploader.getStatus() == Status.SUCCESS)
			{			
				Folder folder = getFolder();
				
				String response = uploader.getServerResponse();

				if(response != null)
				{	
					JsArray<FileInfo> fileInfos = JsonBuilder.asArrayofFileData(response);

					//there is always only one record
					if(fileInfos != null)
					{
						FileInfo info = fileInfos.get(0);
						
						if(info != null)
						{
							EventBus eventbus = EventBus.getInstance();	
							FileUploadedEvent event = new FileUploadedEvent(folder.getId(),info);							
							eventbus.fireEvent(event);
						
							Info.display(displayStrings.fileUpload(),displayStrings.fileUploadSuccess());						
						}						
					}					
				}

				treePanel.setExpanded(folder,true);
			}
			else
			{
				IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
				ErrorHandler.post(errorStrings.fileUploadFailed());				
			}
			
			if(upload_dialog != null)
			{
				upload_dialog.hide();
			}
		}
	};

	private void refreshTree()
	{
		FolderServices.getFiletree(idWorkspace,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());
			}

			@Override
			public void onSuccess(String result)
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				
				mgr.updateWrapper(storeWrapper,result);	
				
				treePanel.expandAll();
			}
		});
	}
	
	private void highlightItem(DiskResource resource)
	{
		if(resource != null)
		{
			treePanel.getSelectionModel().select(resource,false);	
			
			EventBus eventbus = EventBus.getInstance();
			DataBrowserNodeClickEvent clickevent = new DataBrowserNodeClickEvent(resource);
			eventbus.fireEvent(clickevent);
		}
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
				Folder folder = mgr.createFolder(storeWrapper,event.getId(),event.getName());
				
				highlightItem(folder);
			}
		});
		
		//folder renamed
		eventbus.addHandler(FolderRenamedEvent.TYPE,new FolderRenamedEventHandler()
		{
			@Override
			public void onRenamed(FolderRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				Folder folder = mgr.renameFolder(storeWrapper,event.getId(),event.getName());		
				
				highlightItem(folder);
			}
		});
		
		//file uploaded
		eventbus.addHandler(FileUploadedEvent.TYPE,new FileUploadedEventHandler()
		{
			@Override
			public void onUploaded(FileUploadedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				File file = mgr.addFile(storeWrapper,event.getParentId(),event.getFileInfo());
				
				highlightItem(file);
			}
		});	
		
		//file save as completed
		eventbus.addHandler(FileSaveAsEvent.TYPE,new FileSaveAsEventHandler()
		{
			@Override
			public void onSaved(FileSaveAsEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				File file = mgr.addFile(storeWrapper,event.getParentId(),event.getFileInfo());
				
				highlightItem(file);
			}
		});	

		//file renamed
		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				File file = mgr.renameFile(storeWrapper,event.getId(),event.getName());
			
				if(file != null)
				{
					highlightItem(file);
				}		
			}
		});	
				
		//delete
		eventbus.addHandler(DiskResourceDeletedEvent.TYPE,new DiskResourceDeletedEventHandler()
		{
			@Override
			public void onDeleted(DiskResourceDeletedEvent event) 
			{
				TreeStoreManager mgr = TreeStoreManager.getInstance();
				mgr.delete(storeWrapper,event.getFolders(),event.getFiles());
				
				EventBus eventbus = EventBus.getInstance();
				DataBrowserNodeClickEvent clickevent = new DataBrowserNodeClickEvent(null);
				eventbus.fireEvent(clickevent);
			}
		});
	}
	
	private boolean isDraggable(DiskResource selected)
	{		
		return (selected != null && selected instanceof File) ? true : false;
	}
	
	private void initDragAndDrop()
	{		       
	     TreePanelDragSource source = new TreePanelDragSource(treePanel);  
	     source.addDNDListener(new DNDListener() {
	    	 @Override  
	    	 public void dragStart(DNDEvent e) 
	    	 {
	    		 DiskResource selected = treePanel.getSelectionModel().getSelectedItem();
	    		 
	    		 //we cannot drag folders
		         if(!isDraggable(selected)) 
		         {  
		            e.setCancelled(true);  
		            e.getStatus().setStatus(false);  
		            return;  
		         }  
		         
		         super.dragStart(e);  
	    	 }
	     });  
	   
	     TreePanelDropTarget target = new TreePanelDropTarget(treePanel);  
	     target.setAllowDropOnLeaf(true);
	     target.setAllowSelfAsSource(true);  
	     target.setFeedback(Feedback.BOTH); 		
	}
}
