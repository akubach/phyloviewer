package org.iplantc.iptol.client.views.widgets.portlets;

import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.views.widgets.portlets.panels.ProvenanceContentPanel;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

public class ProvenancePortletTabPanel extends TabPanel 
{
	///////////////////////////////////////
	//constructor
	public ProvenancePortletTabPanel()
	{
		super();
		setMinTabWidth(55);  
		setResizeTabs(true);  
		setAnimScroll(true);  
		setTabScroll(true);  		
	}
	
	///////////////////////////////////////
	//public methods
	public void addTab(ProvenanceContentPanel panel,String provenance)
	{
		if(panel != null)
		{
			int idx = panel.getTabIndex();
			
			if(idx < 0 || idx > this.getItems().size())
			{
				add(new ProvenancePortletTab(panel,provenance));
			}
			else
			{
				insert(new ProvenancePortletTab(panel,provenance),idx);
			}
			
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
