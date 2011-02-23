package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.dom.client.HandlesAllKeyEvents;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;

public class InteractionMode
{
	private HandlesAllKeyEvents keyHandler;
	private HandlesAllMouseEvents mouseHandler;
	private String styleName;
	
	public InteractionMode(HandlesAllKeyEvents keyHandler, HandlesAllMouseEvents mouseHandler, String styleName)
	{
		this.keyHandler = keyHandler;
		this.mouseHandler = mouseHandler;
		this.styleName = styleName;
	}
	
	/** @return the keyboard handler */
	public HandlesAllKeyEvents getKeyHandler()
	{
		return keyHandler;
	}
	
	/** @return the mouse handler */
	public HandlesAllMouseEvents getMouseHandler()
	{
		return mouseHandler;
	}
	
	/** @return a css class for styling mouse cursor, etc. */
	public String getStyleName()
	{
		return styleName;
	}
}
