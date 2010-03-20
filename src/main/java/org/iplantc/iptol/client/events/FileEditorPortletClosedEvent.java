package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorPortletClosedEvent extends GwtEvent<FileEditorPortletClosedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String id;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorPortletClosedEventHandler> TYPE = new GwtEvent.Type<FileEditorPortletClosedEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileEditorPortletClosedEvent(String id)
	{
		this.id = id;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileEditorPortletClosedEventHandler handler) 
	{
		handler.onClosed(this);
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FileEditorPortletClosedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	//////////////////////////////////////////
	public String getId()
	{
		return id;
	}
}
