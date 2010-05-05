package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.models.FileIdentifier;

import com.google.gwt.user.client.Element;

public class TraitDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected String idTrait;
	protected TraitEditorPanel panel;
		
	///////////////////////////////////////
	//constructor
	public TraitDataPanel(FileIdentifier file,String idTrait,String json) 
	{
		super(file);
		this.idTrait = idTrait;
		panel = new TraitEditorPanel(idTrait,file.getFileId(),json);
	}

	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
				
		if(panel != null)
		{		
			add(panel,centerData);			
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
		return panel.isDirty();
	}
	
	///////////////////////////////////////
	@Override	
	public int getTabIndex()
	{
		return 1;
	}
}
