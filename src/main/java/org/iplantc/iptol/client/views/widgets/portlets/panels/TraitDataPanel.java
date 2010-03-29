package org.iplantc.iptol.client.views.widgets.portlets.panels;

import org.iplantc.iptol.client.models.FileIdentifier;
import com.google.gwt.user.client.Element;

public class TraitDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected TraitEditorGrid grid;
		
	///////////////////////////////////////
	//constructor
	public TraitDataPanel(FileIdentifier file,String idTrait,String json) 
	{
		super(file);
		grid = new TraitEditorGrid(idTrait,json);
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
