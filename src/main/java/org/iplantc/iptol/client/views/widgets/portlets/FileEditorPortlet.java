package org.iplantc.iptol.client.views.widgets.portlets;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.JobConfiguration.contrast.TraitInfo;
import org.iplantc.iptol.client.events.FileEditorPortletClosedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.services.ViewServices;
import org.iplantc.iptol.client.views.widgets.portlets.panels.RawDataPanel;
import org.iplantc.iptol.client.views.widgets.portlets.panels.TraitDataPanel;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorPortlet extends Portlet 
{
	///////////////////////////////////////
	//private variables
	private String idWorkspace;
	private String provenance;
	private FileIdentifier file;
	ProvenancePortletTabPanel panel = new ProvenancePortletTabPanel();
	
	///////////////////////////////////////
	//constructor
	public FileEditorPortlet(String idWorkspace,FileIdentifier file)
	{
		this.idWorkspace = idWorkspace;
		this.file = file;
		
		registerEventHandlers();
		config();
			
		setHeight(438);
		setHeading(file.getFilename());
		setBorders(false);
		setFrame(false);
		
		updateProvenance();
		constructPanel();		
	}

	///////////////////////////////////////
	//private methods
	private final native JsArray<TraitInfo> asArrayofTraitData(String json) /*-{
																			return eval(json);
																			}-*/;
	
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
				EventBus eventbus = EventBus.getInstance();
				FileEditorPortletClosedEvent event = new FileEditorPortletClosedEvent(file.getFileId());
				eventbus.fireEvent(event);
			}		   
		}));  	
	}
	
	///////////////////////////////////////
	protected void registerEventHandlers()
	{
		EventBus eventbus = EventBus.getInstance();
		
		//file renamed
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
		
		//file save as completed
		eventbus.addHandler(FileSaveAsEvent.TYPE,new FileSaveAsEventHandler()
		{
			@Override
			public void onSaved(FileSaveAsEvent event) 
			{
				//did we get saved as something else?
				if(event.getParentId().equals(file.getParentId()) && event.getOriginalFileId().equals(file.getFileId()))
				{				
					//reset our file
					FileInfo info = event.getFileInfo();
					
					file = new FileIdentifier(info.getName(),event.getParentId(),info.getId());		
					setHeading(info.getName());
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
				RawDataPanel panelRaw = new RawDataPanel(idWorkspace,file,result);		
				
				panel.addTab(panelRaw,provenance);			
			}				
		});
	}
	
	///////////////////////////////////////
	protected List<String> getTraitIds(String json)
	{
		List<String> ret = new ArrayList<String>();
		
		JsArray<TraitInfo> traits = asArrayofTraitData(json);
		
		for (int i = 0; i < traits.length(); i++) 
		{
			ret.add(traits.get(i).getId());
		}		
		
		return ret;
	}
	
	///////////////////////////////////////
	protected void getTraits(String json)
	{
		if(json != null)
		{
			List<String> ids = getTraitIds(json);
			
			for(final String id : ids)
			{
				ViewServices.getTraitData(id,new AsyncCallback<String>()
				{
					@Override
					public void onFailure(Throwable arg0) 
					{
						//TODO: handle failure					
					}

					@Override
					public void onSuccess(String result) 
					{
						TraitDataPanel panelTrait = new TraitDataPanel(id, result);		
						
						panel.addTab(panelTrait,provenance);								
					}					
				});
			}
		}		
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
				if(result != null)
				{
					getTraits(result);
				}
			}				
		});
	}
	
	///////////////////////////////////////
	protected void constructPanel()
	{
		getRawData();
		getTraitData();
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
