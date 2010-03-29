package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolErrorStrings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawDataSaveAsCallback implements AsyncCallback<String>
{
	@Override
	public void onFailure(Throwable arg0) 
	{
		IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
		ErrorHandler.post(errorStrings.rawDataSaveFailed());		
	}

	@Override
	public void onSuccess(String result) 
	{		
		if(result != null)
		{
			//TODO: update tabs
		}				
	}
}
