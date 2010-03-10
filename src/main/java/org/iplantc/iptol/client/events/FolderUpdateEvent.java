package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FolderUpdateEvent extends GwtEvent<FolderUpdateEventHandler> 
{
	//////////////////////////////////////////
	//types
	public static final GwtEvent.Type<FolderUpdateEventHandler> TYPE = new GwtEvent.Type<FolderUpdateEventHandler>();
		
	//////////////////////////////////////////
	//constructors
	public FolderUpdateEvent()
	{
	}
	
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FolderUpdateEventHandler handler) 
	{
		handler.onUpdateComplete();
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FolderUpdateEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
}
