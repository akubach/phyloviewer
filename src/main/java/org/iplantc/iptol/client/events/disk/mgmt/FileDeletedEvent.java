package org.iplantc.iptol.client.events.disk.mgmt;

import com.google.gwt.event.shared.GwtEvent;

public class FileDeletedEvent extends GwtEvent<FileDeletedEventHandler> 
{
	//////////////////////////////////////////
	//private methods
	private String id;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileDeletedEventHandler> TYPE = new GwtEvent.Type<FileDeletedEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileDeletedEvent(String id)
	{
		this.id = id;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileDeletedEventHandler handler) 
	{
		handler.onDeleted(this);
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FileDeletedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	public String getId()
	{
		return id;
	}	
}
