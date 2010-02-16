package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FolderEvent extends GwtEvent<FolderEventHandler> 
{
	//////////////////////////////////////////
	//types
	public enum Action
	{
		CREATE,
		RENAME
	}
	
	public static final GwtEvent.Type<FolderEventHandler> TYPE = new GwtEvent.Type<FolderEventHandler>();
	
	//////////////////////////////////////////
	//private variables
	private String name;
	private Action action;
	
	//////////////////////////////////////////
	//constructor
	public FolderEvent(Action action,String name)
	{
		this.action = action;
		this.name = name;
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override
	protected void dispatch(FolderEventHandler handler) 
	{
		if(action == Action.CREATE)
		{
			handler.onCreate(this);
		}
		else if (action == Action.RENAME)
		{
			handler.onRename(this);
		}
	}

	//////////////////////////////////////////
	//public methods
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FolderEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	//////////////////////////////////////////
	public String getName()
	{
		return name;
	}
}
