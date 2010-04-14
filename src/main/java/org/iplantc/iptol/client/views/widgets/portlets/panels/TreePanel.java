package org.iplantc.iptol.client.views.widgets.portlets.panels;

import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.services.TreeServices;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class TreePanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected String urlTree;
	protected VerticalPanel panel = new VerticalPanel();
	
	///////////////////////////////////////
	//constructor
	public TreePanel(FileIdentifier file,String urlTree) 
	{
		super(file);
		
		this.urlTree = urlTree;
	}

	///////////////////////////////////////
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		panel.setSpacing(5);
		panel.setScrollMode(Scroll.AUTO);
		panel.setWidth("100%");		
			
		panel.add(new Image(urlTree));
		add(panel,centerData);	
	}
	
	///////////////////////////////////////
	protected void getTreeImage(String json)
	{
		if(json != null)
		{			
			TreeServices.getTreeImage(json,new AsyncCallback<String>()
			{
				@Override
				public void onFailure(Throwable arg0) 
				{
					//TODO: handle failure					
				}

				@Override
				public void onSuccess(String result) 
				{
					urlTree = result;	
					layout();
				}					
			});				
		}
	}
	
	///////////////////////////////////////
	//public methods
	@Override
	public String getTabHeader() 
	{
		return displayStrings.tree();
	}	
	
	///////////////////////////////////////
	@Override
	public void updateProvenance(String provenance)
	{
		super.updateProvenance(provenance);
				
		int height = (provenance != null && provenance.trim().length() > 0) ? 280 : 360; 
		panel.setHeight(height);
		
		layout();
	}
	
	///////////////////////////////////////
	@Override
	public int getTabIndex()
	{
		return -1;
	}
}
