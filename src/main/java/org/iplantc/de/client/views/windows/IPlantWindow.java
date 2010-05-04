package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.DEDisplayStrings;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;

public abstract class IPlantWindow extends Window 
{	
	//////////////////////////////////////////
	//protected variables
	protected String tag;
	protected static DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	protected IPlantWindow(String tag)
	{
		this.tag = tag;
	}
	
	//////////////////////////////////////////
	//public methods
	public String getTag()
	{
		return tag;
	}
	
	//////////////////////////////////////////
	public abstract void cleanup();
}
