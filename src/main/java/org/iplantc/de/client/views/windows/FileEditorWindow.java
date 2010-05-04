package org.iplantc.de.client.views.windows;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.JobConfiguration.contrast.TraitInfo;
import org.iplantc.de.client.events.FileEditorWindowDirtyEvent;
import org.iplantc.de.client.events.FileEditorWindowDirtyEventHandler;
import org.iplantc.de.client.events.FileEditorWindowSavedEvent;
import org.iplantc.de.client.events.FileEditorWindowSavedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEvent;
import org.iplantc.de.client.events.disk.mgmt.FileRenamedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEvent;
import org.iplantc.de.client.events.disk.mgmt.FileSaveAsEventHandler;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.models.JsFile;
import org.iplantc.de.client.services.TraitServices;
import org.iplantc.de.client.services.TreeServices;
import org.iplantc.de.client.services.RawDataServices;
import org.iplantc.de.client.utils.JsonUtil;
import org.iplantc.de.client.views.panels.ProvenanceWindowTabPanel;
import org.iplantc.de.client.views.panels.RawDataPanel;
import org.iplantc.de.client.views.panels.TraitDataPanel;
import org.iplantc.de.client.views.panels.TreePanel;

import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorWindow extends ProvenanceWindow 
{
	///////////////////////////////////////
	//protected variables
	protected int numLoadingTabs;
	protected ProvenanceWindowTabPanel panel = new ProvenanceWindowTabPanel();
	
	///////////////////////////////////////
	//constructor
	public FileEditorWindow(String tag,String idWorkspace,FileIdentifier file)
	{
		super(tag,idWorkspace,file);
	}
	
	///////////////////////////////////////
	//protected methods
	@Override
	protected void clearPanel() 
	{
		if(panel != null)
		{
			panel.removeAll();
		}
	}

	///////////////////////////////////////
	protected void updateStatus(int numTabs)
	{
		numLoadingTabs += numTabs;
		
		//are we done loading?
		if(numLoadingTabs == 0)
		{
			status.clearStatus("");
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
				updateStatus(-1);
			}

			@Override
			public void onSuccess(String result) 
			{	
				//add a raw data tab
				RawDataPanel panelRaw = new RawDataPanel(idWorkspace,file,result,true);		
				
				panel.addTab(panelRaw,provenance);
				updateStatus(-1);
			}				
		});
	}
	
	///////////////////////////////////////
	protected List<String> parseTraitIds(String json)
	{
		List<String> ret = new ArrayList<String>();
		
		JsArray<TraitInfo> traits = JsonUtil.asArrayOf(json);
		
		for (int i = 0, len = traits.length(); i < len; i++) 
		{
			//we are only interested in the id
			ret.add(traits.get(i).getId());
		}		
		
		return ret;
	}
	
	///////////////////////////////////////
	protected void getTraits(String json)
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
				TraitServices.getTraitData(id,new AsyncCallback<String>()
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
	protected void getTraitData()
	{
		//retrieve the trait data ids for our file
		TraitServices.getTraitDataIds(idWorkspace,file.getFileId(),new AsyncCallback<String>()
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
	protected List<String> parseTreeIds(String json)
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
	protected void getTreeImage(String json)
	{
		updateStatus(-1);
		
		if(json != null)
		{			
			/*//retrieve the url for the rendered image from the server
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
			}); */
			
			TreePanel panelTree = new TreePanel(file,json);		
			
			panel.addTab(panelTree,provenance);			
		}
	}
	
	///////////////////////////////////////
	protected void getTrees(String json)
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
	protected void getTreeData()
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
	@Override
	protected void constructPanel() 
	{
		//we know we are loading at least a raw data tab
		updateStatus(1);
		showStatus();	
		
		getRawData();
		getTraitData();
		getTreeData();		
	}

	///////////////////////////////////////
	//protected methods
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
					JsFile info = event.getFileInfo();
					
					file = new FileIdentifier(info.getName(),event.getParentId(),info.getId());		
					init();
				}
			}
		}));	
				
		//handle window contents changed - we may need to update our header
		handlers.add(eventbus.addHandler(FileEditorWindowDirtyEvent.TYPE,new FileEditorWindowDirtyEventHandler()
		{
			@Override
			public void onDirty(FileEditorWindowDirtyEvent event) 
			{				
				if(event.getFileId().equals(file.getFileId()))
				{
					setHeading("*" + file.getFilename());					
				}
			}			
		}));
		
		//handle window saved - we may need to refresh all our tabs
		handlers.add(eventbus.addHandler(FileEditorWindowSavedEvent.TYPE,new FileEditorWindowSavedEventHandler()
		{
			@Override
			public void onSaved(FileEditorWindowSavedEvent event) 
			{				
				if(event.getFileId().equals(file.getFileId()))
				{
					init();										
				}
			}			
		}));				
	}

	///////////////////////////////////////
	@Override
	protected void updatePanelProvenance(String provenance) 
	{
		this.provenance = provenance;
		panel.updateProvenance(provenance);		
	}
	
	///////////////////////////////////////
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		
		add(panel);
	}
}
