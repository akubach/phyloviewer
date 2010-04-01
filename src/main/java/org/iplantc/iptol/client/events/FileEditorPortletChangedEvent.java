package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorPortletChangedEvent extends GwtEvent<FileEditorPortletChangedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String idFile;
	private boolean dirty;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorPortletChangedEventHandler> TYPE = new GwtEvent.Type<FileEditorPortletChangedEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileEditorPortletChangedEvent(String idFile,boolean dirty)
	{
		this.idFile = idFile;
		this.dirty = dirty;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileEditorPortletChangedEventHandler handler) 
	{
		handler.onChanged(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FileEditorPortletChangedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}
	
	//////////////////////////////////////////
	public boolean isDirty()
	{
		return dirty;
	}
}
