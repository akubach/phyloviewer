package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.View;

import com.google.gwt.event.dom.client.HandlesAllKeyEvents;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;

public class SelectionKeyHandler extends HandlesAllKeyEvents
{
	private final View view;
	
	public SelectionKeyHandler(View view)
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		// TODO Auto-generated method stub

	}

}
