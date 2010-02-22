package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;

/**
 * @author sriram
 * A class that extends GXT grid
 */
public class DataBrowserGrid extends Grid<ModelData> 
{	
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT.create(IptolDisplayStrings.class);
	
	//possible refactor - pass GridConfig object
	public DataBrowserGrid(ListStore<ModelData> store,ColumnModel model) 
	{
		super(store,model);
	}
	
	protected void configureGrid() 
	{
		setBorders(true);
		setLoadMask(true);
		getView().setEmptyText(displayStrings.noFiles());
	}
}


