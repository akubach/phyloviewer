package org.iplantc.iptol.client.views.widgets.portlets;

import org.iplantc.iptol.client.events.FileEditorPortletClosedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.services.ViewServices;
import org.iplantc.iptol.client.views.widgets.portlets.panels.RawDataPanel;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorPortlet extends Portlet 
{
	///////////////////////////////////////
	//private variables
	private HandlerManager eventbus;
	private String idWorkspace;
	private String provenance;
	private FileIdentifier file;
	ProvenancePortletTabPanel panel = new ProvenancePortletTabPanel();
	
	///////////////////////////////////////
	//constructor
	public FileEditorPortlet(HandlerManager eventbus,String idWorkspace,FileIdentifier file)
	{
		this.eventbus = eventbus;
		this.idWorkspace = idWorkspace;
		this.file = file;
		
		registerEvents();
		config();
			
		setHeight(438);
		setHeading(file.getFilename());
		setBorders(false);
		this.setFrame(false);
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
				FileEditorPortletClosedEvent event = new FileEditorPortletClosedEvent(file.getFileId());
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
				if(file.getFileId().equals(event.getId()))
				{
					//we need to reset our heading and update our provenance
					setHeading(event.getName());
					updateProvenance();
				}
			}
		});	
	}
	
	///////////////////////////////////////
	protected void updateProvenance()
	{
		ViewServices.getFileProvenance(file.getFileId(),new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//do nothing if there is no provenance data
			}

			@Override
			public void onSuccess(String result) 
			{
				updateProvenance(ProvenanceFormatter.format(result));				
			}			
		});
	}

	///////////////////////////////////////
	protected void updateProvenance(String provenance)
	{
		this.provenance = provenance;
		panel.updateProvenance(provenance);
	}
	
	///////////////////////////////////////
	protected void retrieveProvenance()
	{
		ViewServices.getFileProvenance(file.getFileId(),new AsyncCallback<String>()
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
	protected void getRawData()
	{
		ViewServices.getRawData(file.getFileId(),new AsyncCallback<String>()
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
				
				panel.addTab(panelRaw,provenance);			
			}				
		});
	}

	///////////////////////////////////////
	protected void getTraitData()
	{
		ViewServices.getTraitDataIds(idWorkspace,file.getFileId(),new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//we do nothing if we have no raw data
			}

			@Override
			public void onSuccess(String result) 
			{				
				//TODO: build and add tab
			}				
		});
	}
	
	///////////////////////////////////////
	protected void constructPanel()
	{
		getRawData();
		//getTraitData();
	}

	///////////////////////////////////////
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		add(panel);
	}
	
	///////////////////////////////////////
	public String getFileId()
	{
		return file.getFileId();
	}
	
	///////////////////////////////////////
	public String getParentId()
	{
		return file.getParentId();
	}
}
