package org.iplantc.iptol.client;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.core.client.GWT;
/**
 * @author sriram
 * A class that extends GXT grid
 */
public class DataBrowserGrid extends Grid<ModelData> {
	
	private ListStore<ModelData> store;
	private ColumnModel model;
	
	IptolConstants constants = (IptolConstants) GWT
	.create(IptolConstants.class);
	
	//possible refactor - pass GridConfig object
	public DataBrowserGrid(ListStore<ModelData> store,ColumnModel model) {
		super(store,model);
		this.model = model;
		this.store = store;
	}
	
	protected void configureGrid() {
		this.setBorders(true);
		this.setLoadMask(true);
		this.getView().setEmptyText(constants.noFiles());
	}

}


