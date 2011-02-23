package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.client.tree.viewer.DetailView;

public class NavigationMode extends InteractionMode
{

	public NavigationMode(DetailView detailView)
	{
		super(new NavigationKeyHandler(detailView), new NavigationMouseHandler(detailView), "navigation");
	}

	@Override
	public NavigationMouseHandler getMouseHandler()
	{
		return (NavigationMouseHandler)super.getMouseHandler();
	}
}
