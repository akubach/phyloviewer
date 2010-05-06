package org.iplantc.de.client.events.disk.mgmt;

import java.util.ArrayList;

import org.iplantc.de.client.models.JsFile;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a file being uploaded. 
 */
public class FileUploadedEvent extends GwtEvent<FileUploadedEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<FileUploadedEventHandler> TYPE = new GwtEvent.Type<FileUploadedEventHandler>();

	// ////////////////////////////////////////
	// private variables
	private String idParent;
	private JsFile info;
	// when a duplicate file is uploaded, we need to delete existing files
	private ArrayList<String> deleteIds;

	// ////////////////////////////////////////
	// constructor
	public FileUploadedEvent(String idParent, JsFile info, ArrayList<String> deleteIds)
	{
		this.idParent = idParent;
		this.info = info;
		this.deleteIds = deleteIds;
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(FileUploadedEventHandler handler)
	{
		handler.onUploaded(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<FileUploadedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getParentId()
	{
		return idParent;
	}

	// ////////////////////////////////////////
	public JsFile getFileInfo()
	{
		return info;
	}

	public void setDeleteIds(ArrayList<String> deleteIds)
	{
		this.deleteIds = deleteIds;
	}

	public ArrayList<String> getDeleteIds()
	{
		return deleteIds;
	}
}
