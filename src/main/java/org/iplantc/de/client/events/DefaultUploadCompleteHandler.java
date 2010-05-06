package org.iplantc.de.client.events;

import java.util.ArrayList;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.de.client.models.JsFile;
import org.iplantc.de.client.utils.JsonUtil;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * General handler for file upload. Expects to be called after form submission is
 * complete.
 * 
 * @author lenards
 * 
 */
public class DefaultUploadCompleteHandler extends UploadCompleteHandler
{

	/**
	 * Construct a new instance of the default handler. 
	 * @param idParent the parent identifier to upload the file
	 */
	public DefaultUploadCompleteHandler(String idParent)
	{
		super(idParent);
	}

	/**
	 * Invoked on completion of file upload form submission.
	 * 
	 * @param response the server response in JSON format
	 */
	@Override
	public void onCompletion(String response)
	{
		String deletedIdsKey = "deletedIds";
		DEDisplayStrings displayStrings = (DEDisplayStrings)GWT.create(DEDisplayStrings.class);
		boolean isValid = checkForErrors(response);
		if(response != null && isValid)
		{
			JSONObject jsonData = JSONParser.parse(response).isObject();
			JsArray<JsFile> fileInfos = JsonUtil.asArrayOf(jsonData.get("created").toString());

			// TODO: define a JsArray to ArrayList conversion utility method in
			// JsonBuilder
			JSONArray arr = null;
			if(jsonData.get(deletedIdsKey) != null)
			{
				arr = jsonData.get(deletedIdsKey).isArray();
			}
			StringBuffer sb = null;
			ArrayList<String> deleteIds = null;
			if(arr != null)
			{
				deleteIds = new ArrayList<String>();
				// remove surrounding quotes
				for(int i = 0;i < arr.size();i++)
				{
					sb = new StringBuffer(arr.get(i).toString());
					sb.deleteCharAt(0);
					sb.deleteCharAt(sb.length() - 1);
					deleteIds.add(sb.toString());
				}
			}

			// there is always only one record
			JsFile info = (fileInfos != null) ? fileInfos.get(0) : null;
			if(info != null)
			{
				EventBus eventbus = EventBus.getInstance();
				FileUploadedEvent event = new FileUploadedEvent(getParentId(), info, deleteIds);
				eventbus.fireEvent(event);

				Info.display(displayStrings.fileUpload(), displayStrings.fileUploadSuccess());
			}
		}
		else
		{
			DEErrorStrings errorStrings = (DEErrorStrings)GWT.create(DEErrorStrings.class);
			ErrorHandler.post(errorStrings.fileUploadFailed());
		}

		// TODO: consider having onCompletion and onAfterCompletion called by superclass
		// method
		// to more appropriately confirm w/ Template Method and Command patterns
		onAfterCompletion();
	}

	/**
	 * Determine if the response contains error information.
	 * 
	 * @param response response from the servlet; either JSON for a valid response, or
	 *            Error XML
	 * @return true if the response is valid; otherwise false.
	 */
	private boolean checkForErrors(String response)
	{
		if(response.startsWith("<response><error>") || response.contains("Exception"))
		{ // the response will be XML and start with <response> & <error> elements
			// and contain some description of the Exception in the inner-text.
			return false;
		}
		else if(response.startsWith("{"))
		{ // if it starts with this, then it's valid JSON
			return true;
		}
		return false;
	}

	/**
	 * Invoked immediately following onCompletion(). 
	 * 
	 * Provides a manner for handlers to do cleanup or post-completion operations.
	 */
	@Override
	public void onAfterCompletion()
	{
		// Let the specific instance provide an implementation. This is not abstract
		// because
		// then the class would to be abstract and there might be a case when you want to
		// use this without defining this action.
	}
}
