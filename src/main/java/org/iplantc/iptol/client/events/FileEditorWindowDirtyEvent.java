package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorWindowDirtyEvent extends GwtEvent<FileEditorWindowDirtyEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String idFile;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorWindowDirtyEventHandler> TYPE = new GwtEvent.Type<FileEditorWindowDirtyEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileEditorWindowDirtyEvent(String idFile)
	{
		this.idFile = idFile;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileEditorWindowDirtyEventHandler handler) 
	{
		handler.onDirty(this);
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
