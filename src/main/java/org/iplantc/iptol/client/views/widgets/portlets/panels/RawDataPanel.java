package org.iplantc.iptol.client.views.widgets.portlets.panels;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class RawDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//protected variables
	protected String data;
	protected TextArea areaData;
	protected TextArea areaProvenance;
	protected BorderLayoutData centerData;
	protected BorderLayoutData southData;
	
	///////////////////////////////////////
	//constructor
	public RawDataPanel(HandlerManager eventbus,String data)
	{
		super(eventbus);
		
		this.eventbus = eventbus;
		this.data = data;
		
		setLayout(new FitLayout());	
		setHeight(380);
		setHeaderVisible(false);
		
		areaData = buildTextArea(true);		
		areaProvenance = buildTextArea(false);
		
		centerData = buildCenterData();
	}
	
	///////////////////////////////////////
	//protected methods
	protected TextArea buildTextArea(boolean editable)
	{
		TextArea ret = new TextArea();
		
		ret.setStyleName("iptolcaptionlabel");		
		ret.setHideLabel(true);
	
		if(!editable)
		{
			ret.setAutoValidate(false);
			ret.setHideLabel(true);				
			ret.setReadOnly(true);				
		}
		
		return ret;
	}

	///////////////////////////////////////
	protected BorderLayoutData buildSouthData()
	{
		BorderLayoutData ret = new BorderLayoutData(LayoutRegion.SOUTH,80);  
		
		ret.setSplit(true);  
		ret.setCollapsible(true);
		ret.setFloatable(false);
	    ret.setMargins(new Margins(0,0,0,0));
		ret.setMinSize(36);
		
		return ret;
	}
	
	///////////////////////////////////////
	protected BorderLayoutData buildCenterData()
	{
		BorderLayoutData ret = new BorderLayoutData(LayoutRegion.CENTER,300);  
		ret.setMargins(new Margins(0,0,0,0));
		
		return ret;
	}
	///////////////////////////////////////
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
			  
		setLayout(new BorderLayout());
					
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
	//public methods
	public void updateProvenance(String provenance)
	{
		if(provenance != null && provenance.length() > 0)
		{
			areaProvenance.setValue(provenance);
			
			if(southData == null)
			{
				southData = buildSouthData();
				add(areaProvenance,southData);
			}						
		}
		else
		{
			areaProvenance.hide();			
		}
	}
}
