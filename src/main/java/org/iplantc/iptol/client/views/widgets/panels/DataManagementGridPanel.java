package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.events.GetDataEvent;
import org.iplantc.iptol.client.models.DiskResource;
import org.iplantc.iptol.client.models.Folder;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;

public class DataManagementGridPanel extends ContentPanel 
{
	//////////////////////////////////////////
	//private variables
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private String idWorkspace;
	private HandlerManager eventbus;
	private DataBrowserGrid grid;
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//////////////////////////////////////////
	//constructor
	public DataManagementGridPanel(String idWorkspace,String caption,HandlerManager eventbus)
	{
		setHeading(caption);
		this.idWorkspace = idWorkspace;
		this.eventbus = eventbus;	
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
	private void doExport()
	{
		MessageBox.alert("Coming soon!","Export functionality.",null);
	}
	
	//////////////////////////////////////////
	private void doRename()
	{
		MessageBox.alert("Coming soon!","Rename functionality.",null);			
	}
	
	//////////////////////////////////////////
	private void doCopy()
	{
		MessageBox.alert("Coming soon!","Copy functionality.",null);
	}
	
	//////////////////////////////////////////
	private void doViewRaw()
	{
		if(grid != null)
		{
			//build id list
			List<String> ids = new ArrayList<String>();
		
			List<DiskResource> items = grid.getSelectedItems();
		
			if(items != null)
			{
				for(DiskResource file : items)
				{
					String id = file.get("name");
					ids.add(id);
				}
		
				//fire our event
				GetDataEvent event = new GetDataEvent(GetDataEvent.DataType.RAW,ids);
				eventbus.fireEvent(event);
			}
		}
	}
		
	//////////////////////////////////////////
	private void doDelete()
	{
		MessageBox.alert("Coming soon!","Delete functionality.",null);
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
		
		addButton("Export","idExport",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doExport();				
			}			
		});
		
		addButton("Rename","idRename",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doRename();				
			}			
		});
		
		addButton("Copy","idCopy",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doCopy();
			}			
		});
		
		addButton("View Raw","idViewRaw",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doViewRaw();				
			}			
		});			
		
		addButton("Delete","idDelete",new SelectionListener<ButtonEvent>()
		{			
			@Override
			public void componentSelected(ButtonEvent ce) 
			{
				doDelete();
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
	private void updateButtons(GridSelectionModel<DiskResource> gridSelectionModel)
	{
		if(gridSelectionModel != null)
		{
			int numFiles = 0;
			int numFolders = 0;
			List<DiskResource> items = gridSelectionModel.getSelectedItems();
			
			for(DiskResource file : items)
			{
				if(file instanceof Folder)
				{
					numFolders++;
				}
				else
				{
					numFiles++;
				}
			}
					
			updateButton("idExport",numFolders == 0 && numFiles == 1);	
			updateButton("idRename",(numFolders == 0 && numFiles == 1) || (numFolders == 1 && numFiles == 0));			
			updateButton("idCopy",numFolders == 0 && numFiles == 1);
			updateButton("idViewRaw",numFolders == 0 && numFiles > 0);
			updateButton("idDelete",numFolders + numFiles > 0);
		}
	}

	///////////////////////////////////////
	//protected methods
	@Override
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent, index);
					
		grid = new DataBrowserGrid(idWorkspace,eventbus);
		final TreeGrid<DiskResource> treeGrid = grid.assembleView();
			
		treeGrid.getSelectionModel().addListener(Events.SelectionChange,new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be) 
			{	
				updateButtons(treeGrid.getSelectionModel());				
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
}
