package org.iplantc.iptol.client.views.widgets.portlets.panels;

import org.iplantc.iptol.client.models.FileIdentifier;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class TreePanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected Image imageTree;
	
	///////////////////////////////////////
	//constructor
	public TreePanel(FileIdentifier file,String urlTree) 
	{
		super(file);
	
		imageTree = new Image(urlTree);
		
	}

	///////////////////////////////////////
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(5);
		panel.setScrollMode(Scroll.AUTO);
		panel.setWidth("100%");		
		int height = areaProvenance.isVisible() ? 280 : 360; 
		panel.setHeight(height);
				
		panel.add(imageTree);
		add(panel);
	}
	
	///////////////////////////////////////
	//public methods
	@Override
	public String getTabHeader() 
	{
		return displayStrings.tree();
	}
}
