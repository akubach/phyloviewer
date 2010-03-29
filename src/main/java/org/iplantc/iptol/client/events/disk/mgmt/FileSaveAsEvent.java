package org.iplantc.iptol.client.events.disk.mgmt;

import org.iplantc.iptol.client.models.FileInfo;
import com.google.gwt.event.shared.GwtEvent;

public class FileSaveAsEvent extends GwtEvent<FileSaveAsEventHandler> 
{
	//////////////////////////////////////////
	//private variables
	private String idParent;
	private String idOrig;
	private FileInfo info;
	
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<FileSaveAsEventHandler> TYPE = new GwtEvent.Type<FileSaveAsEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public FileSaveAsEvent(String idParent,String idOrig,FileInfo info)
	{
		this.idParent = idParent;
		this.idOrig = idOrig;
		this.info = info;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FileSaveAsEventHandler handler) 
	{
		handler.onSaved(this);
	}

	//////////////////////////////////////////
	//public methods	
	@Override
	public Type<FileSaveAsEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getParentId()
	{
		return idParent;
	}
	
	//////////////////////////////////////////
	public String getOriginalFileId()
	{
		return idOrig;
	}
	
	//////////////////////////////////////////
	public FileInfo getFileInfo()
	{
		return info;
	}
}
