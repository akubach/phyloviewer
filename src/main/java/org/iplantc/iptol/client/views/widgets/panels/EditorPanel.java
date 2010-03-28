package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.events.FileEditorPortletClosedEvent;
import org.iplantc.iptol.client.events.FileEditorPortletClosedEventHandler;
import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.events.disk.mgmt.DiskResourceDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.DiskResourceDeletedEventHandler;
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.views.widgets.portlets.FileEditorPortlet;

import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class EditorPanel extends VerticalPanel 
{
	///////////////////////////////////////
	//private variables
	private Portal portal;
	private String idWorkspace;
	private List<FileIdentifier> files = new ArrayList<FileIdentifier>();
	private List<FileEditorPortlet> filePortlets = new ArrayList<FileEditorPortlet>();
	private int destColumn = 0;
	private final int NUM_COLUMNS = 2;
	
	///////////////////////////////////////
	//constructor
	public EditorPanel(String idWorkspace)
	{	  
		this.idWorkspace = idWorkspace;
		
		setLayout(new FitLayout());		
		registerEvents();
		initPortal();		
	}
		
	///////////////////////////////////////
	//private methods
	private void registerEvents()
	{
		EventBus eventbus = EventBus.getInstance();
		
		//handle portlet close
		eventbus.addHandler(FileEditorPortletClosedEvent.TYPE,new FileEditorPortletClosedEventHandler()
		{
			@Override
			public void onClosed(FileEditorPortletClosedEvent event) 
			{
				removeFilePortlet(event.getId());			
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
					removeFilePortlet(id);
				}
				
				for(String id : event.getFolders())
				{
					removeFilePortletAfterParentDelete(id);
				}
			}
		});
	}

	///////////////////////////////////////
	private void initPortal()
	{
		portal = new Portal(NUM_COLUMNS);
		
		portal.setWidth(820);
		portal.setBorders(false);  
		portal.setStyleAttribute("backgroundColor","white"); 
		portal.setColumnWidth(0,.5);  
		portal.setColumnWidth(1,.5);	
	}
	
	//////////////////////////////////////////
	private void removePortletFromPortal(Portlet in)
	{
		if(in != null)
		{
			int idxColumn = portal.getPortletColumn(in);
			
			if(idxColumn > -1)
			{
				portal.remove(in,idxColumn);
			}
		}
	}
	
	//////////////////////////////////////////
	private void removeFilePortlet(FileEditorPortlet in)
	{
		if(in != null)
		{
			//remove from our list for file portlets
			filePortlets.remove(in);
			
			//remove portlet from the portal
			removePortletFromPortal(in);		
		}
	}

	//////////////////////////////////////////
	private boolean hasFilePortlet(FileIdentifier file)
	{
		boolean ret = false;  //assume failure
	
		if(file != null)
		{
			String idFile = file.getFileId();
			
			for(FileEditorPortlet portlet : filePortlets)
			{			
				if(portlet.getFileId().equals(idFile))
				{
					ret = true;
					break;
				}
			}
		}	
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void removeFilePortlet(String idFile)
	{
		if(idFile != null)
		{
			List<FileEditorPortlet> remove = new ArrayList<FileEditorPortlet>();
		
			//fill our delete list
			for(FileEditorPortlet portlet : filePortlets)
			{
				if(portlet.getFileId().equals(idFile))
				{
					remove.add(portlet);
				}
			}
			
			//perform remove
			for(FileEditorPortlet portlet : remove)
			{				
				removeFilePortlet(portlet);				
			}		
		}
	}
	
	//////////////////////////////////////////
	private void removeFilePortletAfterParentDelete(String idParent)
	{
		if(idParent != null)
		{
			List<FileEditorPortlet> remove = new ArrayList<FileEditorPortlet>();
		
			//fill our delete list
			for(FileEditorPortlet portlet : filePortlets)
			{
				if(portlet.getParentId().equals(idParent))
				{
					remove.add(portlet);
				}
			}
			
			//perform remove
			for(FileEditorPortlet portlet : remove)
			{				
				removeFilePortlet(portlet);				
			}		
		}
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
	private void addFileEditorPortlet(FileIdentifier file)
	{		
		//make sure we don't already have a portlet for this file
		if(!hasFilePortlet(file))
		{
			FileEditorPortlet portlet = new FileEditorPortlet(idWorkspace,file);
		
			portal.add(portlet,destColumn);
			updateDestColumn();
			filePortlets.add(portlet);			
		}		
	}
	
	//////////////////////////////////////////
	private void addNextView()
	{
		while(files.size() > 0)
		{
			FileIdentifier file = files.get(0);			
			files.remove(0);
						
			addFileEditorPortlet(file);			
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
		if(event != null)
		{
			for(FileIdentifier file : event.getFiles())
			{
				files.add(file);
			}		
		
			addNextView();
		}
	}
}
