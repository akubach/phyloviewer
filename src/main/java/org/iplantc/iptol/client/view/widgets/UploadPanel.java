package org.iplantc.iptol.client.view.widgets;

import org.iplantc.iptol.client.IptolConstants;
import org.iplantc.iptol.client.UploadStatus;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;

public class UploadPanel extends ContentPanel {

	private SingleUploader defaultUploader;
	private Button send;
	
	public static final int FILE_INPUT_SIZE = 27;
	public static final String UPLOAD_PANEL_WIDTH = "320px";
	
	private HorizontalPanel h_panel;
	private Status status; 
	private Status upload_percetage;
	private ToolBar toolBar;
	
	IptolConstants constants = (IptolConstants) GWT
	.create(IptolConstants.class);
	/*
	 * contruct a new upload panel
	 */
	public UploadPanel(String header, String servlet_path,IUploader.OnFinishUploaderHandler handler) {
		super();
		this.setHeading(header);
		//must be called before Initing UploadStatus 
		send = new Button("Upload");
		//set id
		DOM.setElementProperty(send.getElement(), "id", "uploadButton");
		
		h_panel = new HorizontalPanel();
		toolBar = new ToolBar();  
		status = new Status();  
		upload_percetage = new Status();
		// end init
		defaultUploader = new SingleUploader(new UploadStatus(this),
				send);
		defaultUploader.addOnFinishUploadHandler(handler);
		defaultUploader.setServletPath(servlet_path);
		
		
	    send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(defaultUploader.getFileName()!= null && ! defaultUploader.getFileName().equals("")) {
					status.setBusy("Uploading " + defaultUploader.getFileName()+ "..." );
				}
				
			}
		});
	}

	public Status getStatusWidget() {
		return this.status;
	}
	
	public Status getPercentageWidget() {
		return this.upload_percetage;
	}
	/**
	 * 
	 * @return
	 */
	public HorizontalPanel getUploadPanel() {
		return h_panel;
	}
	/**
	 * Assemble components
	 */
	public void assembleComponents() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		this.setBorders(false);
		this.setFrame(true);
		this.setCollapsible(true);
		this.setAnimCollapse(false);
		this.setHeaderVisible(false);
		this.setBodyBorder(false);
		this.setBodyStyle("background: #ffffff;");
		
		defaultUploader.setFileInputSize(FILE_INPUT_SIZE);
		defaultUploader.setStyleName("upload-Panel");
		SingleUploader.setStatusInterval(constants.statusInterval());
		defaultUploader.setWidth(UPLOAD_PANEL_WIDTH);
		
		h_panel.add(defaultUploader);
		send.setStyleName("upload_Button");
		buttonPanel.add(send);
		buttonPanel.setSpacing(4);
		h_panel.add(buttonPanel);
		this.add(h_panel);
		
		status.setText("Select a file to upload");  
		toolBar.add(status);
		toolBar.setBorders(false);
		toolBar.setAutoWidth(true);
		toolBar.add(new FillToolItem());
		upload_percetage.setBox(true);
		upload_percetage.setText("");
		toolBar.add(upload_percetage);
		this.setBottomComponent(toolBar);
		
	}
}
