package org.iplantc.iptol.client.views.widgets.portlets;

import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.models.RawData;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class RawDataPortlet extends Portlet 
{
	private HandlerManager eventbus;
	private String id;
	private RawData data;
	private TextArea areaData;
	
	public RawDataPortlet(HandlerManager eventbus,String header,String id,RawData data)
	{
		this.eventbus = eventbus;
		this.id = id;
		this.data = data;
		
		areaData = new TextArea();
		areaData.setStyleName("iptolcaptionlabel");		
		areaData.setHideLabel(true);	
		
		registerEvents();
		
		setHeight(410);
		setHeading(header);
	}

	///////////////////////////////////////
	//private methods
	private void registerEvents()
	{
		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event) 
			{
				if(id.equals(event.getId()))
				{
					setHeading(event.getName());
				}
			}
		});	
	}
	
	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
			  
		setLayout(new BorderLayout());
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0,0,0,0));  
		
		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,36);  
		southData.setSplit(true);  
		southData.setCollapsible(true);
		southData.setFloatable(false);
	    southData.setMargins(new Margins(0,0,0,0));
		southData.setMinSize(36);
		
		if(data != null)
		{									
			//set our body
			String out = data.getData();
			
			if(out != null)
			{			
				areaData.setValue(out);
				
				add(areaData,centerData);
			}
						
			//set our provenance			
			out = data.getProvenance();
			
			if(out != null)
			{		
				TextArea text = new TextArea();
					
				text.setStyleName("iptolcaptionlabel");
				
				text.setAutoValidate(false);
				text.setHideLabel(true);				
				text.setReadOnly(true);
				text.setValue(out);
					
				add(text,southData);
			}
		}
	}	
	
	@Override
	protected void afterRender() 
	{
	    super.afterRender();
	    areaData.el().setElementAttribute("spellcheck","false");
	}
}
