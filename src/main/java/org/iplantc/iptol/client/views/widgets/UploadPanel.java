package org.iplantc.iptol.client.views.widgets;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.UploadStatus;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.services.FolderServices;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Hidden;

public class UploadPanel extends ContentPanel 
{
	private SingleUploader defaultUploader;
	private Button send;
	
	private static final int FILE_INPUT_SIZE = 27;
	private static final String UPLOAD_PANEL_WIDTH = "270px";
	
	private HorizontalPanel h_panel;
	private Status status; 
	private Status upload_percentage;
	private ToolBar toolBar;
	
	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT
	.create(IptolErrorStrings.class);
	
	private Hidden folderid; 
	private Hidden workspaceid;
	
	/*
	 * contruct a new upload panel
	 */
	public UploadPanel(String idWorkspace,String idParentFolder,String header,IUploader.OnFinishUploaderHandler handler) 
	{
		super();
	
		this.setHeading(header);
		folderid = new Hidden("folderid",idParentFolder);
		workspaceid = new Hidden("workspaceid",idWorkspace);
		
		//must be called before initializing UploadStatus 
		send = new Button("Upload");
		
		//set id
		DOM.setElementProperty(send.getElement(),"id","uploadButton");
		
		h_panel = new HorizontalPanel();
		toolBar = new ToolBar();  
		status = new Status();  
		upload_percentage = new Status();
		
		// end init
		defaultUploader = new SingleUploader(new UploadStatus(this),send);
		defaultUploader.addOnFinishUploadHandler(handler);
		defaultUploader.setServletPath(constants.fileUploadServlet());
				
	    send.addClickHandler(new ClickHandler() 
	    {
			@Override
			public void onClick(ClickEvent event) 
			{
				if(defaultUploader.getFileName() != null && !defaultUploader.getFileName().equals("")) 
				{
					status.setBusy(displayStrings.uploading() + " " + defaultUploader.getFileName() + "..." );
				}				
			}
		});
	    
	    defaultUploader.addOnChangeUploadHandler( new IUploader.OnChangeUploaderHandler(){

			@Override
			public void onChange(IUploader uploader) {
				checkForDuplicateFiles(uploader);
				
			}

			
	    	
	    }
	    
	    
	    );
	}
	
	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;
	
	/**
	 * A method to check duplicate file uploads
	 * @param uploader
	 */
	private void checkForDuplicateFiles(final IUploader uploader) {
		FolderServices.getListofFiles(workspaceid.getValue(), new AsyncCallback<String>() {
			
			 boolean flag = false;
			 final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
			       public void handleEvent(MessageBoxEvent ce) {  
			         com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();  
			         if(btn.getText().equals("Yes")) {
			        	 defaultUploader.submit();
			         } else {
			        	 defaultUploader.reset();
			         }
			       }  
			 };  
			
			@Override
			public void onSuccess(String result) {
				JsArray<FileInfo> fileinfos = asArrayofFileData(result);
				for (int i = 0; i < fileinfos.length(); i++) {
					if(fileinfos.get(i).getName().equals(uploader.getFileName())) {
						flag = true;
						MessageBox.confirm("Duplicate File", displayStrings.duplicateFile(), l);
						break;
					}
				}
				
				//no duplicates..enable send button
				if (!flag) {
					send.setEnabled(true);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());
				
			}
		});
		
	
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Status getStatusWidget() 
	{
		return status;
	}
	
	/**
	 * 
	 * @return
	 */
	public Status getPercentageWidget() 
	{
		return upload_percentage;
	}
	
	/**
	 * 
	 * @return
	 */
	public HorizontalPanel getUploadPanel() 
	{
		return h_panel;
	}
	
	/**
	 * Assemble components
	 */
	public void assembleComponents() 
	{		
		setBorders(false);
		setFrame(true);
		setCollapsible(true);
		setAnimCollapse(false);
		setHeaderVisible(false);
		setBodyBorder(false);
		setBodyStyle("background: #ffffff;");
		
		defaultUploader.setFileInputSize(FILE_INPUT_SIZE);
		defaultUploader.setStyleName("upload-panel");
		SingleUploader.setStatusInterval(constants.statusInterval());
		defaultUploader.setWidth(UPLOAD_PANEL_WIDTH);
		defaultUploader.add(folderid);
		defaultUploader.add(workspaceid);
		
		h_panel.add(defaultUploader);
		send.setStyleName("upload-button");
		send.setEnabled(false);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(send);
		buttonPanel.setSpacing(4);
		h_panel.add(buttonPanel);
		add(h_panel);
		
		status.setText(displayStrings.selectAFileToUpload());  
		toolBar.add(status);
		toolBar.setBorders(false);
		toolBar.setAutoWidth(true);
		toolBar.add(new FillToolItem());
		upload_percentage.setBox(true);
		upload_percentage.setText("");
		toolBar.add(upload_percentage);
		setBottomComponent(toolBar);		
	}
	
	public SingleUploader getUploader() {
		return defaultUploader;
	}
}
