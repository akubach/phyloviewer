package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event representation a FileEditorWindow being closed by the user. 
 */
public class FileEditorWindowClosedEvent extends GwtEvent<FileEditorWindowClosedEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<FileEditorWindowClosedEventHandler> TYPE = new GwtEvent.Type<FileEditorWindowClosedEventHandler>();

	// ////////////////////////////////////////
	// private variables
	private String id;

	// ////////////////////////////////////////
	// constructor
	public FileEditorWindowClosedEvent(String id)
	{
		this.id = id;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(FileEditorWindowClosedEventHandler handler)
	{
		handler.onClosed(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<FileEditorWindowClosedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getId()
	{
		return id;
	}
}
