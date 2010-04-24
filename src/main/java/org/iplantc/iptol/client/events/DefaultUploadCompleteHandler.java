package org.iplantc.iptol.client.events;

import java.util.ArrayList;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.JsonBuilder;
import org.iplantc.iptol.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.iptol.client.models.FileInfo;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * General handler for file upload.  Expects to be called after form 
 * submission is complete. 
 * 
 * @author lenards
 *
 */
public class DefaultUploadCompleteHandler extends UploadCompleteHandler {

	/**
	 * 
	 * @param idParent the parent identifier to upload the file
	 */
	public DefaultUploadCompleteHandler(String idParent) {
		super(idParent);
	}

	/**
	 * Invoked on completion of file upload form submission. 
	 * 
	 * @param response the server response in JSON format
	 */
	@Override
	public void onCompletion(String response) {
		IptolDisplayStrings displayStrings = (IptolDisplayStrings)GWT.create(IptolDisplayStrings.class);
		if(response != null)
		{	
			JSONObject jsonData = JSONParser.parse(response).isObject();
			JsArray<FileInfo> fileInfos = JsonBuilder.asArrayofFileData(jsonData.get("created").toString());

			// TODO: define a JsArray to ArrayList conversion utility method in JsonBuilder
			JSONArray arr = null;
			if (jsonData.get("deletedIds") != null) {
				arr = jsonData.get("deletedIds").isArray();
			}
			StringBuffer sb = null;
			ArrayList<String> deleteIds = null;
			if (arr != null) {
				deleteIds = new ArrayList<String>();
				// remove surrounding quotes
				for (int i = 0; i < arr.size(); i++) {
					sb = new StringBuffer(arr.get(i).toString());
					sb.deleteCharAt(0);
					sb.deleteCharAt(sb.length() - 1);
					deleteIds.add(sb.toString());
				}
			}
			
			// there is always only one record
			FileInfo info = (fileInfos != null) ? fileInfos.get(0) : null;
			if (info != null) {
				EventBus eventbus = EventBus.getInstance();
				FileUploadedEvent event = new FileUploadedEvent(
						getParentId(), info, deleteIds);
				eventbus.fireEvent(event);

				Info.display(displayStrings.fileUpload(), 
						displayStrings.fileUploadSuccess());
			}
		}	
		else
		{
			IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
			// TODO: ErrorManager should be called here
			MessageBox.alert(displayStrings.fileUpload(), errorStrings.fileUploadFailed(), null);
		}

		// TODO: consider having onCompletion and onAfterCompletion called by superclass method 
		// to more appropriately confirm w/ Template Method and Command patterns
		onAfterCompletion();
	}
	
	@Override
	public void onAfterCompletion() {
		// Let the specific instance provide an implementation.  This is not abstract because 
		// then the class would to be abstract and there might be a case when you want to  
		// use this without defining this action.
	}
}
