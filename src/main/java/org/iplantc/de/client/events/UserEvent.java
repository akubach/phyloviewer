package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Generic event represents user interactions with the windowed application environment.
 * 
 * The intention is that this event will be fired when a user clicks in a menu. The action
 * and tag values will be provided to handlers to that they may determine operation and
 * appropriately react.
 */
public class UserEvent extends GwtEvent<UserEventHandler>
{
	// ////////////////////////////////////////
	// type
	public static final GwtEvent.Type<UserEventHandler> TYPE = new GwtEvent.Type<UserEventHandler>();

	// ////////////////////////////////////////
	// protected variables
	protected String action = new String();
	protected String tag = new String();

	// ////////////////////////////////////////
	// constructor
	public UserEvent(String action, String tag)
	{
		setAction(action);
		setTag(tag);
	}

	// ////////////////////////////////////////
	// protected methods
	@Override
	protected void dispatch(UserEventHandler handler)
	{
		handler.onEvent(this);
	}

	// ////////////////////////////////////////
	// public methods
	@Override
	public Type<UserEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	// ////////////////////////////////////////
	public String getAction()
	{
		return action;
	}

	// ////////////////////////////////////////
	public void setAction(String action)
	{
		if(action != null)
		{
			this.action = action;
		}
	}

	// ////////////////////////////////////////
	public String getTag()
	{
		return tag;
	}

	// ////////////////////////////////////////
	public void setTag(String tag)
	{
		if(tag != null)
		{
			this.tag = tag;
		}
	}
}
