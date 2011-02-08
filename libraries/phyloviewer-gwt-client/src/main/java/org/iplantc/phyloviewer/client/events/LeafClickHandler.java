package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface LeafClickHandler extends EventHandler
{
	public void onLeafClick(LeafClickEvent event);
}
