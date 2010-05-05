package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.views.tabs.ProvenanceTab;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

public class ProvenanceWindowTabPanel extends TabPanel 
{
	///////////////////////////////////////
	//constructor
	public ProvenanceWindowTabPanel()
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
				add(new ProvenanceTab(panel,provenance));
			}
			else
			{
				insert(new ProvenanceTab(panel,provenance),idx);
			}
			
			layout();
		}
	}
	
	///////////////////////////////////////
	public void updateProvenance(String provenance)
	{
		for(TabItem item : getItems())
		{
			ProvenanceTab tab = (ProvenanceTab)item;
			tab.updateProvenance(provenance);
		}
	}
	
	///////////////////////////////////////
	public void updateFileIdentifier(FileIdentifier file)
	{
		for(TabItem item : getItems())
		{
			ProvenanceTab tab = (ProvenanceTab)item;
			tab.setFileIdentifier(file);
		}
	}	
		
	///////////////////////////////////////
	public boolean isDirty()
	{
		boolean ret = false;  //assume clean
		
		for(TabItem item : getItems())
		{
			ProvenanceTab tab = (ProvenanceTab)item;
			
			if(tab.isDirty())
			{
				ret = true;  
				break;				
			}
		}
		
		return ret;
	}	
}
