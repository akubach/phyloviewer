package org.iplantc.iptol.client.views.widgets.portlets.panels;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class TraitDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected TraitEditorGrid grid;
		
	///////////////////////////////////////
	//constructor
	public TraitDataPanel(HandlerManager eventbus,String json) 
	{
		super(eventbus);
		
		grid = new TraitEditorGrid(json);
	}

	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
				
		if(grid != null)
		{		
			add(grid.assembleView(),centerData);			
		}
	}	
	
	///////////////////////////////////////
	//public methods
	@Override
	public String getTabHeader() 
	{
		return displayStrings.trait();
	}
}
