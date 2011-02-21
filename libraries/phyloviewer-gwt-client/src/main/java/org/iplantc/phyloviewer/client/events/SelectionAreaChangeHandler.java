package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface SelectionAreaChangeHandler extends EventHandler
{
	public void onSelectionAreaChange(SelectionAreaChangeEvent event);
}
