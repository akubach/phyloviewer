/**
 * 
 */
package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
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
 * Provides grid widget with filter for users to select from available trees.  
 * Provides an options to view raw data with a context menu.
 * 
 * @author sriram
 * 
 */
public class SelectTreesGrid {

	private Grid<Tree> grid;
	private ArrayList<ColumnConfig> config;
	private ListStore<Tree> store;
	private ColumnModel columnModel;
	
	private HandlerManager eventbus;
	private int step;

	public SelectTreesGrid(int step, HandlerManager eventbus) {
		config = new ArrayList<ColumnConfig>();
		store = new ListStore<Tree>();
		this.eventbus = eventbus;
		this.step = step;
	}

	public VerticalPanel assembleView() {

		CheckBoxSelectionModel<Tree> sm = new CheckBoxSelectionModel<Tree>();
		config.add(sm.getColumn());

		ColumnConfig filename = new ColumnConfig("filename", "File Name", 100);
		ColumnConfig treename = new ColumnConfig("treename", "Tree Name", 100);
		ColumnConfig upload = new ColumnConfig("uploaded", "Uploaded", 150);
		config.add(filename);
		config.add(treename);
		config.add(upload);
		columnModel = new ColumnModel(config);
		grid = new Grid<Tree>(store, columnModel);
		grid.setWidth(400);
		grid.setHeight(275);
		grid.setAutoExpandColumn("treename");
		grid.setSelectionModel(sm);
		grid.setBorders(true);
		grid.addPlugin(sm);
		grid.setContextMenu(buildContextMenu());
		mockData();

		StoreFilterField<Tree> filter = new StoreFilterField<Tree>() {

			@Override
			protected boolean doSelect(Store<Tree> store, Tree parent,
					Tree record, String property, String filter) {
				String treename = record.get("treename");
				String filename = record.get("filename");
				if (filename == null && treename == null) {
					return false;
				}
				treename = treename.toLowerCase();
				filename = filename.toLowerCase();
				if (treename.startsWith(filter.toLowerCase())
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
						"<span class='iptolcaptionlabel'>Enter a search string(tree label or file name) such as 'vio'</span>"));
		panel.add(filter);
		panel.add(grid);
		return panel;
	}

	public List<Tree> getSelectedData() {
		return grid.getSelectionModel().getSelectedItems();
	}
	
	public void isReadyForNext() {
		DataSelectedEvent event =null;
		if(grid.getSelectionModel().getSelectedItems().size() > 0) {
			 event = new DataSelectedEvent(step,true);
		} else {
			 event = new DataSelectedEvent(step,false);
		}
		eventbus.fireEvent(event);
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
	private final native JsArray<TreeInfo> asArrayofTreeData(String json) /*-{
																			return eval(json);
																			}-*/;

	private void mockData() {
		String jsonResult = "[{\"filename\":\"Basic.nex\",\"id\":\"46\",\"treeName\":\"basic bush\",\"uploaded\":\"2010-01-26 21:08:19.951\"},"
				+ "{\"filename\":\"Sample.nex\",\"id\":\"46\",\"treeName\":\"simple2\",\"uploaded\":\"2010-01-26 21:08:19.951\"}]";
		JsArray<TreeInfo> treeInfo = asArrayofTreeData(jsonResult);
		Tree tree = null;
		for (int i = 0; i < treeInfo.length(); i++) {
			tree = new Tree(treeInfo.get(i).getFilename(), treeInfo.get(i)
					.getTreename(), treeInfo.get(i).getUploaded());
			store.add(tree);
		}

	}

	private String mockRawData() {
		String data = "<span class=text># NEXUS</BR>" + "BEGIN TAXA;</BR>"
				+ "dimensions ntax=4;</BR>" + "taxlabels A B C D;</BR>"
				+ "END</BR>" + "BEGIN CHARACTERS</BR>"
				+ "dimensions nchar=5</BR>"
				+ "format datatype=protein gap=-;</BR>"
				+ "charlabels 1 2 3 4 Five;</BR>" + "matrix</BR>"
				+ "A    MA-LL</BR>" + "B    MA-LE</BR>" + "C    MEATY</BR>"
				+ "D    ME-TE;</BR>" + "END;</BR>" +

				"BEGIN TREES;</BR>"
				+ "tree \"basic bush\" = ((A:1,B:1):1,(C:1,D:1):1);</BR>"
				+ "END</BR></span>";

		return data;
	}

}
