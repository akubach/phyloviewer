package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface JobSavedEventHandler extends EventHandler {

	void onJobSaved(JobSavedEvent jse);

}
