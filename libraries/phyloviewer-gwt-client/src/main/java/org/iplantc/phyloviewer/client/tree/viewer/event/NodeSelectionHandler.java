package org.iplantc.phyloviewer.client.tree.viewer.event;

import com.google.gwt.event.shared.EventHandler;

public interface NodeSelectionHandler extends EventHandler{ 
	public void onNodeSelection(NodeSelectionEvent event);
}
