package org.iplantc.iptol.client;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;
import gwtupload.client.IUploadStatus.Status;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.JsonReader;
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

	IptolConstants constants = (IptolConstants) GWT
			.create(IptolConstants.class);

	public static final String OPERATIONS_RADIO_GROUP = "operationsRadioGroup";
	public static final int HSPACING = 10;
	
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
		hUpload_panel.setSpacing(HSPACING);
		defaultUploader.addStyleName("uploadPanel");
		defaultUploader.setServletPath("servlet.gupld");
		defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);

		HTML prompt = new HTML("Upload your tree: ");
		prompt.addStyleName("uploadTextAlign");

		// column config for tables
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig("File Name", "File Name", 100));
		columns.add(new ColumnConfig("Label", "Label", 100));
		columns.add(new ColumnConfig("Uploaded Date/Time",
				"Uploaded Date/Time", 100));

		// create the column model
		ColumnModel column_model = new ColumnModel(columns);

		// defines the xml structure
		type.setRoot("data");
		type.addField("File Name");
		type.addField("Label");
		type.addField("Uploaded Date/Time");

		// TODO - call service to return a list of files uploaded

		store = new ListStore<ModelData>();
		grid = new Grid<ModelData>(store, column_model);
		grid.setBorders(true);
		grid.setLoadMask(true);
		grid.getView().setEmptyText(constants.noFiles());
		grid.setAutoExpandColumn("Uploaded Date/Time");

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
		hOperations_panel
				.add(new RadioButton(OPERATIONS_RADIO_GROUP, "View Tree"));
		hOperations_panel
				.add(new RadioButton(OPERATIONS_RADIO_GROUP, "Download"));
		hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP, "Delete"));
		hOperations_panel.setSpacing(HSPACING);
		RootPanel.get("file_upload").add(prompt);
		RootPanel.get("file_upload").add(hUpload_panel);
		RootPanel.get("file_management").add(panel);
		RootPanel.get("file_operations").add(hOperations_panel);

	}

	/**
	 * Call back method for file upload submit
	 */
	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		@SuppressWarnings("unchecked")
		public void onFinish(IUploader uploader) {
			// construct a popup to show success / failure message
			HTML msg = null;
			final Dialog popup_dialog = new Dialog();
			popup_dialog.setHeading("File Upload");
			popup_dialog.setButtons(Dialog.OK);
			popup_dialog.setHideOnButtonClick(true);
		
			if (uploader.getStatus() == Status.SUCCESS) {
				String response = uploader.getServerResponse();
				//Window.alert(response);
				JsonReader<ModelData> reader = new JsonReader<ModelData>(type);
				ArrayList model = (ArrayList) reader.read(null, response);
				store.add(model);
				msg = new HTML(constants.fileUploadSuccess());
				popup_dialog.addText(constants.fileUploadSuccess());
			} else {
				msg = new HTML(constants.fileUploadFailed());
				popup_dialog.addText(constants.fileUploadFailed());
			}
			popup_dialog.show();
		}
	};

}
