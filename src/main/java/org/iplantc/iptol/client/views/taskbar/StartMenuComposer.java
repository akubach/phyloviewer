package org.iplantc.iptol.client.views.taskbar;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolClientConstants;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.events.UserEvent;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.core.client.GWT;

public abstract class StartMenuComposer 
{
	//////////////////////////////////////////
	//protected variables
	protected String header;
	protected SelectionListener<MenuEvent> menuListener;	
	protected static IptolClientConstants constants = (IptolClientConstants)GWT.create(IptolClientConstants.class);
	protected static final IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	protected StartMenuComposer(String header)
	{
		this.header = header;
	}
	
	//////////////////////////////////////////
	//protected methods
	protected void itemSelected(MenuEvent me) 
	{
		String action = me.getItem().getData("action");
		String tag = me.getItem().getData("tag");
		
		EventBus eventbus = EventBus.getInstance();
		UserEvent event = new UserEvent(action,tag);
		eventbus.fireEvent(event);
	}
	
	//////////////////////////////////////////
	protected void initMenuListener()
	{
		menuListener = new SelectionListener<MenuEvent>() 
		{
			@Override
			public void componentSelected(MenuEvent me) 
			{
				itemSelected(me);
			}
		};	
	}
	
	//////////////////////////////////////////
	protected void setHeading(StartMenu menu)
	{
		if(header != null)
		{
			menu.setHeading(header);
		}
	}
	
	//////////////////////////////////////////
	protected abstract void addMenuItems(StartMenu menu);
	
	//////////////////////////////////////////
	public void compose(StartMenu menu)
	{
		initMenuListener();
		setHeading(menu);
		
		addMenuItems(menu);
	}
}
