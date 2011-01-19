package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface NodeClickHandler extends EventHandler{ 
	public void onNodeClick(NodeClickEvent event);
}
