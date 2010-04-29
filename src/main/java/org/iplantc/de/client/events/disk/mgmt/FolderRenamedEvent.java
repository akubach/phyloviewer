package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

public class FolderRenamedEvent extends GwtEvent<FolderRenamedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FolderRenamedEventHandler> TYPE = new GwtEvent.Type<FolderRenamedEventHandler>();

	//////////////////////////////////////////
	//constructor
	public FolderRenamedEvent(String id,String name)
	{
		this.id = id;
		this.name = name;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FolderRenamedEventHandler handler) 
	{
		handler.onRenamed(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FolderRenamedEventHandler> getAssociatedType() 
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
