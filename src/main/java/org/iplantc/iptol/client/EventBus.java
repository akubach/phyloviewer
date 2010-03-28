package org.iplantc.iptol.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;

public class EventBus 
{
	class HandlerWrapper
	{
		//////////////////////////////////////////
		//private variables
		private GwtEvent.Type<EventHandler> type;
		private HandlerRegistration handler;
		
		//////////////////////////////////////////
		//constructor
		public HandlerWrapper(Type<EventHandler> type,HandlerRegistration handler)
		{
			this.type = type;
			this.handler = handler;
		}
		
		//////////////////////////////////////////
		//public methods
		public GwtEvent.Type<EventHandler> getType()
		{
			return type;			
		}
		
		//////////////////////////////////////////
		public HandlerRegistration getHandler()
		{
			return handler;
		}

		//////////////////////////////////////////
		public void removeHandler()
		{
			handler.removeHandler();
		}
	}
	
	//////////////////////////////////////////
	//private variables
	private static EventBus instance;	
	private HandlerManager eventbus;
	private List<HandlerWrapper> wrappers = new ArrayList<HandlerWrapper>();
	
	//////////////////////////////////////////
	//constructor
	private EventBus()
	{
		eventbus = new HandlerManager(this);
	}
	
	//////////////////////////////////////////
	//public methods
	public static EventBus getInstance() 
	{
		if(instance == null) 
		{
			instance = new EventBus();
		}
		
		return instance;
	}	
		
	//////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <H extends EventHandler> HandlerRegistration addHandler(
		      GwtEvent.Type<H> type, final H handler)
	{
		HandlerRegistration reg = eventbus.addHandler(type,handler);
		wrappers.add(new HandlerWrapper((Type<EventHandler>) type,reg));
		
		return reg;
	}

	//////////////////////////////////////////
	public void removeHandlers(Type<? extends EventHandler> type)
	{
		List<HandlerWrapper> deleted = new ArrayList<HandlerWrapper>();
		
		//build our delete list
		for(HandlerWrapper wrapper : wrappers)
		{
			if(wrapper.getType().equals(type))
			{
				deleted.add(wrapper);
			}
		}
		
		//perform our delete
		for(HandlerWrapper wrapper : deleted)
		{
			wrapper.removeHandler();
			wrappers.remove(wrapper);
		}
	}
	
	//////////////////////////////////////////
	public void fireEvent(GwtEvent<?> event)
	{
		eventbus.fireEvent(event);
	}

	//////////////////////////////////////////
	public void clearHandlers()
	{
		for(HandlerWrapper wrapper : wrappers)
		{			
			wrapper.removeHandler();			
		}
		
		wrappers.clear();
	}
}
