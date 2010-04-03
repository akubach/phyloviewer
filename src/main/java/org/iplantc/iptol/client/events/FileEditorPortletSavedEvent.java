package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorPortletSavedEvent extends GwtEvent<FileEditorPortletSavedEventHandler> 
{
	//////////////////////////////////////////
	//type
	private String idFile;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorPortletSavedEventHandler> TYPE = new GwtEvent.Type<FileEditorPortletSavedEventHandler>();

	//////////////////////////////////////////
	//constructor	
	public FileEditorPortletSavedEvent(String idFile)
	{
		this.idFile = idFile;		
	}
	
	//////////////////////////////////////////
	//protected methods	
	@Override
	protected void dispatch(FileEditorPortletSavedEventHandler handler) 
	{
		handler.onSaved(this);
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FileEditorPortletSavedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}	
}
