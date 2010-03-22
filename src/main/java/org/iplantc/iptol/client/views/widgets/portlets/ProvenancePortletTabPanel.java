package org.iplantc.iptol.client.views.widgets.portlets;

import org.iplantc.iptol.client.views.widgets.portlets.panels.ProvenanceContentPanel;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

public class ProvenancePortletTabPanel extends TabPanel 
{
	///////////////////////////////////////
	//public methods
	public void addTab(ProvenanceContentPanel panel,String provenance)
	{
		if(panel != null)
		{
			add(new ProvenancePortletTab(panel,provenance));
			layout();
		}
	}
	
	///////////////////////////////////////
	public void updateProvenance(String provenance)
	{
		for(TabItem item : getItems())
		{
			ProvenancePortletTab tab = (ProvenancePortletTab)item;
			tab.updateProvenance(provenance);
		}
	}
}
