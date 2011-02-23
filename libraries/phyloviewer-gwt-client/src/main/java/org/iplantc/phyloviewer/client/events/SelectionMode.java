package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;

public class SelectionMode extends InteractionMode
{

	public SelectionMode(DetailView detailView)
	{
		super(new SelectionKeyHandler(detailView), new SelectionMouseHandler(detailView), "selection");
	}

	/** @return the mouse handler */
	public SelectionMouseHandler getMouseHandler()
	{
		return (SelectionMouseHandler)super.getMouseHandler();
	}
}
