package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.DEDisplayStrings;

import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public abstract class IPlantDialogPanel 
{
	//////////////////////////////////////////
	//protected variables
	protected ButtonBar parentButtons;
	protected static final DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	
	//////////////////////////////////////////
	//public variables	
	public void setButtonBar(ButtonBar buttons)
	{
		parentButtons = buttons;
	}
	
	//////////////////////////////////////////
	public abstract void handleOkClick();
	
	//////////////////////////////////////////
	public abstract Widget getDisplayWidget();
}
