package org.iplantc.iptol.client.views.widgets.portlets.panels;

import com.google.gwt.event.shared.HandlerManager;

public class TraitDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	String tabHeader = new String();
	
	///////////////////////////////////////
	//constructor
	protected TraitDataPanel(HandlerManager eventbus,String data) 
	{
		super(eventbus);
		
	}

	///////////////////////////////////////
	//public methods
	@Override
	public String getTabHeader() 
	{
		return tabHeader;
	}
}
