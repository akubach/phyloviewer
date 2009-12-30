package org.iplantc.iptol.client;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.ArrayList;
import java.util.HashMap;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.js.JsonConverter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

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
	private ToolBar toolBar = null;
	private IptolConstants constants = (IptolConstants) GWT
	.create(IptolConstants.class);
	
	private TreeStore<ModelData> store = null;
	private TreePanel<ModelData> treePanel = null;
	
	private Dialog upload_dialog = null;
	
	
	@SuppressWarnings("unchecked")
	public DataBrowserTree() {
		store = new TreeStore<ModelData>(); 
		store.add(getTreeModel(),true);
		treePanel = new TreePanel<ModelData>(store);
		status = new com.extjs.gxt.ui.client.widget.Status();
		toolBar = new ToolBar();
	}
	
	protected void assembleView() {
		toolBar.add(status);
		treePanel.setBorders(true);
		treePanel.setDisplayProperty("name"); 
		treePanel.setContextMenu(buildContextMenu());
		this.add(treePanel);
		this.setWidth(175);
		this.setHeight(500);
		this.setHeading("Data Browser");
		this.setBottomComponent(toolBar);
		
		//load info about the file on the status bar
		treePanel.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				ModelData folder = treePanel.getSelectionModel().getSelectedItem();
				Window.alert("selected==>" + folder.get("name"));
			}
		});
	}
	
	/**
	 * A method to retrieve the folder structure for the data browser.
	 * This has to be rpc call. for now, mock it
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList getTreeModel() {
		ArrayList folders = new ArrayList();
		Folder dataFolder = new Folder("Data");
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
		upload.setText("Upload");
		upload.addSelectionListener(new SelectionListener<MenuEvent>() {  
			public void componentSelected(MenuEvent ce) {  
				ModelData folder = treePanel.getSelectionModel().getSelectedItem();  
				 if (folder != null) {  
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
		upload_dialog.setWidth(450);
		upload_dialog.show();
	}

	/**
	 * Call back method for file upload submit
	 */
	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				
				ModelData folder = treePanel.getSelectionModel().getSelectedItem();  
				 if (folder != null) {  
					 String response = uploader.getServerResponse();
					 Window.alert("response==>" + response);
					 if(response != null) {
						 JsArray<FileInfo> fileInfo = asArrayofFileData(response);
						 Window.alert(""+ fileInfo);
						 Window.alert(""+ fileInfo.length());
						 //there is always only one record
						 FileInfo info = fileInfo.get(0);
						 Folder child = new Folder(info.getFilename());
						 store.add(folder, child,false);
						 treePanel.setExpanded(folder, true);
						 Info.display("File Upload", constants.fileUploadSuccess());
					 }
					 
				 }
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
}
