package org.iplantc.iptol.client;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TreeFilesManager implements EntryPoint {

	FlexTable list_files = null;

	IptolConstants constants = (IptolConstants) GWT.create(IptolConstants.class);
	
	public static final String operationsRadioGroup = "OPERATIONS_RADIO_GROUP";
	public static final String filesRadioGroup = "FILES_RADIO_GROUP";
	ListStore<ModelData> store = null;
    Grid<ModelData> grid = null;
    ModelType type = new ModelType();  
	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	
	Button send = new Button("Upload");

	// Create a new uploader panel and attach it to the document
	HorizontalPanel hUpload_panel = new HorizontalPanel();
	SingleUploader defaultUploader = new SingleUploader(new UploadStatus(),
			send);
	SingleUploader.setStatusInterval(constants.statusInterval());
	defaultUploader.add(new Hidden("file-type", "tree"));
	hUpload_panel.add(defaultUploader);
	hUpload_panel.add(send);
	defaultUploader.addStyleName("uploadPanel");
	defaultUploader.setServletPath("servlet.gupld");
	defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);

	HTML prompt = new HTML("Upload your tree: ");
	prompt.addStyleName("uploadTextAlign");

	//column config for tables
	List<ColumnConfig> columns = new ArrayList<ColumnConfig>(); 
	columns.add(new ColumnConfig("name","File Name",100));
	columns.add(new ColumnConfig("label","Label",100));
	columns.add(new ColumnConfig("date","Uploaded Date/Time",100));
	
	// create the column model  
	 ColumnModel column_model = new ColumnModel(columns);  
	
	// defines the xml structure  
	type.addField("File Name", "file_name");
	type.addField("Label", "label");
	type.addField("Uploaded Date/Time","date");
	
	
	//TODO - call service to return a list of files uploaded
	
     store = new ListStore<ModelData>();  
     grid = new Grid<ModelData>(store, column_model);  
     grid.setBorders(true);  
     grid.setLoadMask(true);  
     grid.getView().setEmptyText(constants.noFiles());  
     
	 ContentPanel panel = new ContentPanel();  
	 panel.setFrame(true);  
	 panel.setCollapsible(true);  
	 panel.setAnimCollapse(false);  
	 panel.setButtonAlign(HorizontalAlignment.CENTER);  
	 panel.setHeading("Uploaded Trees");  
	 panel.setLayout(new FitLayout());  
	 panel.add(grid);  
	 panel.setSize(350, 250);
	
	HorizontalPanel hOperations_panel = new HorizontalPanel();
	hOperations_panel.add(new RadioButton(operationsRadioGroup,"View Tree"));
	hOperations_panel.add(new RadioButton(operationsRadioGroup,
			"Download"));
	hOperations_panel
			.add(new RadioButton(operationsRadioGroup, "Delete"));

	RootPanel.get("file_upload").add(prompt);
	RootPanel.get("file_upload").add(hUpload_panel);
	RootPanel.get("file_management").add(panel);
	RootPanel.get("file_operations").add(hOperations_panel);

}

/**
 * Call back method for file upload submit
 */
private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
	public void onFinish(IUploader uploader) {
		// construct a popup to show success / failure message
		HTML msg = null;
		final Dialog popup_dialog = new Dialog();
		popup_dialog.setHeading("File Upload");
		popup_dialog.setButtons(Dialog.OK);
		popup_dialog.setHideOnButtonClick(true);
		if (uploader.getStatus() == Status.SUCCESS) {
			String response = uploader.getServerResponse();
//			Window.alert(response);
//			JSONValue value = JSONParser
//					.parse(response);
//			JSONObject json = value.isObject();
//			if (json != null) {
//			
//			}
			JsonLoadResultReader<ModelData> reader = new JsonLoadResultReader<ModelData>(type);
			store.add(reader.read(null, response));
			msg = new HTML(constants.fileUploadSuccess());
			msg.addStyleName("success");
			popup_dialog.addText(constants.fileUploadSuccess());
		} else {
			msg = new HTML(constants.fileUploadFailed());
			msg.addStyleName("failure");
			popup_dialog.addText(constants.fileUploadFailed());
			
		}
		popup_dialog.show();
	}
};

}
