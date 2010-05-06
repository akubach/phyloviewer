package org.iplantc.de.client.events;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.models.FileIdentifier;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents the acquisition of data from a remote service.   
 *
 */
public class GetDataEvent extends GwtEvent<GetDataEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<GetDataEventHandler> TYPE = new GwtEvent.Type<GetDataEventHandler>();

	// ////////////////////////////////////////
	// private variables
	private List<FileIdentifier> files = new ArrayList<FileIdentifier>();

	// ////////////////////////////////////////
	// constructor
	public GetDataEvent(List<FileIdentifier> files)
	{
		this.files = files;
	}

	// ////////////////////////////////////////
	public GetDataEvent(FileIdentifier file)
	{
		files.add(file);
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(GetDataEventHandler handler)
	{
		handler.onGet(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<GetDataEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public List<FileIdentifier> getFiles()
	{
		return files;
	}
}
