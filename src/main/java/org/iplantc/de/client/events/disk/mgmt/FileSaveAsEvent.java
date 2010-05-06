package org.iplantc.de.client.events.disk.mgmt;

import org.iplantc.de.client.models.JsFile;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a file "save as" event. 
 */
public class FileSaveAsEvent extends GwtEvent<FileSaveAsEventHandler>
{
	// ////////////////////////////////////////
	// private variables
	private String idParent;
	private String idOrig;
	private JsFile info;

	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<FileSaveAsEventHandler> TYPE = new GwtEvent.Type<FileSaveAsEventHandler>();

	// ////////////////////////////////////////
	// constructor
	public FileSaveAsEvent(String idParent, String idOrig, JsFile info)
	{
		this.idParent = idParent;
		this.idOrig = idOrig;
		this.info = info;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(FileSaveAsEventHandler handler)
	{
		handler.onSaved(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<FileSaveAsEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getParentId()
	{
		return idParent;
	}

	// ////////////////////////////////////////
	public String getOriginalFileId()
	{
		return idOrig;
	}

	// ////////////////////////////////////////
	public JsFile getFileInfo()
	{
		return info;
	}
}
