package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface LabelClickHandler extends EventHandler
{
	public void onLabelClick(LabelClickEvent event);
}
