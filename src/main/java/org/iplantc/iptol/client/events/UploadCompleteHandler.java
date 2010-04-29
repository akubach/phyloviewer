package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

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
