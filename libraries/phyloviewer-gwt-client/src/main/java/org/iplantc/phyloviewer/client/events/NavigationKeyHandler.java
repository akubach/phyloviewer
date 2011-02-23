package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.View;

import com.google.gwt.event.dom.client.HandlesAllKeyEvents;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;

public class NavigationKeyHandler extends HandlesAllKeyEvents
{
	private final View view;
	
	public NavigationKeyHandler(View view)
	{
		this.view = view;
	}

	@Override
	public void onKeyDown(KeyDownEvent event)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onKeyUp(KeyUpEvent event)
	{
		if(event.isUpArrow())
		{
			view.pan(0.0, 0.1);
		}
		else if(event.isDownArrow())
		{
			view.pan(0.0, -0.1);
		}
		else if(event.isLeftArrow())
		{
			view.pan(0.1, 0.0);
		}
		else if(event.isRightArrow())
		{
			view.pan(-0.1, 0.0);
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		final char charCode = event.getCharCode();
		if(charCode == ' ')
		{
			view.zoomToFit();
		}
	}
}
