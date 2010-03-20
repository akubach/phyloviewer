package org.iplantc.iptol.client.views.widgets.portlets.panels;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.shared.HandlerManager;

public abstract class ProvenanceContentPanel extends ContentPanel 
{
	protected HandlerManager eventbus;
	
	protected ProvenanceContentPanel(HandlerManager eventbus)
	{
		super();
		this.eventbus = eventbus;
	}
	
	public abstract void updateProvenance(String provenance);
}
