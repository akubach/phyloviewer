package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.iptol.client.events.GetDataEvent;
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
	private void addIds(List<String> src,List<String> dest)
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
	private void addPortlet(String json)
	{
		//TODO: re-implement me!!!
		/*
		RawDataPortlet portlet = new RawDataPortlet(data);
		
		configPanel(portlet); 
		portal.add(portlet,destColumn);
		updateDestColumn(); */
	}
	
	//////////////////////////////////////////
	private void addNextView()
	{
		if(rawIds.size() > 0)
		{
			String id = rawIds.get(0);
			
			rawIds.remove(0);
			//TODO: call service to get raw data
			/*
			RPCFacade.getRawData(id, new AsyncCallback<RawData>() 
			{				
				@Override
				public void onSuccess(RawData result) 
				{
					addPortlet(result);
					addNextView();				
				}
				
				@Override
				public void onFailure(Throwable caught) 
				{
					// TODO: handle failure					
				}
			});*/
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
			addIds(event.getIds(),rawIds);
			addNextView();
		}
	}
}
