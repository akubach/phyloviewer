package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorPortletDirtyEvent extends GwtEvent<FileEditorPortletDirtyEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String idFile;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorPortletDirtyEventHandler> TYPE = new GwtEvent.Type<FileEditorPortletDirtyEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileEditorPortletDirtyEvent(String idFile)
	{
		this.idFile = idFile;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileEditorPortletDirtyEventHandler handler) 
	{
		handler.onDirty(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FileEditorPortletDirtyEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}	
}
