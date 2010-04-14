package org.iplantc.iptol.client.views.widgets.portlets.panels;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.RawDataSaveAsDialogPanel;
import org.iplantc.iptol.client.events.FileEditorPortletDirtyEvent;
import org.iplantc.iptol.client.events.FileEditorPortletSavedEvent;
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.services.ViewServices;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	//private variables
	private String idWorkspace;
	private String data;	
	private TextArea areaData;
	private String textOrig = new String();
	private final int TOOLBAR_HEIGHT = 24;
	
	///////////////////////////////////////
	//constructor
	public RawDataPanel(String idWorkspace,FileIdentifier file,String data)
	{
		super(file);		
	
		this.idWorkspace = idWorkspace;
		this.data = data;
		
		buildTextArea();			
	}
	
	///////////////////////////////////////
	//private methods
	private void buildTextArea()
	{
		areaData = buildTextArea(true);
		
		areaData.addListener(Events.OnKeyUp, new Listener<FieldEvent>() 
		{
		      public void handleEvent(FieldEvent be) 
		      {
		    	  String text = areaData.getValue();
		    	  if(!text.equals(textOrig))
		    	  {
		    		  textOrig = text;
		    		  
		    		  //don't fire event if we are already dirty
		    		  if(!dirty)
		    		  {		   
		    			  dirty = true;
		    			  EventBus eventbus = EventBus.getInstance();							
		    			  FileEditorPortletDirtyEvent event = new FileEditorPortletDirtyEvent(file.getFileId());
		    			  eventbus.fireEvent(event);
		    		  }
		    	  }
		      }
		});
	}
	
	///////////////////////////////////////
	private void doSave()
	{
		if(areaData != null)
		{
			String body = areaData.getValue();	
		
			if(file != null)
			{			
				ViewServices.saveRawData(file.getFileId(),file.getFilename(),body,new AsyncCallback<String>()
				{
					@Override
					public void onSuccess(String result) 
					{
						EventBus eventbus = EventBus.getInstance();							
						FileEditorPortletSavedEvent event = new FileEditorPortletSavedEvent(file.getFileId());
						eventbus.fireEvent(event);					
					}					
					
					@Override
					public void onFailure(Throwable caught) 
					{
						IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
						
						ErrorHandler.post(errorStrings.rawDataSaveFailed());						
					}					
				});
			}
		}
	}
	
	///////////////////////////////////////
	private void promptSaveAs()
	{
		IPlantDialog dlg = new IPlantDialog(displayStrings.saveAs(),320,new RawDataSaveAsDialogPanel(idWorkspace,file,areaData.getValue()));
		dlg.show();
	}
	
	///////////////////////////////////////
	private ToolBar buildToolbar()
	{
		ToolBar ret = new ToolBar();
		ret.setWidth(getWidth());
		ret.setHeight(TOOLBAR_HEIGHT);
		
		IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
		
		//add our Save button
		ret.add(new Button(displayStrings.save(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doSave();				
			}			
		}));
		
		//add our Save As button
		ret.add(new Button(displayStrings.saveAs(),new SelectionListener<ButtonEvent>() 
		{
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				promptSaveAs();				
			}			
		}));
		
		return ret;
	}
		
	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		if(data != null)
		{			
			textOrig = data;
			areaData.setValue(data);
			areaData.setWidth(getWidth());
			
			ContentPanel panel = new ContentPanel();
			panel.setHeaderVisible(false);
			panel.setLayout(new FitLayout());
			panel.setWidth(getWidth());
			panel.add(areaData);	
			panel.setTopComponent(buildToolbar());
						
			add(panel,centerData);
		}
	}	
	
	///////////////////////////////////////
	@Override
	protected void afterRender() 
	{
		super.afterRender();
		areaData.el().setElementAttribute("spellcheck","false");
	}

	///////////////////////////////////////
	//public methods
	@Override
	public String getTabHeader() 
	{
		return displayStrings.raw();
	}	
		
	///////////////////////////////////////
	public int getTabIndex()
	{
		return 0;
	}
}
