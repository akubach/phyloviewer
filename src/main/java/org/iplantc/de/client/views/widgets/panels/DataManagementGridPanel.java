package org.iplantc.de.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.JsonConverter;
import org.iplantc.de.client.events.GetDataEvent;
import org.iplantc.de.client.models.DiskResource;
import org.iplantc.de.client.models.File;
import org.iplantc.de.client.models.FileIdentifier;
import org.iplantc.de.client.models.Folder;
import org.iplantc.de.client.services.DiskResourceDeleteCallback;
import org.iplantc.de.client.services.FolderServices;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class DataManagementGridPanel extends ContentPanel 
{
	//////////////////////////////////////////
	//private variables
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private String idWorkspace;
	private DataBrowserGrid grid;
	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT.create(DEDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public DataManagementGridPanel(String idWorkspace,String caption)
	{
		setHeading(caption);
		
		this.idWorkspace = idWorkspace;			
	}
	
	//////////////////////////////////////////
	//private methods
	private Button getButton(String id)
	{
		Button ret = null;  //assume failure
		
		for(int i = 0;i < buttons.size(); i++)
		{
			Button btn = buttons.get(i);
			
			if(btn.getId() == id)
			{
				ret = btn;
				break;
			}		
		}
		
		return ret;
	}
	
	//////////////////////////////////////////
	private void doRename()
	{	
		if(grid != null)
		{
			grid.promptForRename();
		}					
	}
	
	//////////////////////////////////////////
	private void doViewRaw()
	{
		if(grid != null)
		{
			//build id list
			List<FileIdentifier> files = new ArrayList<FileIdentifier>();
			List<DiskResource> items = grid.getSelectedItems();
		
			if(items != null)
			{
				for(DiskResource resource : items)
				{
					if(resource instanceof File)
					{
						File file = (File)resource;
						Folder parent = (Folder)file.getParent();
					
						files.add(new FileIdentifier(file.getName(),parent.getId(),file.getId()));					
					}
				}
		
				//fire our event
				EventBus eventbus = EventBus.getInstance();
				GetDataEvent event = new GetDataEvent(files);
				eventbus.fireEvent(event);
			}
		}
	}

	//////////////////////////////////////////	
	private boolean isNonEmptyFolderSelected()
	{
		boolean ret = false;
		
		if(grid != null)
		{
			List<DiskResource> items = grid.getSelectedItems();
			
			if(items != null)
			{
				for(DiskResource item : items)
				{ 
					//is there a folder selected?
					if(item instanceof Folder)
					{
						Folder folder = (Folder)item;
						if(folder.getChildCount() > 0)
						{
							ret = true;
						}
						break;
					}
				}
			}
		}
		return ret;
	}
			
	//////////////////////////////////////////
	private void doDelete()
	{
		List<String> idFolders = new ArrayList<String>();
		List<String> idFiles = new ArrayList<String>();
		
		if(grid != null)
		{
			//first we need to fill our id lists
			List<DiskResource> items = grid.getSelectedItems();
			
			for(DiskResource item : items)
			{ 
				if(item instanceof Folder)
				{
					idFolders.add(item.getId());					
				}
				else if(item instanceof File)
				{
					idFiles.add(item.getId());
				}
			}
			
			String json = JsonConverter.buildDeleteString(idFolders,idFiles);
			
			if(json != null)
			{
				FolderServices.deleteDiskResources(idWorkspace,json,new DiskResourceDeleteCallback(idFolders,idFiles));
			}
		}
	}
	
	//////////////////////////////////////////
	private void delete()
	{
		if(isNonEmptyFolderSelected())
		{
			MessageBox.confirm(displayStrings.warning(),displayStrings.folderDeleteWarning(),new Listener<MessageBoxEvent>() 
			{  
				public void handleEvent(MessageBoxEvent ce) 
				{  
					Button btn = ce.getButtonClicked();  
					
					//did the user click yes?
					if(btn.getItemId().equals("yes"))
					{
						doDelete();
					}	
				}  
			});
		}
		else
		{
			doDelete();
		}
	}
	
	//////////////////////////////////////////
	private void addButton(String caption,String id,SelectionListener<ButtonEvent> listener)
	{
		Button btn = new Button(caption,listener);
		
		btn.setId(id);
		btn.setEnabled(false);
		btn.setMinWidth(80);
		
		buttons.add(btn);
	}
	
	//////////////////////////////////////////
	private VerticalPanel buildButtonPanel()
	{
		VerticalPanel ret = new VerticalPanel();
		ret.setSpacing(5);
		
		addButton(displayStrings.edit(),"idEdit",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doViewRaw();				
			}			
		});			

		addButton(displayStrings.rename(),"idRename",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doRename();				
			}			
		});
				
		addButton(displayStrings.delete(),"idDelete",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				delete();
			}			
		});
						
		//add all buttons to our panel
		for(int i = 0;i < buttons.size();i++)
		{
			ret.add(buttons.get(i));
		}
		
		return ret;
	}
		
	///////////////////////////////////////
	private void updateButton(String idBtn,boolean enable)
	{
		Button btn = getButton(idBtn);
		
		if(btn != null)
		{			
			btn.setEnabled(enable);
		}
	}
	
	///////////////////////////////////////
	private void updateButtons()
	{
		List<DiskResource> items = grid.getSelectedItems();
		
		if(items != null)
		{
			int numFiles = 0;
			int numFolders = 0;
			
			for(DiskResource resource : items)
			{
				if(resource instanceof Folder)
				{
					numFolders++;
				}
				else
				{
					numFiles++;
				}
			}
			
			updateButton("idEdit",numFolders == 0 && numFiles > 0);
			updateButton("idRename",((numFolders == 1 && numFiles == 0) || (numFolders == 0 && numFiles == 1)));		
			updateButton("idDelete",numFolders + numFiles > 0);	
		}
	}

	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent, index);
					
		grid = new DataBrowserGrid(idWorkspace);
		final TreeGrid<DiskResource> treeGrid = grid.assembleView();
	
		treeGrid.getSelectionModel().addListener(Events.SelectionChange,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be) 
			{	
				updateButtons();				
			}			
		});
		
		setLayout(new FitLayout());
		HorizontalPanel panel = new HorizontalPanel();
				
		panel.add(treeGrid);
		panel.add(buildButtonPanel());
		
		add(panel);		
	}
	
	///////////////////////////////////////
	//public methods
	public void promptForFolderCreate()
	{
		if(grid != null)
		{
			grid.promptForFolderCreate();
		}
	}
	
	///////////////////////////////////////
	public String getUploadParentId()
	{
		return (grid == null) ? null : grid.getUploadParentId();
	}
}
