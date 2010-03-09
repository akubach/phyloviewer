package org.iplantc.iptol.client.services;

import org.iplantc.iptol.client.events.FolderUpdateEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FolderUpdater  implements AsyncCallback<String>
{
	private HandlerManager eventbus;

	public FolderUpdater(HandlerManager eventbus)
	{
		this.eventbus = eventbus;
	}
	
	@Override
	public void onFailure(Throwable caught)
	{
		//TODO: handle failure
	}

	@Override
	public void onSuccess(String result)
	{
		FolderUpdateEvent event = new FolderUpdateEvent();
		eventbus.fireEvent(event);
	}
}