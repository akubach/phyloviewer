/**
 * 
 */
package org.iplantc.de.client.jobs.contrast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.jobs.DataSelectedEvent;
import org.iplantc.de.client.events.jobs.MessageNotificationEvent;
import org.iplantc.de.client.jobs.Card;
import org.iplantc.de.client.models.JsTree;
import org.iplantc.de.client.models.Tree;
import org.iplantc.de.client.services.TreeServices;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides grid widget with filter for users to select from available trees.
 * Provides an options to view raw data with a context menu.
 * 
 * @author sriram
 * 
 */
public class SelectTrees extends Card {

	private Grid<Tree> grid;
	private ArrayList<ColumnConfig> config;
	private ListStore<Tree> store;
	private ColumnModel columnModel;
	private String workspaceId;

	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT
			.create(DEDisplayStrings.class);

	public SelectTrees(int step, String workspaceId) {
		config = new ArrayList<ColumnConfig>();
		store = new ListStore<Tree>();
		this.step = step;
		this.workspaceId = workspaceId;
	}

	@Override
	public VerticalPanel assembleView() {
		CheckBoxSelectionModel<Tree> sm = new CheckBoxSelectionModel<Tree>();
		config.add(sm.getColumn());

		ColumnConfig filename = new ColumnConfig("filename", displayStrings
				.fileName(), 100);
		ColumnConfig treename = new ColumnConfig("treename", displayStrings
				.label(), 100);
		ColumnConfig upload = new ColumnConfig("uploaded", displayStrings
				.uploadedDateTime(), 150);
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
		grid.setTitle(displayStrings.selectedTraits());
		grid.getView().setEmptyText(displayStrings.noFiles());
		// grid.setContextMenu(buildContextMenu());
		getTrees();
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
			public void handleEvent(BaseEvent be) {
				// isReadyForNext();
			}
		});

		grid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						isReadyForNext();
					}

				});
		filter.bind(store);
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("x-small-editor");
		panel.setSpacing(5);

		panel.add(new Html("<span class='iplantc-caption-label'>"
				+ displayStrings.filterSearchString() + "</span>"));
		panel.add(filter);
		panel.add(grid);
		return panel;
	}

	public List<Tree> getSelectedData() {
		return grid.getSelectionModel().getSelectedItems();
	}

	@Override
	public void isReadyForNext() {
		DataSelectedEvent event = null;
		if (grid.getSelectionModel().getSelectedItems().size() > 0) {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("trees", grid.getSelectionModel().getSelectedItems());
			event = new DataSelectedEvent(step, true, param);
		} else {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("trees", null);
			event = new DataSelectedEvent(step, false, param);
		}
		EventBus eventbus = EventBus.getInstance();
		eventbus.fireEvent(event);
	}

	@Override
	public void reset() {
		grid.getSelectionModel().deselectAll();

	}

	// private Menu buildContextMenu() {
	// Menu contextMenu = new Menu();
	// MenuItem view = new MenuItem();
	// view.setText(displayStrings.viewRawData());
	// view.addSelectionListener(new SelectionListener<MenuEvent>() {
	// @Override
	// public void componentSelected(MenuEvent ce) {
	// ContentPanel data = new ContentPanel();
	// data.add(new Html(mockRawData()));
	// data.setHeaderVisible(false);
	// Dialog d = new Dialog();
	// d.setHeading(displayStrings.rawData());
	// d.add(data);
	// d.setHideOnButtonClick(true);
	// d.show();
	// }
	// });
	// contextMenu.add(view);
	// return contextMenu;
	// }

	/**
	 * A native method to eval returned json
	 * 
	 * @param json
	 * @return
	 */
	private final native JsArray<JsTree> asArrayofTreeData(String json) /*-{
																			return eval(json);
																			}-*/;

	private void getTrees() {
		TreeServices.getTreesInWorkspace(workspaceId,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						if (result != null) {
							JsArray<JsTree> treeInfo = asArrayofTreeData(result);
							Tree tree = null;
							for (int i = 0; i < treeInfo.length(); i++) {
								tree = new Tree(treeInfo.get(i).getId(),
										treeInfo.get(i).getFilename(), treeInfo
												.get(i).getTreename(), treeInfo
												.get(i).getUploaded());
								store.add(tree);
							}
						} else {
							EventBus eventbus = EventBus.getInstance();
							MessageNotificationEvent event = new MessageNotificationEvent(
									displayStrings.getListOfTreesError(),
									MessageNotificationEvent.MessageType.ERROR);
							eventbus.fireEvent(event);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
	}

}
