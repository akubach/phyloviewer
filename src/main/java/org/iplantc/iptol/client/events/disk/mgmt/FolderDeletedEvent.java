package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

public class FolderDeletedEvent extends GwtEvent<FolderDeletedEventHandler>  
{
	//////////////////////////////////////////
	//private variables
	private String id;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FolderDeletedEventHandler> TYPE = new GwtEvent.Type<FolderDeletedEventHandler>();

	//////////////////////////////////////////
	//constructor
	public FolderDeletedEvent(String id)
	{
		this.id = id;
	}

	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FolderDeletedEventHandler handler) 
	{
		handler.onDeleted(this);		
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FolderDeletedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getId()
	{
		return id;
	}
}
