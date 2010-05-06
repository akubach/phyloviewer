package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorWindowDirtyEvent extends GwtEvent<FileEditorWindowDirtyEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String idFile;
	private boolean dirty;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorWindowDirtyEventHandler> TYPE = new GwtEvent.Type<FileEditorWindowDirtyEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileEditorWindowDirtyEvent(String idFile,boolean dirty)
	{
		this.idFile = idFile;
		this.dirty = dirty;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileEditorWindowDirtyEventHandler handler) 
	{
		if(dirty)
		{
			handler.onDirty(this);
		}
		else
		{
			handler.onClean(this);
		}
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FileEditorWindowDirtyEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}	
}
