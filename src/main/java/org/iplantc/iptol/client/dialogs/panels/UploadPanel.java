package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.UploadStatus;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class UploadPanel extends ContentPanel {

	private SingleUploader defaultUploader;
	private Button send;
	
	public static final int FILE_INPUT_SIZE = 27;
	public static final String UPLOAD_PANEL_WIDTH = "320px";
	
	private HorizontalPanel h_panel;
	private Status status; 
	private Status upload_percentage;
	private ToolBar toolBar;
	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	
	/*
	 * contruct a new upload panel
	 */
	public UploadPanel(String header, String servlet_path, 
			IUploader.OnFinishUploaderHandler handler) {
		super();
		this.setHeading(header);
		//must be called before Initing UploadStatus 
		send = new Button("Upload");
		h_panel = new HorizontalPanel();
		toolBar = new ToolBar();  
		status = new Status();  
		upload_percentage = new Status();
		// end init
		defaultUploader = new SingleUploader(new UploadStatus(this),
				send);
		defaultUploader.addOnFinishUploadHandler(handler);
		defaultUploader.setServletPath(servlet_path);
		
		
	    send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (defaultUploader.getFileName() != null
						&& !defaultUploader.getFileName().equals("")) {
					status.setBusy("Uploading " 
							+ defaultUploader.getFileName()
							+ "...");
				}
			}
		});
	}

	public Status getStatusWidget() {
		return this.status;
	}
	
	public Status getPercentageWidget() {
		return this.upload_percentage;
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
	public void assembleComponents() 
	{
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setSpacing(4);
		
		setBorders(false);
		setFrame(true);
		setCollapsible(true);
		setAnimCollapse(false);
		setHeaderVisible(false);
		setBodyBorder(false);
		setBodyStyle("background: #ffffff;");
		
		buttonPanel.addStyleName("upload-button");
		defaultUploader.setFileInputSize(FILE_INPUT_SIZE);
		defaultUploader.setStyleName("upload-panel");
		SingleUploader.setStatusInterval(constants.statusInterval());
		defaultUploader.setWidth(UPLOAD_PANEL_WIDTH);
		h_panel.setBorders(true);
		h_panel.setLayout(new RowLayout(Orientation.VERTICAL));
		h_panel.add(defaultUploader);
		buttonPanel.add(send);
		h_panel.add(buttonPanel);
		h_panel.setSpacing(5);
		this.add(h_panel);
		status.setText("Select a file to upload");  
		toolBar.add(status);
		toolBar.setBorders(false);
		toolBar.setAutoWidth(true);
		toolBar.add(new FillToolItem());
		upload_percentage.setBox(true);
		upload_percentage.setText("");
		toolBar.add(upload_percentage);
		this.setBottomComponent(toolBar);
	}
}