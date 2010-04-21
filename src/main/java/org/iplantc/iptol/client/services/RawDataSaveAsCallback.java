package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.JsonBuilder;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.iptol.client.models.FileInfo;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawDataSaveAsCallback implements AsyncCallback<String>
{
	//////////////////////////////////////////
	//private variables
	private String idParent;
	private String idOrig;
	private MessageBox wait;
	
	//////////////////////////////////////////
	//constructor
	public RawDataSaveAsCallback(String idParent,String idOrig, MessageBox wait)
	{
		this.idParent = idParent;
		this.idOrig = idOrig;
		this.wait = wait;
		wait.show();
	}
	
	//////////////////////////////////////////
	//public methods
	@Override
	public void onFailure(Throwable arg0) 
	{
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
		ErrorHandler.post(errorStrings.rawDataSaveFailed());
		wait.close();
	}

	//////////////////////////////////////////
	@Override
	public void onSuccess(String result) 
	{		
		if(result != null)
		{
			JsArray<FileInfo> fileInfos = JsonBuilder.asArrayofFileData(result);

			//there is always only one record
			if(fileInfos != null)
			{
				FileInfo info = fileInfos.get(0);
				
				if(info != null)
				{
					EventBus eventbus = EventBus.getInstance();
					//fire event that
					FileSaveAsEvent event = new FileSaveAsEvent(idParent,idOrig,info);
					eventbus.fireEvent(event);					
				}
			}
		}
		wait.close();
	}
}
