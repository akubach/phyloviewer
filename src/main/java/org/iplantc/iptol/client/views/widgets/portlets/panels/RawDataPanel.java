package org.iplantc.iptol.client.views.widgets.portlets.panels;

import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class RawDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected String data;
	protected TextArea areaData;
	
	///////////////////////////////////////
	//constructor
	public RawDataPanel(HandlerManager eventbus,String data)
	{
		super(eventbus);		
	
		this.data = data;
		areaData = buildTextArea(true);		
	}
	
	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
							
		if(data != null)
		{									
			areaData.setValue(data);
				
			add(areaData,centerData);			
		}
	}	
	
	///////////////////////////////////////
	@Override
	protected void afterRender() 
	{
		super.afterRender();
		areaData.el().setElementAttribute("spellcheck","false");
	}

	///////////////////////////////////////
	@Override
	public String getTabHeader() 
	{
		return displayStrings.raw();
	}
}
