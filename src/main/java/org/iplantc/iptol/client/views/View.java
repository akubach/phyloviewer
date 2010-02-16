package org.iplantc.iptol.client.views;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;

public abstract class View 
{	
	//////////////////////////////////////////
	//protected variables
	protected final HandlerManager eventbus;
	
	//////////////////////////////////////////
	//constructor
	protected View(final HandlerManager eventbus)
	{
		this.eventbus = eventbus;
	}
	
	//////////////////////////////////////////
	public abstract Widget getDisplayWidget();
}
