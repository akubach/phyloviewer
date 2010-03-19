package org.iplantc.iptol.client.dialogs.panels;

import org.iplantc.iptol.client.IptolDisplayStrings;

import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;

public abstract class IPlantDialogPanel 
{
	//////////////////////////////////////////
	//protected variables
	protected HandlerManager eventbus;
	protected ButtonBar parentButtons;
	protected static final IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//protected methods	
	protected IPlantDialogPanel(HandlerManager eventbus)
	{
		this.eventbus = eventbus;		
	}
	
	//////////////////////////////////////////
	//public variables	
	public void setButtonBar(ButtonBar buttons)
	{
		parentButtons = buttons;
	}
	
	//////////////////////////////////////////
	public abstract void handleOkClick();
	
	//////////////////////////////////////////
	public abstract Widget getDisplayComponent();
}
