package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface DataPayloadEventHandler extends EventHandler {

	void onFire(DataPayloadEvent event);
}
