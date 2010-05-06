package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for a form file upload completed event. 
 *
 * Subclasses of this handler are called when file upload is complete by the FileUploadPanel
 * 
 * @see org.iplantc.de.client.views.panels.FileUploadPanel
 * @see org.iplantc.de.client.events.DefaultUploadCompleteHandler
 */
public abstract class UploadCompleteHandler implements EventHandler
{
	private String parentId;

	public UploadCompleteHandler(String idParent)
	{
		if(idParent == null || idParent.length() == 0)
		{
			throw new IllegalArgumentException("Argument, idParent, must have a valid value provided.");
		}
		this.parentId = idParent;
	}

	public String getParentId()
	{
		return parentId;
	}

	public abstract void onCompletion(String response);

	public abstract void onAfterCompletion();
}
