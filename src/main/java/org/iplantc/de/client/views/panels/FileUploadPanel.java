package org.iplantc.de.client.views.panels;

import java.util.HashMap;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.events.UploadCompleteHandler;
import org.iplantc.de.client.models.JsFile;
import org.iplantc.de.client.services.FolderServices;
import org.iplantc.de.client.utils.JsonConverter;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * Panel component for uploading files. 
 * 
 * @author lenards
 *
 */
public class FileUploadPanel extends IPlantDialogPanel {
	public static final String HDN_WORKSPACE_ID_KEY = "workspaceid";
	public static final String HDN_PARENT_ID_KEY = "parentfolderid";
	
	private DEErrorStrings errorStrings = (DEErrorStrings)GWT.create(DEErrorStrings.class);
	
	private final FormPanel form;
	private VerticalPanel pnlLayout;
	private final UploadCompleteHandler hdlrUpload;
	private final String idWorkspace;
	private final FileUpload fupload;
	
	public FileUploadPanel(HashMap<String, String> hiddenFields, String servletActionUrl, UploadCompleteHandler handler) {
		hdlrUpload = handler;
		idWorkspace = hiddenFields.get(HDN_WORKSPACE_ID_KEY);
		
		form = new FormPanel();
		form.setStyleName("iplantc-form-panel");
		form.setAction(servletActionUrl);
		form.setMethod(FormPanel.METHOD_POST);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);

		fupload = new FileUpload();
		fupload.setStyleName("iplantc-file-upload");
		fupload.setName("fileUpload");
		
		final Button btnUpload = new Button(displayStrings.upload());
		btnUpload.setEnabled(false);
		// TODO: determine if this is really needed - it's a carry-over from the old panel
		DOM.setElementProperty(btnUpload.getElement(),"id","uploadButton");
		
		final Status fileStatus = new Status();
		fileStatus.setStyleName("iplantc-file-status");
		
		HorizontalPanel pnlInternalLayout = new HorizontalPanel();
		pnlInternalLayout.setStyleName("iplantc-form-internal-layout-panel");
		pnlInternalLayout.setSpacing(5);

		// add any key/value pairs provided as hidden field
		for (String field : hiddenFields.keySet())
		{
			Hidden hdn = new Hidden(field, hiddenFields.get(field));
			pnlInternalLayout.add(hdn);
		}
		// then add the visual widgets 
		pnlInternalLayout.add(fupload);
		pnlInternalLayout.add(btnUpload);
		pnlInternalLayout.add(fileStatus);
		
		form.setWidget(pnlInternalLayout);

		fupload.addChangeHandler(new ChangeHandler() 
		{
			/**
			 * When the file upload has changed, enable the upload button. 
			 * 
			 * This is only fired when an actual file is selected, not 
			 * merely when the browse button is clicked. 
			 */
			@Override
			public void onChange(ChangeEvent event) 
			{
				// the verify step take longer as more files are uploaded 
				// so give some indication to the user that the UI is busy
				fileStatus.setBusy("");
				
				// TODO: something better than this (lenards)
				verifyNoDuplicateFileNames(btnUpload);
				// FIX: the verify method about is currently deciding 
				// whether to enable the button based on a lack of  
				// duplication. This is not really something that 
				// should be tied to a general component.  Rather, 
				// it should allow some Validators to be added and 
				// applied prior to this.
				
				// the upload button would be enabled if validation was successful
				// btnUpload.setEnabled(true);
				fileStatus.clearStatus("");
			}
		});
		
		btnUpload.addListener(Events.OnClick, new Listener<BaseEvent>() 
		{
			public void handleEvent(BaseEvent be) {
				fileStatus.setBusy("");	// shows just the spinner
				btnUpload.setEnabled(false);
				form.submit();
			}
		});		
		
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() 
		{
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) 
			{
				String response = event.getResults(); 
				hdlrUpload.onCompletion(response);
				// we're done, so clear the busy notification
				fileStatus.clearStatus("");
			}
		});
		
		pnlLayout = new VerticalPanel();
		pnlLayout.setStyleName("iplantc-form-layout-panel");
		pnlLayout.setLayoutData(new FitLayout());
		pnlLayout.add(form);
	}

	/**
	 * Ensure that duplicate filenames are not allowed to be submitted. 
	 * 
	 * @param uploadButton the button to enable when a suitable name is determined
	 */
	private void verifyNoDuplicateFileNames(final Button uploadButton) {
		// TODO: evaluate moving this out since it is not general validation
		FolderServices.getListofFiles(idWorkspace, new AsyncCallback<String>() {
			
			 boolean dupeFound = false;
			 final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
			       public void handleEvent(MessageBoxEvent ce) {  
			         com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();  
			         if(btn.getText().equals(displayStrings.affirmativeResponse())) {
			        	 form.submit();
			         } else {
			        	 form.reset();
			         }
			       }  
			 };  
			
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess(String result) 
			{
				JsArray<JsFile> fileinfos = JsonConverter.asArrayofFileData(result);
				for (int i = 0, len = fileinfos.length(); i < len; i++) 
				{
					if(fileinfos.get(i).getName().equals(fupload.getFilename())) 
					{
						dupeFound = true;
						// when a duplicate is found, ask the user if they want to overwrite it
						MessageBox.confirm(displayStrings.duplicateFileTitle(), displayStrings.duplicateFileText(), l);
						break;
					}
				}
				
				//no duplicates..enable upload button
				if (!dupeFound) 
				{
					uploadButton.setEnabled(true);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());
			}
		});		
	}
	
	/**
	 * Provide the root layout panel as the display widget. 
	 */
	@Override
	public Widget getDisplayWidget() 
	{
		return pnlLayout;
	}

	@Override
	public void handleOkClick() 
	{
		// there is no OK button in the panel to handle
	}
}