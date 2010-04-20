package org.iplantc.iptol.client.views.widgets;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.iptol.client.models.WorkspacePerspective;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.BufferView;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Element;

public class NavigationMenu extends ContentPanel
{	
	//////////////////////////////////////////
	//private variables
	private String idWorkspace;
	private BorderLayoutData northData;
	private BorderLayoutData centerData;
	
	//////////////////////////////////////////
	//constructor
	public NavigationMenu(String idWorkspace)
	{		
		this.idWorkspace = idWorkspace;
		setBorders(false);		
		setStyleName("iptol_menulabel");
		
		northData = buildNorthData();
		centerData = buildCenterData();		
	}	
	
	//////////////////////////////////////////
	//private methods
	private void doPerspectiveChange(int idx)
	{
		//TODO: implement me!!!
	}
	
	///////////////////////////////////////
	private BorderLayoutData buildNorthData()
	{
		BorderLayoutData ret = new BorderLayoutData(LayoutRegion.NORTH,86);  
		
		ret.setFloatable(false);
	    ret.setMargins(new Margins(0,0,0,0));
		
		return ret;
	}
	
	///////////////////////////////////////
	private BorderLayoutData buildCenterData()
	{
		BorderLayoutData ret = new BorderLayoutData(LayoutRegion.CENTER);  
		ret.setMargins(new Margins(0,0,0,0));
		
		return ret;
	}

	//////////////////////////////////////////
	private void renderMenu(List<WorkspacePerspective> perpsectives)
	{
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		   
		ColumnConfig column = new ColumnConfig();  
		column.setId("name");  
		column.setWidth(200); 
		column.setMenuDisabled(true);
		configs.add(column);  
		
		ListStore<WorkspacePerspective> store = new ListStore<WorkspacePerspective>();  
		store.add(perpsectives);  
	
		ColumnModel cm = new ColumnModel(configs);
		
		Grid<WorkspacePerspective> grid = new Grid<WorkspacePerspective>(store,cm);  
		grid.setBorders(false);  
		grid.setHideHeaders(true);		
		grid.setAutoExpandColumn("name");
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		Listener<GridEvent<ModelData>> gridListener = new Listener<GridEvent<ModelData>>() 
		{
			public void handleEvent(GridEvent<ModelData> be) 
			{
				EventType type = be.getType();

				if(type == Events.CellClick) 
				{
					doPerspectiveChange(be.getRowIndex());	
				}
			}
		};
		
		grid.addListener(Events.CellClick,gridListener);

		BufferView view = new BufferView();
		view.setRowHeight(42); 
		view.setForceFit(true);			
		view.setSortingEnabled(false);
		
		view.setViewConfig(new GridViewConfig()
		{
			@Override
			public String getRowStyle(ModelData model,int rowIndex,ListStore<ModelData> ds)
			{
				return "iptolmenu";
			}
		});	
		
		//highlightCurrentPerspective();
		
		grid.setView(view);
		
		add(grid,northData);
	}
	
	//////////////////////////////////////////
	private void parsePerspectiveNames(String json)
	{
		//TODO: parse json returned from rpc call
		
		List<WorkspacePerspective> names = new ArrayList<WorkspacePerspective>();
		
		names.add(new WorkspacePerspective("1","Trait Evolution"));		
		names.add(new WorkspacePerspective("2","Ultra HT Sequencing"));	

		renderMenu(names);
	}
	
	//////////////////////////////////////////
	private void getPerspectives()
	{
		//TODO: make rpc call
		
		parsePerspectiveNames(new String());
	}
	
	//////////////////////////////////////////
	private void renderDataBrowserTree()
	{		
		DataBrowserTree tree = new DataBrowserTree(idWorkspace);		
		tree.assembleView();
		
		add(tree,centerData);		
	}
	
	//////////////////////////////////////////
	//protected methods
	@Override  
	protected void onRender(Element parent,int index) 
	{  
		super.onRender(parent,index);
		setLayout(new BorderLayout());
		
		//TODO: uncomment the following line to add perspective view
		//getPerspectives();
		renderDataBrowserTree();
	}	
}