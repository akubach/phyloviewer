package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
/**
 * Provides grid widget with filter for users to select from available traits.  
 * Provides an options to view raw data with a context menu.
 * @author sriram
 *
 */
public class SelectTraitGrid {
	private Grid<Trait> grid;
	private ArrayList<ColumnConfig> config;
	private ListStore<Trait> store;
	private ColumnModel columnModel;
	
	private HandlerManager eventbus;
	private int step;
	public SelectTraitGrid(int step,HandlerManager eventbus) {
		config = new ArrayList<ColumnConfig>();
		store = new ListStore<Trait>();
		this.eventbus = eventbus;
		this.step = step;
	}

	public VerticalPanel assembleView() {
		CheckBoxSelectionModel<Trait> sm = new CheckBoxSelectionModel<Trait>();
		config.add(sm.getColumn());

		ColumnConfig filename = new ColumnConfig("filename", "File Name", 100);
		ColumnConfig treename = new ColumnConfig("traitblock", "Trait Block",
				100);
		ColumnConfig upload = new ColumnConfig("uploaded", "Uploaded", 150);
		config.add(filename);
		config.add(treename);
		config.add(upload);
		columnModel = new ColumnModel(config);
		grid = new Grid<Trait>(store, columnModel);
		grid.setWidth(400);
		grid.setHeight(275);
		grid.setAutoExpandColumn("uploaded");
		grid.setSelectionModel(sm);
		grid.setBorders(true);
		grid.addPlugin(sm);
		grid.setContextMenu(buildContextMenu());
		mockData();
		StoreFilterField<Trait> filter = new StoreFilterField<Trait>() {

			@Override
			protected boolean doSelect(Store<Trait> store, Trait parent,
					Trait record, String property, String filter) {
				String traitblock = record.get("traitblock");
				String filename = record.get("filename");

				if (traitblock == null && filename == null) {
					return false;
				}

				traitblock = traitblock.toLowerCase();
				filename = filename.toLowerCase();

				if (traitblock.startsWith(filter.toLowerCase())
						|| filename.startsWith(filter.toLowerCase())) {
					return true;
				}

				return false;
			}

		};
		
		grid.addListener(Events.RowClick, new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			public void handleEvent(BaseEvent be) {
				isReadyForNext();
			}
		});
		
		grid.getSelectionModel().addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				isReadyForNext();
			}
			
		});
		
		filter.bind(store);
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("x-small-editor");
		panel.setSpacing(5);

		panel
				.add(new Html(
						"<span class='iptolcaptionlabel'>Enter a search string(trait label or file name) such as 'vio'</span>"));
		panel.add(filter);
		panel.add(grid);
		return panel;
	}

	public void isReadyForNext() {
		DataSelectedEvent event = null;
		if(grid.getSelectionModel().getSelectedItems().size() > 0) {
			 event = new DataSelectedEvent(step,true);
		} else {
			 event = new DataSelectedEvent(step,false);
		}
		eventbus.fireEvent(event);
	}
	
	public List<Trait> getSelectedData() {
		return grid.getSelectionModel().getSelectedItems();
	}

	private Menu buildContextMenu() {
		Menu contextMenu = new Menu();
		MenuItem view = new MenuItem();
		view.setText("View raw data ");
		view.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				ContentPanel data = new ContentPanel();
				data.add(new Html(mockRawData()));
				data.setHeaderVisible(false);
				Dialog d = new Dialog();
				d.setHeading("Raw Data");
				d.add(data);
				d.setHideOnButtonClick(true);
				d.show();

			}
		});
		contextMenu.add(view);
		return contextMenu;
	}

	/**
	 * A native method to eval returned json
	 * 
	 * @param json
	 * @return
	 */
	private final native JsArray<TraitInfo> asArrayofTreeData(String json) /*-{
																			return eval(json);
																			}-*/;

	private void mockData() {
		String jsonResult = "[{\"filename\":\"Basic.nex\",\"id\":\"46\",\"traitblock\":\"bush\",\"uploaded\":\"2010-01-26 21:08:19.951\"},"
				+ "{\"filename\":\"sample.nex\",\"id\":\"46\",\"traitblock\":\"simple\",\"uploaded\":\"2010-01-26 21:08:19.951\"}]";
		JsArray<TraitInfo> traitInfo = asArrayofTreeData(jsonResult);
		Trait trait = null;
		for (int i = 0; i < traitInfo.length(); i++) {
			trait = new Trait(traitInfo.get(i).getFilename(), traitInfo.get(i)
					.getTraitblockname(), traitInfo.get(i).getUploaded());
			store.add(trait);
		}

	}

	private String mockRawData() {
		String data = "Species  flowersize   color</br>"
				+ "A         4.3         	1</br>"
				+ "B         2.1          0</br>"
				+ "C         4.1          0</br>" + "D         3.9			1</br>";

		return data;
	}
}
