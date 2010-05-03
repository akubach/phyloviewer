package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class UserEvent extends GwtEvent<UserEventHandler> 
{
	//////////////////////////////////////////
	//protected variables
	protected String action = new String();
	protected String tag = new String();
		
	//////////////////////////////////////////
	//type
	public static final GwtEvent.Type<UserEventHandler> TYPE = new GwtEvent.Type<UserEventHandler>();
	
	//////////////////////////////////////////
	//constructor
	public UserEvent(String action,String tag)
	{
		setAction(action);
		setTag(tag);		
	}
	
	//////////////////////////////////////////
	//protected methods	
	@Override
	protected void dispatch(UserEventHandler handler) 
	{
		handler.onEvent(this);
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public Type<UserEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getAction()
	{
		return action;
	}
	
	//////////////////////////////////////////
	public void setAction(String action)
	{
		if(action != null)
		{
			this.action = action;
		}
	}
	
	//////////////////////////////////////////
	public String getTag()
	{
		return tag;
	}
	
	//////////////////////////////////////////
	public void setTag(String tag)
	{
		if(tag != null)
		{
			this.tag = tag;
		}		
	}	
}
