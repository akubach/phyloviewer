package org.iplantc.iptol.client.views.widgets.portlets;

import org.iplantc.iptol.client.models.FileIdentifier;
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
	
	///////////////////////////////////////
	public void updateFileIdentifier(FileIdentifier file)
	{
		for(TabItem item : getItems())
		{
			ProvenancePortletTab tab = (ProvenancePortletTab)item;
			tab.setFileIdentifier(file);
		}
	}	
		
	///////////////////////////////////////
	public boolean isDirty()
	{
		boolean ret = false;  //assume clean
		
		for(TabItem item : getItems())
		{
			ProvenancePortletTab tab = (ProvenancePortletTab)item;
			
			if(tab.isDirty())
			{
				ret = true;  
				break;				
			}
		}
		
		return ret;
	}	
}
