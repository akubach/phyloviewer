package org.iplantc.iptol.client.views.widgets.portlets;

import java.util.ArrayList;
import java.util.List;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.contrast.TraitInfo;
import org.iplantc.iptol.client.events.FileEditorPortletDirtyEvent;
import org.iplantc.iptol.client.events.FileEditorPortletDirtyEventHandler;
import org.iplantc.iptol.client.events.FileEditorPortletClosedEvent;
import org.iplantc.iptol.client.events.FileEditorPortletSavedEvent;
import org.iplantc.iptol.client.events.FileEditorPortletSavedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.iptol.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.iptol.client.models.FileIdentifier;
import org.iplantc.iptol.client.models.FileInfo;
import org.iplantc.iptol.client.services.TreeServices;
import org.iplantc.iptol.client.services.ViewServices;
import org.iplantc.iptol.client.views.widgets.portlets.panels.RawDataPanel;
import org.iplantc.iptol.client.views.widgets.portlets.panels.TraitDataPanel;
import org.iplantc.iptol.client.views.widgets.portlets.panels.TreePanel;
import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorPortlet extends Portlet 
{
	///////////////////////////////////////
	//private variables
	private String idWorkspace;
	private String provenance;
	private FileIdentifier file;
	private Label lblStatus;
	private Status status; 
	private int numLoadingTabs;
	private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();
	private ProvenancePortletTabPanel panel = new ProvenancePortletTabPanel();
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	///////////////////////////////////////
	//constructor
	public FileEditorPortlet(String idWorkspace,FileIdentifier file)
	{
		this.idWorkspace = idWorkspace;
		this.file = file;
		
		registerEventHandlers();
		config();
			
		setHeight(438);		
		setBorders(false);
		setFrame(false);
		
		init();		
	}

	///////////////////////////////////////
	//private methods
	private final native JsArray<TraitInfo> asArrayofTraitData(String json) /*-{
																			return eval(json);
																			}-*/;
	
	///////////////////////////////////////
	//private methods
	private void initStatus()
	{
		//add our 'Downloading' label
		lblStatus = new Label(displayStrings.loading());
		getHeader().addTool(lblStatus);
		lblStatus.hide();
		
		//add our status
		status = new Status();
		getHeader().addTool(status);
		status.hide();		
	}

	///////////////////////////////////////
	private void showStatus()
	{
		lblStatus.show();
		status.show();
		status.setBusy("");
	}
	
	///////////////////////////////////////
	private void updateStatus(int numTabs)
	{
		numLoadingTabs += numTabs;
		
		//are we done loading?
		if(numLoadingTabs == 0)
		{
			lblStatus.hide();
			status.clearStatus("");
		}
	}
	
	///////////////////////////////////////
	private void config()
	{
		setCollapsible(false);  
		
		initStatus();		
	
		//add our close button
		getHeader().addTool(new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() 
		{  
			@Override  
			public void componentSelected(IconButtonEvent ce) 
			{  
				if(panel.isDirty())
				{					
					MessageBox.confirm(displayStrings.warning(),displayStrings.editPortletCloseWithoutSaveWarning(),new Listener<MessageBoxEvent>() 
					{  
						public void handleEvent(MessageBoxEvent ce) 
						{  
							//which button did the user click?
							Button btn = ce.getButtonClicked();  
							
							if(btn.getItemId().equals("yes"))
							{
								doClose();
							}	
						}  
					});
				}
				else
				{
					doClose();
				}
			}		   
		}));  	
	}

	///////////////////////////////////////
	private void init()
	{
		setHeading(file.getFilename());
		panel.removeAll();
		
		//get our provenance
		updateProvenance();
		
		//add necessary tabs		
		constructPanel();	
	}
	
	///////////////////////////////////////
	private void doClose()
	{
		cleanup();
		EventBus eventbus = EventBus.getInstance();
		FileEditorPortletClosedEvent event = new FileEditorPortletClosedEvent(file.getFileId());
		eventbus.fireEvent(event);		
	}
		
	///////////////////////////////////////
	private void registerEventHandlers()
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
		
		//file save as completed - we will need to update
		handlers.add(eventbus.addHandler(FileSaveAsEvent.TYPE,new FileSaveAsEventHandler()
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
					init();
				}
			}
		}));	
				
		//handle portlet contents changed - we may need to update our header
		handlers.add(eventbus.addHandler(FileEditorPortletDirtyEvent.TYPE,new FileEditorPortletDirtyEventHandler()
		{
			@Override
			public void onDirty(FileEditorPortletDirtyEvent event) 
			{				
				if(event.getFileId().equals(file.getFileId()))
				{
					setHeading("*" + file.getFilename());					
				}
			}			
		}));
		
		//handle portlet saved - we may need to refresh all our tabs
		handlers.add(eventbus.addHandler(FileEditorPortletSavedEvent.TYPE,new FileEditorPortletSavedEventHandler()
		{
			@Override
			public void onSaved(FileEditorPortletSavedEvent event) 
			{				
				if(event.getFileId().equals(file.getFileId()))
				{
					init();										
				}
			}			
		}));		
	}
		
	///////////////////////////////////////
	private void updateProvenance()
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
	private void updateProvenance(String provenance)
	{
		this.provenance = provenance;
		panel.updateProvenance(provenance);
	}
		
	///////////////////////////////////////
	private void getRawData()
	{
		//retrieve raw data from the server
		ViewServices.getRawData(file.getFileId(),new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//we do need to update that we are no longer trying to load this tab
				updateStatus(-1);
			}

			@Override
			public void onSuccess(String result) 
			{	
				//add a raw data tab
				RawDataPanel panelRaw = new RawDataPanel(idWorkspace,file,result);		
				
				panel.addTab(panelRaw,provenance);
				updateStatus(-1);
			}				
		});
	}
	
	///////////////////////////////////////
	private List<String> parseTraitIds(String json)
	{
		List<String> ret = new ArrayList<String>();
		
		JsArray<TraitInfo> traits = asArrayofTraitData(json);
		
		for (int i = 0; i < traits.length(); i++) 
		{
			//we are only interested in the id
			ret.add(traits.get(i).getId());
		}		
		
		return ret;
	}
	
	///////////////////////////////////////
	private void getTraits(String json)
	{
		if(json != null)
		{
			//pull ids from the json
			List<String> ids = parseTraitIds(json);
			
			//make sure we provide loading feedback for all traits
			numLoadingTabs += ids.size();
			
			for(final String id : ids)
			{
				//retrieve trait data from the server
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
						//add a trait data tab
						TraitDataPanel panelTrait = new TraitDataPanel(file,id,result);		
						
						panel.addTab(panelTrait,provenance);
						updateStatus(-1);
					}					
				});
			}
		}		
	}
	
	///////////////////////////////////////
	private void getTraitData()
	{
		//retrieve the trait data ids for our file
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
					//now that we've succeeded with getting the ids... we need to get each set of traits
					getTraits(result);
				}
			}				
		});
	}
	
	///////////////////////////////////////
	@SuppressWarnings("unchecked")
	private List<String> parseTreeIds(String json)
	{
		List<String> ret = null;
		
		if(json != null)
		{
			json = "{\"data\":" + json + "}";
		
			ModelType type = new ModelType();
			type.setRoot("data");
			type.addField("filename");
			type.addField("id");
			type.addField("treeName");
			type.addField("uploaded");
		
			JsonReader<ModelData> reader = new JsonReader<ModelData>(type);
		
			ArrayList<ModelData> model = (ArrayList<ModelData>)reader.read(null,json);
		
			if(!model.isEmpty())
			{
				ret = new ArrayList<String>();
				
				for(ModelData item : model)
				{
					ret.add(item.get("id").toString());
				}
			}			
		}
		
		return ret;
	}
	
	///////////////////////////////////////
	private void getTreeImage(String json)
	{
		if(json != null)
		{			
			//retrieve the url for the rendered image from the server
			TreeServices.getTreeImage(json,new AsyncCallback<String>()
			{
				@Override
				public void onFailure(Throwable arg0) 
				{
					updateStatus(-1);					
				}

				@Override
				public void onSuccess(String result) 
				{					
					//we got the url of an individual tree... lets add a tab
					TreePanel panelTree = new TreePanel(file,result);		
					
					panel.addTab(panelTree,provenance);
					updateStatus(-1);
				}					
			});				
		}
	}
	
	///////////////////////////////////////
	private void getTrees(String json)
	{
		if(json != null)
		{
			//get our ids from the data
			List<String> ids = parseTreeIds(json);
					
			if(ids != null)
			{
				//make sure we provide loading feedback for all trees
				numLoadingTabs += ids.size();
				
				for(final String id : ids)
				{
					//get the json for each individual tree
					TreeServices.getTreeData(id,new AsyncCallback<String>()
					{
						@Override
						public void onFailure(Throwable arg0) 
						{					
							//we are no longer waiting for this tab to load
							updateStatus(-1);					
						}
	
						@Override
						public void onSuccess(String result) 
						{
							//now we need to take the json and get the url for the tree image
							getTreeImage(result);							
						}					
					});
				}
			}
		}		
	}
	
	///////////////////////////////////////
	private void getTreeData()
	{
		//first... we need to get the ids of trees in this file
		TreeServices.getTreesFromFile(file.getFileId(),new AsyncCallback<String>()
		{
			@Override
			public void onFailure(Throwable arg0) 
			{
				//we do nothing if we have no raw data
			}

			@Override
			public void onSuccess(String result) 
			{		
				//now we should have all the tree info
				getTrees(result);			
			}				
		});
	}
		
	///////////////////////////////////////
	private void startLoading()
	{
		//we know we are loading at least a raw data tab
		updateStatus(1);
		showStatus();		
	}
			
	///////////////////////////////////////
	private void constructPanel()
	{
		startLoading();
		
		getRawData();
		getTraitData();
		getTreeData();
	}
	
	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		add(panel);
	}

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
