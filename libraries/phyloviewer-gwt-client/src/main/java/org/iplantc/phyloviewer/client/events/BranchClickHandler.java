package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface BranchClickHandler extends EventHandler
{
	public void onBranchClick(BranchClickEvent event);
}
