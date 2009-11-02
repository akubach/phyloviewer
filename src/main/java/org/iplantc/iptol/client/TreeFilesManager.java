package org.iplantc.iptol.client;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;
import gwtupload.client.IUploadStatus.Status;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TreeFilesManager implements EntryPoint {
	
	 FlexTable list_files = null;
	 
	 public final int STATUS_INTERVAL = 100;
	 
	 public final String NO_FILES = "No files to display";
	 
	 public final String FILES_RADIO_GROUP = "fileListRadioGroup";
	 
	 public final String OPERATIONS_RADIO_GROUP = "operationsRadioGroup";
	 
	 public final String FILE_UPLOAD_SUCCESS = "File uploaded successfully!";
	 
	 public final String FILE_UPLOAD_FAILURE = "File upload failed. Please try again!";
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() { 
	  
	  	Button send = new Button("Upload");
	  	  	
	// Create a new uploader panel and attach it to the document
	    HorizontalPanel hUpload_panel = new HorizontalPanel();
	    SingleUploader defaultUploader = new SingleUploader(null,send);
	  
	    hUpload_panel.add(defaultUploader);
	    hUpload_panel.add(send);
	    SingleUploader.setStatusInterval(STATUS_INTERVAL);
	    
	    VerticalPanel vFiles_panel = new VerticalPanel();
	    defaultUploader.addStyleName("uploadPanel");
	    defaultUploader.setServletPath("servlet.gupld");
	    
	    //create dynamic table to display list of uploaded files
	    list_files = new FlexTable();
	    list_files.setText(0,0,"Select");
	    list_files.setText(0,1,"Label");
	    list_files.setText(0,2,"File Name");
	    list_files.setText(0,3,"Date / Time Uploded");
	    list_files.getRowFormatter().addStyleName(0, "fileListHeader");
	    list_files.addStyleName("fileList");
	    list_files.setCellPadding(5);
	    
	    /* TODO - Call a service to retrieve a list of files already uploaded*/
	    
	    if(list_files.getRowCount() == 1) {
	    	list_files.getFlexCellFormatter().setColSpan(1, 0, 3);
	    	list_files.setText(1, 0, NO_FILES);
	    }
	    
	    vFiles_panel.add(list_files);
	    HTML prompt = new HTML("Upload your trees: ");
	    prompt.addStyleName("uploadTextAlign");
	    defaultUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
	    
	    HorizontalPanel hOperations_panel = new HorizontalPanel();
	    hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP, "View File"));
	    hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP, "View Tree"));
	    hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP, "Download"));
	    hOperations_panel.add(new RadioButton(OPERATIONS_RADIO_GROUP, "Delete"));
	    
	    RootPanel.get("file_upload").add(prompt);
	    RootPanel.get("file_upload").add(hUpload_panel);
	    RootPanel.get("file_management").add(vFiles_panel);
	    RootPanel.get("file_operations").add(hOperations_panel);
	    
  }
  /**
   * Call back method for file upload submit
   */
  private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
	      public void onFinish(IUploader uploader) {
	    	  //construct a popup to show success / failure message
	    	  HTML msg = null;
	    	  final PopupPanel popup = new PopupPanel(false);
	    	  Button btn_Ok = new Button("Ok");
	    	  btn_Ok.addClickHandler( new ClickHandler() {
				public void onClick(ClickEvent event) {
					popup.hide();
				}
			});
	       
	       VerticalPanel vPopupHeaderPanel = new VerticalPanel();
	       vPopupHeaderPanel.add(new HTML("File Upload"));
	       vPopupHeaderPanel.setStyleName("popup_header_panel");
	       
	       VerticalPanel vPopupContentPanel = new VerticalPanel();
	       HorizontalPanel holder = new HorizontalPanel();
	       holder.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
	       holder.add(btn_Ok);
	       holder.addStyleName("popup_ok_btn_panel");
	    
          if (uploader.getStatus() == Status.SUCCESS) {
	        	  JSONValue value = JSONParser.parse(uploader.getServerResponse());
	        	  JSONObject json = value.isObject();
	        	  if(json != null) {
		        	  int row = list_files.getRowCount();
		        	  if(row == 2 && NO_FILES.equals(list_files.getText(1, 0))) {
		        		  row = 1;
		        		  list_files.getFlexCellFormatter().setColSpan(1, 0, 1);
		        	  }
		        	  list_files.setWidget(row, 0, new RadioButton(FILES_RADIO_GROUP));
		        	  list_files.setText(row,1,json.get("label").isString().stringValue());
		        	  list_files.setText(row,2,json.get("file_name").isString().stringValue());
		        	  list_files.setText(row,3,json.get("date").isString().stringValue());
		          }
	        	  msg = new HTML(FILE_UPLOAD_SUCCESS);
	        	  msg.addStyleName("success");
	        } else {
	        	 msg = new HTML(FILE_UPLOAD_FAILURE);
	        	 msg.addStyleName("failure");
	        }
          	
          	  vPopupContentPanel.add(vPopupHeaderPanel);
          	  vPopupContentPanel.add(msg);
          	  vPopupContentPanel.add(holder);
          	  
          	  popup.add(vPopupContentPanel);
	          popup.setStyleName("popup");
	          popup.center();
	       }
	     };   
  
  }

