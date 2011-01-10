package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasNodeSelectionHandlers extends HasHandlers
{
	public HandlerRegistration addSelectionHandler(NodeSelectionHandler handler);
}
