package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.models.RawData;
import org.iplantc.iptol.client.services.ViewServices;
import org.iplantc.iptol.client.views.widgets.portlets.RawDataPortlet;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


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
	//private classes
	private class RawDataBuilder
	{
		private ViewPanel owner;
		private RawData data = new RawData();
		
		public RawDataBuilder(ViewPanel owner)
		{
			this.owner = owner;
		}
		
		private String trimQuotes(String in)
		{
			String ret = new String();
			
			if(in != null)
			{
				ret = in;
				
				if(ret.length() > 2)
				{
					if(ret.charAt(0) == '\"')
					{
						ret = ret.substring(1);					
					}
					
					if(ret.charAt(ret.length() - 1) == '\"')
					{
						ret = ret.substring(0,ret.length() - 1);
					}
				}
			}
			
			return ret;
		}
		
		private void buildProvenance(String id)
		{
			ViewServices.getRawDataProvenance(id,new AsyncCallback<String>()
			{

				@Override
				public void onSuccess(String result) 
				{
					if(result != null)
					{							
						StringBuffer buf = new StringBuffer();

						JSONValue value = JSONParser.parse(result);
						JSONArray array = value.isArray();
						
						int size = array.size();
					    for(int i = 0; i < size; i++) 
					    {
					    	//fill our output buffer
					    	JSONValue item = array.get(i);
					    	
					    	String out  = trimQuotes(item.isString().toString());
					    	buf.append(out);
					    }
					   
						data.setProvenance(buf.toString()); 
					}
					owner.buildPortlet(data);
				}
				
				@Override
				public void onFailure(Throwable arg0) 
				{
					owner.buildPortlet(data);				
				}
			});
		}
		
		public void build(final String id,final String header)
		{			
			data.setHeader(header);
			
			ViewServices.getRawData(id,new AsyncCallback<String>()
			{
				@Override
				public void onFailure(Throwable arg0) 
				{
					//we don't have any raw data - let's see if we have any provenance
					buildProvenance(id);
				}

				@Override
				public void onSuccess(String result) 
				{
					data.setData(result);					
					buildProvenance(id);
				}				
			});
		}
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
	private void addPortlet(RawData data)
	{		
		RawDataPortlet portlet = new RawDataPortlet(data);
		
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
			
			RawDataBuilder rawDataBuilder = new RawDataBuilder(this);
			rawDataBuilder.build(id,filename);			
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

	//////////////////////////////////////////
	public void buildPortlet(RawData data)
	{
		if(data != null)
		{
			addPortlet(data);
		}
		
		addNextView();	
	}	
}
