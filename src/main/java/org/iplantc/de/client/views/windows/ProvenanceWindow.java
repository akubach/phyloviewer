package org.iplantc.de.client.views.windows;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.FileEditorWindowClosedEvent;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.services.RawDataServices;
import org.iplantc.de.client.utils.ProvenanceFormatter;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ProvenanceWindow extends IPlantWindow 
{
	///////////////////////////////////////
	//protected variables
	protected String idWorkspace;
	protected String provenance;
	protected FileIdentifier file;
	protected Status status; 
	protected List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();
	protected DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	///////////////////////////////////////
	//constructor
	protected ProvenanceWindow(String tag,String idWorkspace,FileIdentifier file)
	{
		super(tag);
		this.idWorkspace = idWorkspace;
		this.file = file;
		
		registerEventHandlers();
		
		config();
					
		init();		
	}
	
	///////////////////////////////////////
	//protected methods
	protected void config()
	{
		setCollapsible(false);  
		
		initStatus();		
	
		setMinimizable(true);
	    setMaximizable(true);
	    setHeight(438);	
	    setLayout(new FitLayout());
	    setConstrain(true);	
	    initListeners();		  	
	}
	
	///////////////////////////////////////
	protected void init()
	{
		setHeading(file.getFilename());
		
		clearPanel();
		
		//get our provenance
		updateProvenance();
		
		//add necessary tabs		
		constructPanel();	
	}
	
	///////////////////////////////////////
	protected void initListeners()
	{
		addWindowListener(new WindowListener()
		{
            @Override
            public void windowHide(WindowEvent we) 
            {
            	//make sure we are really hiding (and not just minimizing) 
            	if(getData("minimize") == null)
            	{
            		doClose();					
            	}
            }
        }); 
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
	protected void updateProvenance(String provenance)
	{
		this.provenance = provenance;
		
		updatePanelProvenance(provenance);	
	}
	
	///////////////////////////////////////
	protected void initStatus()
	{
		//add our status
		status = new Status();
		getHeader().addTool(status);
		status.hide();		
	}

	///////////////////////////////////////
	protected void showStatus()
	{		
		status.show();
		status.setBusy("");
	}
	
	///////////////////////////////////////
	protected void hideStatus()
	{
		status.clearStatus("");
	}
	
	///////////////////////////////////////
	protected void doClose()
	{
		EventBus eventbus = EventBus.getInstance();
		FileEditorWindowClosedEvent event = new FileEditorWindowClosedEvent(file.getFileId());
		eventbus.fireEvent(event);		
	}
	
	///////////////////////////////////////
	protected abstract void registerEventHandlers();
	
	///////////////////////////////////////
	protected abstract void clearPanel();
	
	///////////////////////////////////////
	protected abstract void constructPanel();
	
	///////////////////////////////////////
	protected abstract void updatePanelProvenance(String provenance);
	
	///////////////////////////////////////
	//public methods
	public String getFileId()
	{
		return file.getFileId();
	}
	
	///////////////////////////////////////
	public String getParentId()
	{
		return file.getParentId();
	}
	
	///////////////////////////////////////
	//public methods
	@Override
	public void cleanup() 
	{
		EventBus eventbus = EventBus.getInstance();
		
		//unregister
		for(HandlerRegistration reg : handlers)
		{
			eventbus.removeHandler(reg);
		}
		
		//clear our list
		handlers.clear();	
	}
}
