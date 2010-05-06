package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.DEDisplayStrings;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;

/**
 * Provides a base class for windows in the application desktop.  
 */
public abstract class IPlantWindow extends Window
{
	// ////////////////////////////////////////
	// protected variables
	protected static DEDisplayStrings displayStrings = (DEDisplayStrings)GWT
	.create(DEDisplayStrings.class);
	protected String tag;

	// ////////////////////////////////////////
	// constructor
	protected IPlantWindow(String tag)
	{
		this.tag = tag;
	}

	// ////////////////////////////////////////
	// public methods
	public String getTag()
	{
		return tag;
	}

	// ////////////////////////////////////////
	public abstract void cleanup();
}
