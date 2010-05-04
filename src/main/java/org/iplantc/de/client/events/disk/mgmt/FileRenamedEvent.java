package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

public class FileRenamedEvent extends GwtEvent<FileRenamedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	private String name;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileRenamedEventHandler> TYPE = new GwtEvent.Type<FileRenamedEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileRenamedEvent(String id,String name)
	{
		this.id = id;
		this.name = name;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileRenamedEventHandler handler) 
	{
		handler.onRenamed(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FileRenamedEventHandler> getAssociatedType() 
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
