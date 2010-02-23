package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class FolderEvent extends GwtEvent<FolderEventHandler> 
{
	//////////////////////////////////////////
	//types
	public static final GwtEvent.Type<FolderEventHandler> TYPE = new GwtEvent.Type<FolderEventHandler>();
	
	public enum Action
	{
		CREATE,
		RENAME,
		DELETE
	}
		
	//////////////////////////////////////////
	//private variables
	private Action action;
	private String name;
	private String id;
		
	//////////////////////////////////////////
	//constructors
	public FolderEvent(Action action,String name,String id)
	{
		this.action = action;
		this.name = name;
		this.id = id;		
	}
	
	//////////////////////////////////////////
	public FolderEvent(Action action,String name)
	{		
		this(action,name,"-1");		
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
		else if (action == Action.DELETE)
		{
			handler.onDelete(this);
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
	
	//////////////////////////////////////////
	public String getId()
	{
		return id;
	}
}
