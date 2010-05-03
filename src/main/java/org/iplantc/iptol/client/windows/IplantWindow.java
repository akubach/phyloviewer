package org.iplantc.iptol.client.windows;

import org.iplantc.iptol.client.IptolDisplayStrings;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;

public abstract class IplantWindow extends Window 
{	
	//////////////////////////////////////////
	//protected variables
	protected String tag;
	protected static IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	protected IplantWindow(String tag)
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
