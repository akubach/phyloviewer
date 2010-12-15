package org.iplantc.phyloviewer.client.tree.viewer.event;

import com.google.gwt.event.shared.EventHandler;

public interface DocumentChangeHandler extends EventHandler
{
	public void onDocumentChange(DocumentChangeEvent event);
}
