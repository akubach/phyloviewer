package org.iplantc.de.client.views.widgets;

import java.util.HashMap;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.dialogs.IPlantDialog;
import org.iplantc.de.client.dialogs.ImportDialog;
import org.iplantc.de.client.dialogs.panels.AddFolderDialogPanel;
import org.iplantc.de.client.dialogs.panels.FileUploadPanel;
import org.iplantc.de.client.dialogs.panels.RenameFileDialogPanel;
import org.iplantc.de.client.dialogs.panels.RenameFolderDialogPanel;
import org.iplantc.de.client.events.DataBrowserNodeClickEvent;
import org.iplantc.de.client.events.DefaultUploadCompleteHandler;
import org.iplantc.de.client.events.GetDataEvent;
import org.iplantc.de.client.events.UploadCompleteHandler;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileMovedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileMovedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileUploadedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FolderCreatedEvent;
import org.iplantc.de.client.events.disk.mgmt.FolderCreatedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FolderRenamedEvent;
import org.iplantc.de.client.events.disk.mgmt.FolderRenamedEventHandler;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.DiskResource;
import org.iplantc.de.client.models.File;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.models.Folder;
import org.iplantc.de.client.services.DiskResourceDeleteCallback;
import org.iplantc.de.client.services.FileMoveCallback;
import org.iplantc.de.client.services.FolderServices;
import org.iplantc.de.client.utils.JsonConverter;
import org.iplantc.de.client.views.widgets.panels.TreeStoreWrapper;

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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.core.client.GWT;
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
	private IPlantDialog dlgUpload = null;
	private Button btnOptions = new Button();
	private String idWorkspace;
	private String idDraggedFile;
	private Menu mnuFileContext;
	private Menu mnuFolderContext;
	private TreeStoreWrapper storeWrapper = new TreeStoreWrapper();
	private TreePanel<DiskResource> pnlTree = new TreePanel<DiskResource>(storeWrapper.getStore());
	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	public DataBrowserTree(String idWorkspace)
	{
		this.idWorkspace = idWorkspace;
		setScrollMode(Scroll.AUTOY);

		//create our context menus
		mnuFileContext = buildFileContextMenu();
		mnuFolderContext = buildFolderContextMenu();
		
		initEventHandlers();
		initDragAndDrop();
	}

	/**
	 * Put together the widget
	 */
	public void assembleView()
	{
		pnlTree.setBorders(true);
		pnlTree.setDisplayProperty("name");
		pnlTree.setContextMenu(buildFolderContextMenu());
		pnlTree.setWidth("100%");
		pnlTree.setHeight("99%");
		//disable multi-select
		TreePanelSelectionModel<DiskResource> sm = new TreePanelSelectionModel<DiskResource>();
		sm.setSelectionMode(SelectionMode.SINGLE);
		pnlTree.setSelectionModel(sm);

		add(pnlTree);

		setHeading(displayStrings.dataBrowser());

		btnOptions.setScale(ButtonScale.SMALL);
		btnOptions.setArrowAlign(ButtonArrowAlign.RIGHT);
		btnOptions.setIcon(Resources.ICONS.listItems());
		btnOptions.setMenu(buildOptionsMenu());

		getHeader().addTool(btnOptions);

		//provide icons to the tree nodes
		pnlTree.setIconProvider(new ModelIconProvider<DiskResource>()
		{
			@Override
			public AbstractImagePrototype getIcon(DiskResource model)
			{
				 if(model instanceof Folder)
				 {
					 return (pnlTree.isExpanded(model)) ? 
							 pnlTree.getStyle().getNodeOpenIcon() : 
							 pnlTree.getStyle().getNodeCloseIcon();
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
		pnlTree.getSelectionModel().addListener(Events.Select,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				showContextMenu();
			}
		});

		pnlTree.getSelectionModel().addListener(Events.SelectionChange,new Listener<BaseEvent>()
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

		DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
		DiskResource folder = pnlTree.getSelectionModel().getSelectedItem();

		//set context menu
		if(folder instanceof Folder)
		{
			pnlTree.setContextMenu(mnuFolderContext);
		}
		else
		{
			pnlTree.setContextMenu(mnuFileContext);
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
				IPlantDialog dlg = new IPlantDialog(displayStrings.newFolder(),320,
						new AddFolderDialogPanel(idWorkspace,storeWrapper.getRootFolderId()));
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
				Window.open("help/databrowser.html", "Help", null);
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
				DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
				DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
		String json = JsonConverter.buildDeleteFolderString(id);

		if(json != null)
		{
			DiskResourceDeleteCallback callback = new DiskResourceDeleteCallback();
			callback.addFolder(id);

			FolderServices.deleteDiskResources(idWorkspace,json,callback);
		}
	}

	private void deleteFile(String id)
	{
		String json = JsonConverter.buildDeleteFileString(id);

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
				final DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
				DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
				final DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

				if(selected != null)
				{
				    String address = Window.Location.getProtocol() + "://" + Window.Location.getHost()
				        + Window.Location.getPath() + "files/" + selected.getId() + "/content.gdwnld";
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
				DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
				DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();

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
	{	// provide key/value pairs for hidden fields
		HashMap<String, String> hiddenFields = new HashMap<String, String>();
		hiddenFields.put(FileUploadPanel.HDN_WORKSPACE_ID_KEY, idWorkspace);
		hiddenFields.put(FileUploadPanel.HDN_PARENT_ID_KEY, idParent);
		
		// define a handler for upload completion
		UploadCompleteHandler handler = new DefaultUploadCompleteHandler(idParent) 
		{ 
			@Override   
			public void onAfterCompletion() 
			{
				if(dlgUpload != null) 
				{
					dlgUpload.hide();
				}
			}
		};
		
		// get the servlet action url
		DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);
		String servletActionUrl = constants.fileUploadServlet();
		
		FileUploadPanel pnlUpload = new FileUploadPanel(hiddenFields, servletActionUrl, handler);
		
		dlgUpload = new IPlantDialog(displayStrings.uploadYourData(), 375, pnlUpload);
        dlgUpload.setButtons(Dialog.CANCEL);
        dlgUpload.setPagePosition(p);
        dlgUpload.show();
	}

	private void refreshTree()
	{
		FolderServices.getFiletree(idWorkspace,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());
			}

			@Override
			public void onSuccess(String result)
			{
				storeWrapper.updateWrapper(result);

				pnlTree.expandAll();
			}
		});
	}

	private void highlightItem(DiskResource resource)
	{
		if(resource != null)
		{
			pnlTree.getSelectionModel().select(resource,false);

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
				Folder folder = storeWrapper.createFolder(event.getId(),event.getName());
				
				highlightItem(folder);
			}
		});

		//folder renamed
		eventbus.addHandler(FolderRenamedEvent.TYPE,new FolderRenamedEventHandler()
		{
			@Override
			public void onRenamed(FolderRenamedEvent event)
			{
				Folder folder = storeWrapper.renameFolder(event.getId(),event.getName());

				highlightItem(folder);
			}
		});

		//file uploaded
		eventbus.addHandler(FileUploadedEvent.TYPE,new FileUploadedEventHandler()
		{
			@Override
			public void onUploaded(FileUploadedEvent event)
			{
				File file = storeWrapper.addFile(event.getParentId(),event.getFileInfo(),event.getDeleteIds());

				highlightItem(file);
			}
		});

		//file save as completed
		eventbus.addHandler(FileSaveAsEvent.TYPE,new FileSaveAsEventHandler()
		{
			@Override
			public void onSaved(FileSaveAsEvent event)
			{
				File file = storeWrapper.addFile(event.getParentId(),event.getFileInfo(),null);

				highlightItem(file);
			}
		});

		//file renamed
		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event)
			{
				File file = storeWrapper.renameFile(event.getId(),event.getName());

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
				storeWrapper.delete(event.getFolders(),event.getFiles());

				EventBus eventbus = EventBus.getInstance();
				DataBrowserNodeClickEvent clickevent = new DataBrowserNodeClickEvent(null);
				eventbus.fireEvent(clickevent);
			}
		});
		
		//file moved
		eventbus.addHandler(FileMovedEvent.TYPE,new FileMovedEventHandler()
		{
			@Override
			public void onMoved(FileMovedEvent event)
			{
				File file = storeWrapper.moveFile(event.getFolderId(),event.getFileId());

				if(file != null)
				{					
					highlightItem(file);					
				}
			}
		});
	}
	
	private boolean isDraggable(DiskResource selected)
	{		
		return (selected != null && selected instanceof File) ? true : false;
	}
	
	private void initDragAndDrop()
	{		       
		TreePanelDragSource source = new TreePanelDragSource(pnlTree);  
		
	    source.addDNDListener(new DNDListener() 
	    {
	    	@Override  
	    	public void dragStart(DNDEvent e) 
	    	{
	    		DiskResource selected = pnlTree.getSelectionModel().getSelectedItem();
	    		 
	    		if(isDraggable(selected)) 
	    		{  
	    			idDraggedFile = selected.getId();
	    			super.dragStart(e);
	    		}  
	    		else
	    		{
		    		//we cannot drag folders
	    			e.setCancelled(true);  
		            e.getStatus().setStatus(false);	    			
	    		}
	    	}	      	
	    });  
	   
	    TreePanelDropTarget target = new TreePanelDropTarget(pnlTree);  
		target.setAllowDropOnLeaf(true);
		target.setAllowSelfAsSource(true);  
		target.setFeedback(Feedback.BOTH); 
		 
		target.addDNDListener(new DNDListener()
		{
			@Override 
			@SuppressWarnings("unchecked")
			public void dragDrop(DNDEvent e) 
	    	{					
				TreeNode node = pnlTree.findNode(e.getTarget());
								
				if(node != null)
				{
					DiskResource res = (DiskResource)node.getModel();
					
					String idFolder = res.getId();
					
					FolderServices.moveFile(idWorkspace,idFolder,idDraggedFile,new FileMoveCallback(idFolder,idDraggedFile));					
				}
				
				super.dragDrop(e);
				idDraggedFile = null;						
	    	}
			
			@Override
			@SuppressWarnings("unchecked")
			public void dragMove(DNDEvent e) 
			{
				TreeNode node = pnlTree.findNode(e.getTarget());
				
				if(node != null)
				{
					DiskResource res = (DiskResource)node.getModel();
					
					// if the user dragged the file beneath a file... we need 
					// to bump up to the parent folder
					if (!(res instanceof Folder))
					{
						e.setCancelled(true);  
			            e.getStatus().setStatus(false);
					}
				}
				else
				{
					// this is reached when the user is dragging a file without 
					// highlighting a node.  Normally the feedback at this point
					// would be a vertical bar.
					e.setCancelled(true);  
		            e.getStatus().setStatus(false);	
				}
			}
		 }); 		
	}
}
