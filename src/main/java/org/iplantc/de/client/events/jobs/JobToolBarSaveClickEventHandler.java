package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author sriram An event handler for JobToolBarCancelClickEvent
 */
public interface JobToolBarSaveClickEventHandler extends EventHandler
{
	public void onSave(JobToolBarSaveClickEvent saveEvent);
}
