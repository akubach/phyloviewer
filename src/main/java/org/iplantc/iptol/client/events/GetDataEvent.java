package org.iplantc.iptol.client.events;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

public class GetDataEvent extends GwtEvent<GetDataEventHandler> 
{
	//////////////////////////////////////////
	//types
	public enum DataType
	{
		RAW,
		TRAIT,
		TREE
	}
	
	public static final GwtEvent.Type<GetDataEventHandler> TYPE = new GwtEvent.Type<GetDataEventHandler>();

	//////////////////////////////////////////
	//private variables
	private DataType type;
	private List<String> ids = new ArrayList<String>();
	
	//////////////////////////////////////////
	//constructor
	public GetDataEvent(DataType type,List<String> ids)
	{
		this.type = type;
		this.ids = ids;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(GetDataEventHandler handler) 
	{
		handler.onGet(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<GetDataEventHandler> getAssociatedType() 
	{
		return TYPE;
	}
	
	//////////////////////////////////////////
	public DataType getType()
	{
		return type;
	}
	
	//////////////////////////////////////////
	public List<String> getIds()
	{
		return ids;
	}	
}
