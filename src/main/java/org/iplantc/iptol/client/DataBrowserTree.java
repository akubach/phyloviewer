package org.iplantc.iptol.client;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.TreeFilesManager.TreeFilesListUpdater;
import org.iplantc.iptol.client.events.DataBrowserNodeClickEvent;
import org.iplantc.iptol.client.images.Resources;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
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
public class DataBrowserTree extends ContentPanel {
	public static final String UPLOAD_PROMPT = "Upload your data";
	public static final String SERVLET_PATH = "servlet.gupld";

	private com.extjs.gxt.ui.client.widget.Status status = null;
	private IptolConstants constants = (IptolConstants) GWT
	.create(IptolConstants.class);
	
	private TreeStore<File> store = null;
	private TreePanel<File> treePanel = null;
	
	private Dialog upload_dialog = null;
	
	private HandlerManager eventbus;
	private Button options;
	
	private final String DEFAULT_FOLDER = "Data";
	
	private Folder defaultParent = null;
	
	@SuppressWarnings("unchecked")
	public DataBrowserTree(HandlerManager eventbus) {
		store = new TreeStore<File>(); 
		treePanel = new TreePanel<File>(store);
		this.eventbus = eventbus;
		options = new Button();
		
	}
	/**
	 * Put together the widget
	 */
	public void assembleView() {
		treePanel.setBorders(true);
		treePanel.setDisplayProperty("name"); 
		treePanel.setContextMenu(buildContextMenu());
		this.add(treePanel);
		this.setWidth(175);
		this.setHeight(500);
		this.setHeading("Data Browser");
		options.setScale(ButtonScale.SMALL);
		options.setArrowAlign(ButtonArrowAlign.RIGHT);
		options.setIcon(Resources.ICONS.listItems());
		options.setMenu(buildOptionsMenu());
		this.getHeader().addTool(options);
		
		//provide icons to the tree nodes
		treePanel.setIconProvider(new ModelIconProvider<File>() {
			@Override
			public AbstractImagePrototype getIcon(File model) {
				if(!model.get("name").equals(DEFAULT_FOLDER)) {
					return Resources.ICONS.green();
				} else {
					if(treePanel.isExpanded(model))
						return treePanel.getStyle().getNodeOpenIcon();
					else 
						return treePanel.getStyle().getNodeCloseIcon();
				}
			}
		});
	
		//add default folder
		store.add(getTreeModel(),true);
		
		//retrieve all the files that have been uploaded already
		getFilesInfo();
		
		//load info about the file on the status bar
		treePanel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				File folder = treePanel.getSelectionModel().getSelectedItem();
				//Window.alert("selected==>" + folder.get("name"));
				//Window.alert("child count=>" + store.getChildCount(folder));
	 			DataBrowserNodeClickEvent event = new DataBrowserNodeClickEvent(folder);
	 			eventbus.fireEvent(event);
			}
			});
	}
	
	/**
	 * Build menu for the data browser tree
	 * @return
	 */
	private Menu buildOptionsMenu() {
		final Menu optionsMenu = new Menu();
		MenuItem uploadItem = new MenuItem("Upload");
		uploadItem.setId("upload_menu_item_option");
		uploadItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				promptUpload(optionsMenu.getPosition(false));
			}
		});
		optionsMenu.add(uploadItem);
		return optionsMenu;
	}
	
	/**
	 * A method to retrieve the folder structure for the data browser.
	 * This has to be rpc call. for now, mock it
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<File> getTreeModel() {
		ArrayList folders = new ArrayList();
		Folder dataFolder = new Folder(DEFAULT_FOLDER);
		defaultParent = dataFolder;
		folders.add(dataFolder);
		return folders;
	}
	/**
	 * Attach context menu to DataBrowserTree
	 * @return
	 */
	private Menu buildContextMenu() {
		Menu contextMenu = new Menu();  
		MenuItem upload = new MenuItem(); 
		upload.setId("upload_menu_item");
		upload.setText("Upload");
		upload.addSelectionListener(new SelectionListener<MenuEvent>() {  
			@Override
			public void componentSelected(MenuEvent ce) {  
				File folder = treePanel.getSelectionModel().getSelectedItem();  
				 if ( folder != null) {  
					 	promptUpload(ce.getXY());
			       }  
				}  
		  }); 
		contextMenu.add(upload);
		return contextMenu;
	}
	/**
	 * Display dialog for file upload
	 * @param p XY coordinate at which the prompt should be displayed
	 */
	private void promptUpload(Point p) {
		UploadPanel upload_panel = new UploadPanel(UPLOAD_PROMPT, SERVLET_PATH, onFinishUploaderHandler);
		upload_panel.assembleComponents();
		upload_dialog = new Dialog();
		upload_dialog.add(upload_panel);
		upload_dialog.setPagePosition(p);
		upload_dialog.setHeaderVisible(true);
		upload_dialog.setHeading("Upload A File");
		upload_dialog.setButtons(Dialog.CANCEL);
		upload_dialog.setHideOnButtonClick(true);
		upload_dialog.setWidth(450);
		upload_dialog.show();
	}

	/**
	 * Call back method for file upload submit
	 */
	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				File folder = (File) treePanel.getSelectionModel().getSelectedItem();  
//				 Window.alert(""+ (folder instanceof  Folder));
//				 Window.alert("folder-->" + folder);
//				 Window.alert("folder-->" + folder.getName());
				if(folder == null ) {
					List<File> folders = store.getRootItems();
					//add to the default folder (Data)
					folder = folders.get(0);
					//Window.alert("folder->" + folder);
				} 
			
				String response = uploader.getServerResponse();
				// Window.alert("response==>" + response);
				 if(response != null) {
					 JsArray<FileInfo> fileInfo = asArrayofFileData(response);
			//		 Window.alert(""+ fileInfo);
			//		 Window.alert(""+ fileInfo.length());
					 //there is always only one record
					 if(fileInfo!=null) {
						 FileInfo info = fileInfo.get(0);
				//		 Window.alert("info->" + info);
						 if (info!=null) {
							 File child = new File(info.getName());
							 if(false == folder instanceof  Folder) {
								 folder = (Folder) folder.getParent();
			//					 Window.alert("folder-->" + parent);
			//					 Window.alert("folder-->" + parent.getName());
							 }
							 store.add(folder, child,true);
							 child.setParent(folder);
							 child.setInfo(info);
							 folder.add(child);
				//			 Window.alert("count==>" + folder.getChildCount());
							 Info.display("File Upload", constants.fileUploadSuccess());
						 }
					 }
				 }
				
				 treePanel.setExpanded(folder, true);
			} else {
				MessageBox.alert("File Upload", constants.fileUploadFailed(),null);
			}
			
			if(upload_dialog!=null) {
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
	private void getFilesInfo() {
		IptolServiceFacade.getInstance().getServiceData(
				constants.filesListService(),
				new FilesListUpdater<String>());
	}
	/**
	 * Update the tree panel with retrieved results
	 * @param jsonResult
	 */
	private void updateStore(String jsonResult) {
		if(jsonResult!=null) {
			JsArray<FileInfo> fileInfos =  asArrayofFileData(jsonResult);
			for(int i=0;i<fileInfos.length();i++) {
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

	/**
	 * 
	 * @author sriram
	 * @param <String>
	 * Callback for treeFilesListService
	 */
	@SuppressWarnings("hiding")
	class FilesListUpdater<String> extends AbstractAsyncHandler {

		@Override
		public void handleFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void handleSuccess(Object result) {
			updateStore((java.lang.String) result);
		}
	}
}
