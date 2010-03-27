package org.iplantc.iptol.client.views.widgets.portlets.panels;

import org.iplantc.iptol.client.IptolDisplayStrings;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class RawDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	private final int TOOLBAR_HEIGHT = 24;
	
	///////////////////////////////////////
	//protected variables
	protected String data;
	protected TextArea areaData;
	
	///////////////////////////////////////
	//constructor
	public RawDataPanel(String data)
	{
		super();		
	
		this.data = data;
		areaData = buildTextArea(true);
	}
	
	///////////////////////////////////////
	//protected methods
	protected void doSave()
	{
		
	}
	
	///////////////////////////////////////
	protected void promptSaveAs()
	{
		
	}
	
	///////////////////////////////////////
	protected ToolBar buildToolbar()
	{
		ToolBar ret = new ToolBar();
		ret.setWidth(getWidth());
		ret.setHeight(TOOLBAR_HEIGHT);
		
		IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
		
		//add our Save button
		ret.add(new Button(displayStrings.save(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doSave();				
			}			
		}));
		
		//add our Save As button
		ret.add(new Button(displayStrings.saveAs(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				promptSaveAs();				
			}			
		}));
		
		return ret;
	}
	
	///////////////////////////////////////
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
				
		if(data != null)
		{	
			areaData.setValue(data);
			areaData.setWidth("100%");
			areaData.setHeight(280);
			
			VerticalPanel panelOuter = new VerticalPanel();
			panelOuter.setWidth(getWidth());
			panelOuter.add(buildToolbar());
			panelOuter.add(areaData);
			
			add(panelOuter);
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
