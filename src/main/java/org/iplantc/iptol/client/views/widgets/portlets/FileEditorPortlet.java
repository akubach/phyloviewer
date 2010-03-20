package org.iplantc.iptol.client.views.widgets.portlets;

import org.iplantc.iptol.client.events.FileEditorPortletClosedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.services.ViewServices;
import org.iplantc.iptol.client.views.widgets.portlets.panels.ProvenanceContentPanel;
import org.iplantc.iptol.client.views.widgets.portlets.panels.RawDataPanel;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorPortlet extends Portlet 
{
	///////////////////////////////////////
	//private variables
	private HandlerManager eventbus;
	private String id;	
	private String provenance;
	private ProvenanceContentPanel panel;
	
	///////////////////////////////////////
	//constructor
	public FileEditorPortlet(HandlerManager eventbus,String header,String id)
	{
		this.eventbus = eventbus;
		this.id = id;
		
		registerEvents();
		config();
		
		setHeight(410);
		setHeading(header);
		
		retrieveProvenance();
	}

	///////////////////////////////////////
	//protected methods
	protected void config()
	{
		setCollapsible(false);  
		
		getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() 
		{  
			@Override  
			public void componentSelected(IconButtonEvent ce) 
			{  
				FileEditorPortletClosedEvent event = new FileEditorPortletClosedEvent(id);
				eventbus.fireEvent(event);
			}		   
		}));  	
	}
	
	///////////////////////////////////////
	protected void registerEvents()
	{
		eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
		{
			@Override
			public void onRenamed(FileRenamedEvent event) 
			{
				//has our file been renamed?
				if(id.equals(event.getId()))
				{
					//we need to reset our heading and update our provenance
					setHeading(event.getName());
					retrieveProvenance();
				}
			}
		});	
	}

	///////////////////////////////////////
	protected void updatePanelsProvenance()
	{
		if(panel != null)
		{
			panel.updateProvenance(provenance);
		}
	}
	
	///////////////////////////////////////
	protected void updateProvenance(String provenance)
	{
		this.provenance = provenance;
		updatePanelsProvenance();
	}

	///////////////////////////////////////
	protected void retrieveProvenance()
	{
		ViewServices.getFileProvenance(id,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//we still want to build our panels with or without provenance
				constructPanel();	
			}

			@Override
			public void onSuccess(String result) 
			{
				updateProvenance(ProvenanceFormatter.format(result));				
				constructPanel();
			}			
		});
	}
	
	///////////////////////////////////////
	protected void constructPanel()
	{
		getRawData();
	}

	///////////////////////////////////////
	protected void getRawData()
	{
		ViewServices.getRawData(id,new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//we do nothing if we have no raw data
			}

			@Override
			public void onSuccess(String result) 
			{
				RawDataPanel panelRaw = new RawDataPanel(eventbus,result);		
				panelRaw.updateProvenance(provenance);
				
				panel = panelRaw;
				
				add(panel);
				layout();
			}				
		});
	}
	
	///////////////////////////////////////
	public String getId()
	{
		return id;
	}
}
