package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

public class FolderCreatedEvent extends GwtEvent<FolderCreatedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FolderCreatedEventHandler> TYPE = new GwtEvent.Type<FolderCreatedEventHandler>();

	//////////////////////////////////////////
	//constructor
	public FolderCreatedEvent(String id,String name)
	{
		this.id = id;
		this.name = name;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FolderCreatedEventHandler handler) 
	{
		handler.onCreated(this);		
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FolderCreatedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getId()
	{
		return id;
	}
	
	//////////////////////////////////////////
	public String getName()
	{
		return name;
	}
}
