package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ViewDataEvent extends GwtEvent<ViewDataEventHandler> 
{
	//////////////////////////////////////////
	//types
	public static final GwtEvent.Type<ViewDataEventHandler> TYPE = new GwtEvent.Type<ViewDataEventHandler>();
	
	public static enum ViewType
	{
		Raw,
		Tree		
	}
	
	//////////////////////////////////////////
	//private variables
	private String name;
	private ViewType type;
	
	//////////////////////////////////////////
	//constructor
	public ViewDataEvent(String name,ViewType type)
	{
	 	this.name = name;
	 	this.type = type;
	}
	
	@Override
	protected void dispatch(ViewDataEventHandler handler) 
	{
		handler.onView(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ViewDataEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	//////////////////////////////////////////
	public String getName()
	{
		return name;
	}
	
	//////////////////////////////////////////
	public ViewType getViewType()
	{
		return type;
	}
}
