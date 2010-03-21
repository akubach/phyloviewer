package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.events.FileEditorPortletClosedEvent;
import org.iplantc.iptol.client.events.FileEditorPortletClosedEventHandler;
import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileDeletedEventHandler;
import org.iplantc.iptol.client.views.widgets.portlets.FileEditorPortlet;

import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class EditorPanel extends VerticalPanel 
{
	///////////////////////////////////////
	//private variables
	private Portal portal;
	private HandlerManager eventbus;
	private String idWorkspace;
	private List<String> rawIds = new ArrayList<String>();
	private List<String> filenames = new ArrayList<String>();
	private List<FileEditorPortlet> filePortlets = new ArrayList<FileEditorPortlet>();
	private int destColumn = 0;
	private final int NUM_COLUMNS = 2;
	
	///////////////////////////////////////
	//constructor
	public EditorPanel(String idWorkspace,HandlerManager eventbus)
	{	  
		this.eventbus = eventbus;
		this.idWorkspace = idWorkspace;
		
		setLayout(new FitLayout());		
		registerEvents();
		initPortal();		
	}
		
	///////////////////////////////////////
	//private methods
	private void registerEvents()
	{
		eventbus.addHandler(FileEditorPortletClosedEvent.TYPE,new FileEditorPortletClosedEventHandler()
		{
			@Override
			public void onClosed(FileEditorPortletClosedEvent event) 
			{
				removeFilePortlet(event.getId());			
			}
		});	
		
		eventbus.addHandler(FileDeletedEvent.TYPE,new FileDeletedEventHandler()
		{
			@Override
			public void onDeleted(FileDeletedEvent event) 
			{
				removeFilePortlet(event.getId());			
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
	private boolean hasFilePortlet(String idFile)
	{
		boolean ret = false;  //assume failure
		
		for(FileEditorPortlet portlet : filePortlets)
		{
			
			if(portlet.getFileId().equals(idFile))
			{
				ret = true;
				break;
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
	private void updateDestColumn()
	{
		destColumn++;
		
		if(destColumn >= NUM_COLUMNS)
		{
			destColumn = 0;
		}
	}
	
	//////////////////////////////////////////
	private void addFileEditorPortlet(String header,String idFile)
	{		
		//make sure we don't already have a portlet for this file
		if(!hasFilePortlet(idFile))
		{
			FileEditorPortlet portlet = new FileEditorPortlet(eventbus,header,idWorkspace,idFile);
		
			portal.add(portlet,destColumn);
			updateDestColumn();
			filePortlets.add(portlet);
		}
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
						
			addFileEditorPortlet(filename,id);			
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
