package org.iplantc.iptol.client.events.disk.mgmt;

import org.iplantc.iptol.client.models.FileInfo;

import com.google.gwt.event.shared.GwtEvent;

public class FileUploadedEvent extends GwtEvent<FileUploadedEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String idParent;
	private FileInfo info;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileUploadedEventHandler> TYPE = new GwtEvent.Type<FileUploadedEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileUploadedEvent(String idParent,FileInfo info)
	{
		this.idParent = idParent;
		this.info = info;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileUploadedEventHandler handler) 
	{
		handler.onUploaded(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<FileUploadedEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getParentId()
	{
		return idParent;
	}
	
	//////////////////////////////////////////
	public FileInfo getFileInfo()
	{
		return info;
	}	
}
