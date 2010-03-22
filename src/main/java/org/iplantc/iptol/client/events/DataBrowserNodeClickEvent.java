package org.iplantc.iptol.client.events;

import org.iplantc.iptol.client.models.DiskResource;

import com.google.gwt.event.shared.GwtEvent;

public class DataBrowserNodeClickEvent extends GwtEvent <DataBrowserNodeClickEventHandler>
{
	public static final GwtEvent.Type<DataBrowserNodeClickEventHandler> TYPE = new GwtEvent.Type<DataBrowserNodeClickEventHandler>();
	
	private DiskResource resource;
	
	public DataBrowserNodeClickEvent(DiskResource resource) 
	{
		this.resource = resource;
	}
		
	@Override
	protected void dispatch(DataBrowserNodeClickEventHandler handler) 
	{
		handler.onNodeClick(this);
	}

	@Override
	public Type<DataBrowserNodeClickEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	public DiskResource getDiskResource() 
	{
		return resource;
	}
}
