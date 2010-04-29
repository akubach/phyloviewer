package org.iplantc.de.client.windows;

import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.services.ViewServices;
import org.iplantc.de.client.views.widgets.portlets.ProvenanceFormatter;
import org.iplantc.de.client.views.widgets.portlets.panels.RawDataPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileWindow extends ProvenanceWindow 
{	
	///////////////////////////////////////
	//protected variables
	protected RawDataPanel panel;
	
	///////////////////////////////////////
	//constructor
	public FileWindow(String idWorkspace,FileIdentifier file)
	{
		super(idWorkspace,file);	
	}

	///////////////////////////////////////
	@Override
	protected void clearPanel() 
	{
		if(panel != null)
		{
			panel.removeAll();
		}	
	}

	///////////////////////////////////////
	protected void buildPanel(String data)
	{
		if(data != null)
		{
			panel = new RawDataPanel(idWorkspace,file,data,false);		
			
			updateProvenance(provenance);				
		}
	}
	
	///////////////////////////////////////
	protected void getRawData()
	{
		//retrieve raw data from the server
		ViewServices.getRawData(file.getFileId(),new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//we do need to update that we are no longer trying to load this tab
				hideStatus();
			}

			@Override
			public void onSuccess(String result) 
			{
				hideStatus();
				buildPanel(result);
				add(panel);				
				layout();
			}				
		});
	}
	
	///////////////////////////////////////
	@Override
	protected void constructPanel() 
	{
		//we know we are loading raw data
		showStatus();
		
		getRawData();				
	}
	
	///////////////////////////////////////
	protected void updateProvenance()
	{
		//retrieve provenance from the server
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
	@Override
	protected void registerEventHandlers() 
	{
		EventBus eventbus = EventBus.getInstance();
		
		//file renamed - we may need to update our header
		handlers.add(eventbus.addHandler(FileRenamedEvent.TYPE,new FileRenamedEventHandler()
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
		}));		
	}

	///////////////////////////////////////
	@Override
	protected void updatePanelProvenance(String provenance) 
	{
		if(panel != null)
		{
			panel.updateProvenance(provenance);
		}		
	}	
}
