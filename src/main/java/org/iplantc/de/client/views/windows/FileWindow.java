package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceRenamedEventHandler;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.services.RawDataServices;
import org.iplantc.de.client.utils.ProvenanceFormatter;
import org.iplantc.de.client.views.panels.RawDataPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileWindow extends ProvenanceWindow 
{	
	///////////////////////////////////////
	//protected variables
	protected RawDataPanel panel;
	
	///////////////////////////////////////
	//constructor
	public FileWindow(String tag,String idWorkspace,FileIdentifier file)
	{
		super(tag,idWorkspace,file);	
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
		RawDataServices.getRawData(file.getFileId(),new AsyncCallback<String>()
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
		RawDataServices.getFileProvenance(file.getFileId(),new AsyncCallback<String>()
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
		handlers.add(eventbus.addHandler(DiskResourceRenamedEvent.TYPE,new DiskResourceRenamedEventHandler()
		{
			@Override
			public void onFolderRenamed(DiskResourceRenamedEvent event) 
			{
				// ignore folder rename
			}
			
			@Override
			public void onFileRenamed(DiskResourceRenamedEvent event) 
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
