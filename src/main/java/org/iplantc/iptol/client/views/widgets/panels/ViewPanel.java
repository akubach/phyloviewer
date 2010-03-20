package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.views.widgets.portlets.FileEditorPortlet;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class ViewPanel extends VerticalPanel 
{
	///////////////////////////////////////
	//private variables
	private Portal portal;
	private HandlerManager eventbus;
	private ArrayList<String> rawIds = new ArrayList<String>();
	private ArrayList<String> filenames = new ArrayList<String>();
	
	private int destColumn = 0;
	private final int NUM_COLUMNS = 2;
	
	///////////////////////////////////////
	//constructor
	public ViewPanel(HandlerManager eventbus)
	{	  
		this.eventbus = eventbus;
		setLayout(new FitLayout());		
		initPortal();
	}
		
	///////////////////////////////////////
	//private methods
	private void initPortal()
	{
		portal = new Portal(NUM_COLUMNS);
		
		portal.setWidth(800);
		portal.setBorders(false);  
		portal.setStyleAttribute("backgroundColor","white"); 
		portal.setColumnWidth(0,.5);  
		portal.setColumnWidth(1,.5);	
	}
	
	//////////////////////////////////////////
	private void addStrings(List<String> src,List<String> dest)
	{
		if(src != null && dest != null)
		{
			for(String id : src)
			{
				dest.add(id);
			}
		}
	}
	
	//////////////////////////////////////////
	private void configPanel(final ContentPanel panel) 
	{  
		panel.setCollapsible(false);  
		
		panel.getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() 
		{  
			@Override  
			public void componentSelected(IconButtonEvent ce) 
			{  
				panel.removeFromParent();  
			}		   
		}));  
	}
	
	//////////////////////////////////////////
	private void updateDestColumn()
	{
		destColumn++;
		
		if(destColumn >= NUM_COLUMNS)
		{
			destColumn = 0;
		}
	}
	
	//////////////////////////////////////////
	private void addPortlet(String header,String id)
	{		
		FileEditorPortlet portlet = new FileEditorPortlet(eventbus,header,id);
		
		configPanel(portlet); 
		portal.add(portlet,destColumn);
		updateDestColumn();
	}
	
	//////////////////////////////////////////
	private void addNextView()
	{
		if(rawIds.size() > 0)
		{
			String id = rawIds.get(0);
			String filename = filenames.get(0);
			
			rawIds.remove(0);
			filenames.remove(0);
						
			addPortlet(filename,id);			
		}
	}
		
	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		add(portal);
	}
	
	//////////////////////////////////////////
	//public methods
	public void showData(GetDataEvent event)
	{
		if(event.getType() == GetDataEvent.DataType.RAW)
		{
			addStrings(event.getIds(),rawIds);
			addStrings(event.getFilenames(),filenames);
			
			addNextView();
		}
	}
}
