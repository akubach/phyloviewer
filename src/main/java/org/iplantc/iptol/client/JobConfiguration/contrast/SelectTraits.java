package org.iplantc.iptol.client.JobConfiguration.contrast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.Card;
import org.iplantc.iptol.client.JobConfiguration.DataSelectedEvent;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEvent;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEvent.MessageType;
import org.iplantc.iptol.client.services.TraitServices;
import org.iplantc.iptol.client.services.TreeServices;

import com.extjs.gxt.ui.client.Style.SelectionMode;
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
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides grid widget with filter for users to select from available traits.
 * Provides an options to view raw data with a context menu.
 * 
 * @author sriram
 * 
 */
public class SelectTraits extends Card {
	private Grid<Trait> grid;
	private ArrayList<ColumnConfig> config;
	private ListStore<Trait> store;
	private ColumnModel columnModel;

	private HandlerManager eventbus;

	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);

	public SelectTraits(int step, HandlerManager eventbus) {
		config = new ArrayList<ColumnConfig>();
		store = new ListStore<Trait>();
		this.eventbus = eventbus;
		this.step = step;
	}

	@Override
	public VerticalPanel assembleView() {
		//CheckBoxSelectionModel<Trait> sm = new CheckBoxSelectionModel<Trait>();
		//config.add(sm.getColumn());

		ColumnConfig filename = new ColumnConfig("filename", displayStrings
				.fileName(), 100);
		ColumnConfig upload = new ColumnConfig("uploaded", displayStrings
				.uploadedDateTime(), 150);
		config.add(filename);
		config.add(upload);
		columnModel = new ColumnModel(config);
		grid = new Grid<Trait>(store, columnModel);
		grid.setWidth(400);
		grid.setHeight(275);
		grid.setAutoExpandColumn("uploaded");
		GridSelectionModel< Trait> gsm = new GridSelectionModel<Trait>();
		gsm.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(gsm);
	//	grid.setSelectionModel(sm);
		grid.setBorders(true);
	//	grid.addPlugin(sm);
		grid.getView().setEmptyText(displayStrings.noFiles());
	//	grid.setContextMenu(buildContextMenu());
	//	mockData();
		getTraits();
		StoreFilterField<Trait> filter = new StoreFilterField<Trait>() {

			@Override
			protected boolean doSelect(Store<Trait> store, Trait parent,
					Trait record, String property, String filter) {
				String filename = record.get("filename");

				if (filename == null) {
					return false;
				}

				filename = filename.toLowerCase();

				if (filename.startsWith(filter.toLowerCase())) {
					return true;
				}

				return false;
			}

		};

		grid.addListener(Events.RowClick, new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			public void handleEvent(BaseEvent be) {
				if (grid.getSelectionModel().getSelectedItems().size() > 1) {
					MessageNotificationEvent event = new MessageNotificationEvent(
							displayStrings.traitAggregation(),
							MessageNotificationEvent.MessageType.ALERT);
					eventbus.fireEvent(event);
				}
				// isReadyForNext();
			}
		});

		grid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (grid.getSelectionModel().getSelectedItems().size() > 1) {
							MessageNotificationEvent event = new MessageNotificationEvent(
									displayStrings.traitAggregation(),
									MessageNotificationEvent.MessageType.ALERT);
							eventbus.fireEvent(event);
						}
						isReadyForNext();
					}

				});

		filter.bind(store);
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("x-small-editor");
		panel.setSpacing(5);

		panel.add(new Html("<span class='iptolcaptionlabel'>"
				+ displayStrings.filterSearchString() + "</span>"));
		panel.add(filter);
		panel.add(grid);
		return panel;
	}

	public void isReadyForNext() {
		DataSelectedEvent event = null;
		if (grid.getSelectionModel().getSelectedItems().size() > 0) {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("traits", grid.getSelectionModel().getSelectedItems());
			event = new DataSelectedEvent(step, true, param);
		} else {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("traits", null);
			event = new DataSelectedEvent(step, false, param);
		}
		eventbus.fireEvent(event);
	}

	public List<Trait> getSelectedData() {
		return grid.getSelectionModel().getSelectedItems();
	}

//	private Menu buildContextMenu() {
//		Menu contextMenu = new Menu();
//		MenuItem view = new MenuItem();
//		view.setText(displayStrings.viewRawData());
//		view.addSelectionListener(new SelectionListener<MenuEvent>() {
//			@Override
//			public void componentSelected(MenuEvent ce) {
//				ContentPanel data = new ContentPanel();
//				data.add(new Html(mockRawData()));
//				data.setHeaderVisible(false);
//				Dialog d = new Dialog();
//				d.setHeading(displayStrings.rawData());
//				d.add(data);
//				d.setHideOnButtonClick(true);
//				d.show();
//
//			}
//		});
//		contextMenu.add(view);
//		return contextMenu;
//	}

	/**
	 * A native method to eval returned json
	 * 
	 * @param json
	 * @return
	 */
	private final native JsArray<TraitInfo> asArrayofTraitData(String json) /*-{
																			return eval(json);
																			}-*/;

	
	
	private void getTraits() {
		TraitServices.getMatrices(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				if(result != null ) {
					JsArray<TraitInfo> traitInfo = asArrayofTraitData(result);
					Trait trait = null;
					for (int i = 0; i < traitInfo.length(); i++) {
						trait = new Trait(traitInfo.get(i).getId(), traitInfo.get(i)
								.getFilename(),traitInfo.get(i).getUploaded());
						store.add(trait);
					}
				} else {
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
