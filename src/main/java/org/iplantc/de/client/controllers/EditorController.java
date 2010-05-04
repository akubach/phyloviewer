package org.iplantc.de.client.controllers;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.FileEditorWindowClosedEvent;
import org.iplantc.de.client.events.FileEditorWindowClosedEventHandler;
import org.iplantc.de.client.events.GetDataEvent;
import org.iplantc.de.client.events.GetDataEventHandler;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceDeletedEventHandler;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.views.WindowManager;
import org.iplantc.de.client.windows.ProvenanceWindow;
import org.iplantc.de.client.windows.FileEditorWindow;

import com.google.gwt.core.client.GWT;

public class EditorController 
{
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private WindowManager mgrWindow;
	private List<FileIdentifier> files = new ArrayList<FileIdentifier>();
	private List<ProvenanceWindow> fileWindows = new ArrayList<ProvenanceWindow>();
	private static DEClientConstants constants = (DEClientConstants)GWT.create(DEClientConstants.class);
	
	//////////////////////////////////////////
	//constructor
	public EditorController(String idWorkspace,WindowManager mgrWindow)
	{
		this.idWorkspace = idWorkspace;
		this.mgrWindow = mgrWindow;
		
		initEventListeners();		
	}
	
	//////////////////////////////////////////
	//private methods
	private void initEventListeners()
	{
		EventBus eventbus = EventBus.getInstance();
		
		//handle window close
		eventbus.addHandler(FileEditorWindowClosedEvent.TYPE,new FileEditorWindowClosedEventHandler()
		{
			@Override
			public void onClosed(FileEditorWindowClosedEvent event) 
			{
				removeFileWindow(event.getId());			
			}
		});	
			
		//handle deletions
		eventbus.addHandler(DiskResourceDeletedEvent.TYPE,new DiskResourceDeletedEventHandler()
		{
			@Override
			public void onDeleted(DiskResourceDeletedEvent event) 
			{
				for(String id : event.getFiles())
				{
					removeFileWindow(id);
				}
				
				for(String id : event.getFolders())
				{
					removeFileWindowAfterParentDelete(id);
				}
			}
		});	
		
		//handle get data
		eventbus.addHandler(GetDataEvent.TYPE,new GetDataEventHandler()
		{        	
			@Override
			public void onGet(GetDataEvent event) 
			{
				for(FileIdentifier file : event.getFiles())
				{
					files.add(file);
				}		
			
				addNextView();
			}
		});
	}
	
	//////////////////////////////////////////
	private void removeFileWindow(ProvenanceWindow in)
	{
		if(in != null)
		{
			//since minimized is really hidden... we need to check or else the hide event is never picked up
			//the result is that the user can delete a 'minimized' provenance window and it will not be removed
			//from the toolbar.  The solution is to show the window first and then hide.  If this causes visual 
			//anomalies on slower machines, we will likely have to fire an event to have the perspective remove 
			//the item from the toolbar.
			if(in.getData("minimize") != null)
			{
				in.show();
			}
			
			//make sure our window cleans up after itself
			in.hide();
			
			//remove from our list for file portlets
			fileWindows.remove(in);			
			
			mgrWindow.remove(in);
		}
	}

	//////////////////////////////////////////
	private boolean hasFileWindow(FileIdentifier file)
	{
		boolean ret = false;  //assume failure
	
		if(file != null)
		{
			String idFile = file.getFileId();
			
			for(ProvenanceWindow window : fileWindows)
			{			
				if(window.getFileId().equals(idFile))
				{
					ret = true;
					break;
				}
			}
		}	
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void removeFileWindow(String idFile)
	{
		if(idFile != null)
		{
			List<ProvenanceWindow> remove = new ArrayList<ProvenanceWindow>();
		
			//fill our delete list
			for(ProvenanceWindow window : fileWindows)
			{
				if(window.getFileId().equals(idFile))
				{
					remove.add(window);
				}
			}
			
			//perform remove
			for(ProvenanceWindow window : remove)
			{				
				removeFileWindow(window);				
			}		
		}
	}
	
	//////////////////////////////////////////
	private void removeFileWindowAfterParentDelete(String idParent)
	{
		if(idParent != null)
		{
			List<ProvenanceWindow> remove = new ArrayList<ProvenanceWindow>();
		
			//fill our delete list
			for(ProvenanceWindow window : fileWindows)
			{
				if(window.getParentId().equals(idParent))
				{
					remove.add(window);
				}
			}
			
			//perform remove
			for(ProvenanceWindow window : remove)
			{				
				removeFileWindow(window);				
			}		
		}
	}
		
	//////////////////////////////////////////
	private void addFileWindow(FileIdentifier file)
	{		
		//make sure we don't already have a window for this file
		if(!hasFileWindow(file))
		{
			String tag = constants.fileEditorTag() + file.getFileId();
			FileEditorWindow window = new FileEditorWindow(tag,idWorkspace,file);
					
			fileWindows.add(window);
			mgrWindow.add(window);
			
			window.show();
		}		
	}
	
	//////////////////////////////////////////
	private void addNextView()
	{
		while(files.size() > 0)
		{
			FileIdentifier file = files.get(0);			
			files.remove(0);
						
			addFileWindow(file);			
		}
	}
}
