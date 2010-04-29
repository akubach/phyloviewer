package org.iplantc.de.client.views.widgets.portlets.panels;

import org.iplantc.de.client.models.FileIdentifier;

import com.google.gwt.user.client.Element;

public class TraitDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected String idTrait;
	protected TraitEditorGrid grid;
		
	///////////////////////////////////////
	//constructor
	public TraitDataPanel(FileIdentifier file,String idTrait,String json) 
	{
		super(file);
		this.idTrait = idTrait;
		grid = new TraitEditorGrid(idTrait,file.getFileId(),json);
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
	
	///////////////////////////////////////
	@Override
	public boolean isDirty()
	{
		return grid.isDirty();
	}
	
	///////////////////////////////////////
	@Override	
	public int getTabIndex()
	{
		return 1;
	}
}
