package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.iptol.client.models.FileInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawDataSaveAsCallback implements AsyncCallback<String>
{
	//////////////////////////////////////////
	//private variables
	private String idParent;
	private String idOrig;
	
	//////////////////////////////////////////
	//constructor
	public RawDataSaveAsCallback(String idParent,String idOrig)
	{
		this.idParent = idParent;
		this.idOrig = idOrig;
	}
	
	//////////////////////////////////////////
	//private methods
	private final native JsArray<FileInfo> asArrayofFileData(String json) /*-{
		return eval(json);
	}-*/;
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
		ErrorHandler.post(errorStrings.rawDataSaveFailed());		
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String result) 
	{		
		if(result != null)
		{
			JsArray<FileInfo> fileInfos = asArrayofFileData(result);

			//there is always only one record
			if(fileInfos != null)
			{
				FileInfo info = fileInfos.get(0);
				
				if(info != null)
				{
					EventBus eventbus = EventBus.getInstance();
					FileSaveAsEvent event = new FileSaveAsEvent(idParent,idOrig,info);
					eventbus.fireEvent(event);
				}
			}
		}				
	}
}
