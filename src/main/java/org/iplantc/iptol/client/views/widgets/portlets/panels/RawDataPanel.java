package org.iplantc.iptol.client.views.widgets.portlets.panels;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.dialogs.IPlantDialog;
import org.iplantc.iptol.client.dialogs.panels.RawDataSaveAsDialogPanel;
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.services.ViewServices;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RawDataPanel extends ProvenanceContentPanel 
{
	///////////////////////////////////////
	private final int TOOLBAR_HEIGHT = 24;
	
	///////////////////////////////////////
	//protected variables
	protected String idWorkspace;
	protected FileIdentifier file;
	protected String data;	
	protected TextArea areaData;
	
	///////////////////////////////////////
	//constructor
	public RawDataPanel(String idWorkspace,FileIdentifier file,String data)
	{
		super();		
	
		this.idWorkspace = idWorkspace;
		this.file = file;
		this.data = data;
		
		areaData = buildTextArea(true);
	}
	
	///////////////////////////////////////
	//protected methods
	protected void doSave()
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
						// TODO: post save message						
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
	protected void promptSaveAs()
	{
		IPlantDialog dlg = new IPlantDialog(displayStrings.saveAs(),320,new RawDataSaveAsDialogPanel(idWorkspace,file,areaData.getValue()));
		dlg.show();
	}
	
	///////////////////////////////////////
	protected ToolBar buildToolbar()
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
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
				
		if(data != null)
		{	
			areaData.setValue(data);
			areaData.setWidth("100%");
			areaData.setHeight(280);
			
			VerticalPanel panelOuter = new VerticalPanel();
			panelOuter.setWidth(getWidth());
			panelOuter.add(buildToolbar());
			panelOuter.add(areaData);
			
			add(panelOuter);
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
	@Override
	public String getTabHeader() 
	{
		return displayStrings.raw();
	}	
}
