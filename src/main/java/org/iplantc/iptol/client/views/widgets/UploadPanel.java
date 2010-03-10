package org.iplantc.iptol.client.views.widgets;

import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolDisplayStrings;
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
import com.google.gwt.user.client.ui.Hidden;

public class UploadPanel extends ContentPanel 
{
	private SingleUploader defaultUploader;
	private Button send;
	
	private static final int FILE_INPUT_SIZE = 27;
	private static final String UPLOAD_PANEL_WIDTH = "270px";
	
	private HorizontalPanel h_panel;
	private Status status; 
	private Status upload_percetage;
	private ToolBar toolBar;
	
	private IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
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
		upload_percetage = new Status();
		
		// end init
		defaultUploader = new SingleUploader(new UploadStatus(this),send);
		defaultUploader.addOnFinishUploadHandler(handler);
		defaultUploader.setServletPath("servlet.gupld");
				
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
		return upload_percetage;
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
		defaultUploader.setStyleName("upload-Panel");
		SingleUploader.setStatusInterval(constants.statusInterval());
		defaultUploader.setWidth(UPLOAD_PANEL_WIDTH);
		defaultUploader.add(folderid);
		defaultUploader.add(workspaceid);
		
		h_panel.add(defaultUploader);
		send.setStyleName("upload_Button");
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
		upload_percetage.setBox(true);
		upload_percetage.setText("");
		toolBar.add(upload_percetage);
		setBottomComponent(toolBar);		
	}
}
