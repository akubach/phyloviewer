package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FileEditorWindowSavedEvent extends GwtEvent<FileEditorWindowSavedEventHandler> 
{
	//////////////////////////////////////////
	//type
	private String idFile;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileEditorWindowSavedEventHandler> TYPE = new GwtEvent.Type<FileEditorWindowSavedEventHandler>();

	//////////////////////////////////////////
	//constructor	
	public FileEditorWindowSavedEvent(String idFile)
	{
		this.idFile = idFile;		
	}
	
	//////////////////////////////////////////
	//protected methods	
	@Override
	protected void dispatch(FileEditorWindowSavedEventHandler handler) 
	{
		handler.onSaved(this);
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FileEditorWindowSavedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getFileId()
	{
		return idFile;
	}	
}
