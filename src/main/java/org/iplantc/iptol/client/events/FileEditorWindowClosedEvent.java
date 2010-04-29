package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorWindowClosedEvent extends GwtEvent<FileEditorWindowClosedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorWindowClosedEventHandler> TYPE = new GwtEvent.Type<FileEditorWindowClosedEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileEditorWindowClosedEvent(String id)
	{
		this.id = id;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileEditorWindowClosedEventHandler handler) 
	{
		handler.onClosed(this);
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FileEditorWindowClosedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	//////////////////////////////////////////
	public String getId()
	{
		return id;
	}
}
