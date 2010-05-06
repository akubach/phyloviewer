package org.iplantc.de.client.jobs.contrast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.jobs.DataSelectedEvent;
import org.iplantc.de.client.events.jobs.MessageNotificationEvent;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.jobs.Card;
import org.iplantc.de.client.models.JobParams;
import org.iplantc.de.client.models.JsSpecies;
import org.iplantc.de.client.models.Species;
import org.iplantc.de.client.models.Trait;
import org.iplantc.de.client.models.Tree;
import org.iplantc.de.client.services.TraitServices;
import org.iplantc.de.client.services.TreeServices;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Provides a widget to compare tree and trait species. Users can re-order
 * species using drag and drop to match the species.
 * 
 * @author sriram
 * 
 */
public class Reconcile extends Card {

	private ContentPanel widget;
	private ContentPanel treeContentPanel;
	private ContentPanel traitContentPanel;
	private HorizontalPanel info;
	private Grid<Species> treeSpecies;
	private Grid<Species> traitSpecies;
	private ListStore<Species> treeStore;
	private ListStore<Species> traitStore;

	private JsArray<JsSpecies> treeSpeciesNames;
	private JsArray<JsSpecies> traitSpeciesNames;

	private Timer t;

	private boolean isTreeServiceComplete;
	private boolean isTraitServiceComplete;

	private ArrayList<String> treeids;
	private ArrayList<String> traitids;

	public IndepdentContrastJobView view;

	// for service calls timeout
	private static final int TIMEOUT = 5000;

	// for time between checks
	private static final int CHECK_INTERVAL = 1000;

	private Button reconciled;

	private HashMap<String, String> params;

	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT
			.create(DEDisplayStrings.class);

	public Reconcile(int step) {
		widget = new ContentPanel();
		widget.setLayout(new VBoxLayout());
		info = new HorizontalPanel();
		treeContentPanel = new ContentPanel();
		traitContentPanel = new ContentPanel();

		// content.setLayout(new HBoxLayout());
		this.step = step;
		treeStore = new ListStore<Species>();
		traitStore = new ListStore<Species>();
		treeids = new ArrayList<String>();
		traitids = new ArrayList<String>();
	}

	@Override
	public ContentPanel assembleView() {
		widget.setScrollMode(Scroll.NONE);
		widget.setHeight(375);
		ArrayList<ColumnConfig> config = new ArrayList<ColumnConfig>();
		ColumnConfig name = new ColumnConfig("name", displayStrings
				.treeDataSpecies(), 175);
		config.add(name);
		ColumnModel cm = new ColumnModel(config);

		treeSpecies = new Grid<Species>(treeStore, cm);
		treeSpecies.setAutoHeight(true);
		treeSpecies.setAutoExpandColumn("name");

		ArrayList<ColumnConfig> config1 = new ArrayList<ColumnConfig>();
		ColumnConfig name1 = new ColumnConfig("name", displayStrings
				.triatDataSpecies(), 175);
		config1.add(name1);
		ColumnModel cm1 = new ColumnModel(config1);

		traitSpecies = new Grid<Species>(traitStore, cm1);
		traitSpecies.setAutoHeight(true);
		traitSpecies.setAutoExpandColumn("name");

		treeSpecies.addListener(Events.RowClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
			}

		});

		traitSpecies.addListener(Events.RowClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {

			}

		});

		// set cell bg color to red if there is no match
		treeSpecies.getView().setViewConfig(new ReconcileGridViewConfig());

		// set cell bg color to red if there is no match
		traitSpecies.getView().setViewConfig(new ReconcileGridViewConfig());

		treeContentPanel.setHeaderVisible(false);
		treeContentPanel.setBodyBorder(true);
		treeContentPanel.add(treeSpecies);
		treeContentPanel.setWidth(180);

		traitContentPanel.setHeaderVisible(false);
		traitContentPanel.setBodyBorder(true);
		traitContentPanel.add(traitSpecies);
		traitContentPanel.setWidth(180);

		HorizontalPanel gridsPanel = new HorizontalPanel();
		gridsPanel.setLayout(new HBoxLayout());
		gridsPanel.setHeight(305);
		gridsPanel.setScrollMode(Scroll.AUTOY);
		gridsPanel.add(treeContentPanel);
		gridsPanel.add(traitContentPanel);

		info.add(new Html("<span class=text>" + displayStrings.reconileInfo()
				+ "</span>"));
		info.setStyleAttribute("padding", "3px");
		widget.add(info);
		widget.add(gridsPanel);
		widget.setHeaderVisible(false);
		widget.setBottomComponent(buildBottomComponent());
		return widget;
	}

	// provide toolbar with swap buttons
	private ToolBar buildBottomComponent() {
		ToolBar t = new ToolBar();
		t.setWidth(350);
		Button swapTreeSpecies = new Button(displayStrings.swapTree());
		swapTreeSpecies.setIcon(AbstractImagePrototype.create(Resources.ICONS.refresh()));
		t.add(swapTreeSpecies);
		swapTreeSpecies.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				doTreeSpeciesSwap();
				reconciled.el().blink(FxConfig.NONE);
			}
		});

		reconciled = new Button("Apply");
		reconciled.setStyleAttribute("padding-left", "15px");
		reconciled.setStyleAttribute("padding-right", "15px");
		reconciled.setIcon(AbstractImagePrototype.create(Resources.ICONS.apply()));
		reconciled.focus();
		t.add(reconciled);
		reconciled.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				doReconcile();

			}
		});

		t.add(new FillToolItem());

		Button swapTraitSpecies = new Button(displayStrings.swapTrait());
		swapTraitSpecies.setIcon(AbstractImagePrototype.create(Resources.ICONS.refresh()));
		t.add(swapTraitSpecies);
		swapTraitSpecies.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				doTraitSpeciesSwap();
				reconciled.el().blink(FxConfig.NONE);

			}
		});
		return t;
	}

	/**
	 * if a valid tree species does not have matching trait then its error. if
	 * there is a trait for a species which is not in tree then its a warning.
	 */

	private void doReconcile() {
		Species s;
		Species s1;

		EventBus eventbus = EventBus.getInstance();
		for (int i = 0; i < treeStore.getCount(); i++) {
			s = treeStore.getAt(i);
			if (!"-1".equals(s.get("id"))) {
				s1 = traitStore.getAt(i);

				if (s1 != null && "-1".equals(s1.get("id"))) {

					MessageNotificationEvent event = new MessageNotificationEvent(
							displayStrings.matchingTreeSpecies(),
							MessageNotificationEvent.MessageType.ERROR);
					eventbus.fireEvent(event);
					return;
				}
			} else {
				s1 = traitStore.getAt(i);
				if (s1 != null && !("-1".equals(s1.get("id")))) {
					MessageNotificationEvent event = new MessageNotificationEvent(
							displayStrings.matchingTraitSpecies(),
							MessageNotificationEvent.MessageType.ERROR);
					eventbus.fireEvent(event);
					break;
				}
			}
		}

		// if i reach hear then no error
		MessageBox.info("Reconcile Taxa", displayStrings.reconcileTaxa(), null);

		params = new HashMap<String, String>();
		for (int k = 0; k < treeStore.getCount(); k++) {
			s = treeStore.getAt(k);
			if (s != null && !("-1".equals(s.get("id")))) {
				s1 = traitStore.getAt(k);
				params.put(s.get("id").toString(), s1.get("id").toString());
			}
		}

		isReadyForNext();
	}

	/**
	 * Swap two tree species
	 */
	private void doTraitSpeciesSwap() {
		if (traitSpecies.getSelectionModel().getSelectedItems().size() != 2) {
			EventBus eventbus = EventBus.getInstance();
			MessageNotificationEvent event = new MessageNotificationEvent(
					displayStrings.swapError(),
					MessageNotificationEvent.MessageType.ERROR);
			eventbus.fireEvent(event);
			traitSpecies.getSelectionModel().deselectAll();
		} else {
			List<Species> species = traitSpecies.getSelectionModel()
					.getSelectedItems();

			// there can be only two of them
			int i = traitStore.indexOf(species.get(0));
			int j = traitStore.indexOf(species.get(1));

			ArrayList<Species> store = new ArrayList<Species>();
			Species s;

			for (int k = 0; k <= traitStore.getCount(); k++) {
				s = traitStore.getAt(k);
				if (k == i) {
					store.add(species.get(1));
				} else if (k == j) {
					store.add(species.get(0));
				} else {
					store.add(s);
				}
			}

			traitStore.removeAll();
			traitStore.add(store);
		}

		EventBus eventbus = EventBus.getInstance();
		DataSelectedEvent dataevent = new DataSelectedEvent(step, false, null);
		eventbus.fireEvent(dataevent);

	}

	/**
	 * Swap two trait species
	 */
	private void doTreeSpeciesSwap() {
		if (treeSpecies.getSelectionModel().getSelectedItems().size() != 2) {
			EventBus eventbus = EventBus.getInstance();
			MessageNotificationEvent event = new MessageNotificationEvent(
					displayStrings.swapError(),
					MessageNotificationEvent.MessageType.ERROR);
			eventbus.fireEvent(event);
			treeSpecies.getSelectionModel().deselectAll();
		} else {
			List<Species> species = treeSpecies.getSelectionModel()
					.getSelectedItems();

			// there can be only two of them
			int i = treeStore.indexOf(species.get(0));
			int j = treeStore.indexOf(species.get(1));

			ArrayList<Species> store = new ArrayList<Species>();
			Species s;

			for (int k = 0; k <= treeStore.getCount(); k++) {
				s = treeStore.getAt(k);
				if (k == i) {
					store.add(species.get(1));
				} else if (k == j) {
					store.add(species.get(0));
				} else {
					store.add(s);
				}
			}

			treeStore.removeAll();
			treeStore.add(store);
		}

		EventBus eventbus = EventBus.getInstance();
		DataSelectedEvent dataevent = new DataSelectedEvent(step, false, null);
		eventbus.fireEvent(dataevent);

	}

	@Override
	public void isReadyForNext() {
		DataSelectedEvent event = null;
		HashMap<String, Object> reconcile = new HashMap<String, Object>();
		reconcile.put("reconciliation", params);

		EventBus eventbus = EventBus.getInstance();
		event = new DataSelectedEvent(step, true, reconcile);
		eventbus.fireEvent(event);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setJobParams(JobParams params) {
		// have to get the tree and trait species for selected tree and traits
		ArrayList<Tree> trees = (ArrayList<Tree>) params.get("trees");
		ArrayList<Trait> traits = (ArrayList<Trait>) params.get("traits");
		ArrayList<String> tid = new ArrayList<String>();
		ArrayList<String> trid = new ArrayList<String>();
		if (trees != null) {
			for (Tree t : trees) {
				tid.add((String) t.get("id"));
			}
		}

		if (traits != null) {
			for (Trait t : traits) {
				trid.add((String) t.get("id"));
			}
		}

		if (treeids.containsAll(tid) && traitids.containsAll(trid)) {
			// no change in selected trees and traits. already reconciled if we
			// are inside this if
			return;
		} else {
			treeids.clear();
			treeids.addAll(tid);
			traitids.clear();
			traitids.addAll(trid);
			treeStore.removeAll();
			traitStore.removeAll();
			getSpecies(treeids, traitids);
		}
	}

	/**
	 * build tree and trait store
	 */
	private void buildStore() {

		boolean matched = false;
		String unknown = "UNK";
		int unkown_counter = 0;

		ArrayList<Species> treeSpecies = new ArrayList<Species>();
		ArrayList<Species> traitSpecies = new ArrayList<Species>();
		ArrayList<Species> unmatchedTreeSpecies = new ArrayList<Species>();

		// transform into arraylist for easy processing
		for (int i = 0; i < treeSpeciesNames.length(); i++) {
			treeSpecies.add(new Species(treeSpeciesNames.get(i).getId(),
					treeSpeciesNames.get(i).getName()));
		}

		for (int j = 0; j < traitSpeciesNames.length(); j++) {
			traitSpecies.add(new Species(traitSpeciesNames.get(j).getId(),
					traitSpeciesNames.get(j).getName()));
		}

		// sort list
		Collections.sort(treeSpecies, new SpeciesComparator());
		Collections.sort(traitSpecies, new SpeciesComparator());

		for (Species s : treeSpecies) {
			if (traitSpecies.contains(s)) {
				// Window.alert("matched...");
				treeStore.add(s);
				int index = traitSpecies.lastIndexOf(s);
				traitStore.add(traitSpecies.remove(index));
				matched = true;
			}

			if (!matched) {
				unmatchedTreeSpecies.add(s);
			} else {
				matched = false;
			}
		}

		// unmatched tree species. error condition
		for (Species s : unmatchedTreeSpecies) {
			treeStore.add(s);
			unkown_counter++;
			traitStore.add(new Species("-1", unknown + unkown_counter));
		}

		// unmatched trait species
		for (Species s : traitSpecies) {
			unkown_counter++;
			treeStore.add(new Species("-1", unknown + unkown_counter));
			traitStore.add(s);
		}

		// doReconcile();

	}

	@Override
	public void reset() {

	}

	private final native JsArray<JsSpecies> asArrayOfSpecies(String json) /*-{
																			return eval(json);
																			}-*/;

	private void getSpecies(ArrayList<String> treeids,
			ArrayList<String> traitids) {
		StringBuilder sb = new StringBuilder("{\"ids\":[");

		if (treeids != null) {
			for (String id : treeids) {
				sb.append("\"" + id + "\",");
			}
			// remove unwanted comma
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]}");

			TreeServices.getTreeSpecies(sb.toString(),
					new AsyncCallback<String>() {

						@Override
						public void onSuccess(String result) {
							if (result != null) {
								JSONObject obj = JSONParser.parse(result)
										.isObject();
								String evalString = obj.get("species")
										.toString();
								treeSpeciesNames = asArrayOfSpecies(evalString);
								isTreeServiceComplete = true;
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							isTreeServiceComplete = true;
							treeSpeciesNames = null;
						}
					});
		}

		if (traitids != null) {
			TraitServices.getSpeciesNames(traitids.get(0),
					new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							traitSpeciesNames = null;
							isTraitServiceComplete = true;
						}

						@Override
						public void onSuccess(String result) {
							if (result != null) {
								JSONObject obj = JSONParser.parse(result)
										.isObject();
								String evalString = obj.get("species")
										.toString();
								traitSpeciesNames = asArrayOfSpecies(evalString);
								isTraitServiceComplete = true;
							}

						}
					});
		}

		// check if services have completed.
		t = new Timer() {
			int elapsed = 0;

			@Override
			public void run()
			{
				if(isTraitServiceComplete && isTreeServiceComplete)
				{
					t.cancel();
					buildStore();
				}
				else if(elapsed == TIMEOUT)
				{
					t.cancel();
				}
				else
				{
					elapsed = elapsed + CHECK_INTERVAL;
				}
			}
		};

		t.scheduleRepeating(CHECK_INTERVAL);
	}

	/**
	 * set cell bg color to red if there is no match
	 * 
	 * @author sriram
	 * 
	 */
	class ReconcileGridViewConfig extends GridViewConfig {
		@Override
		public String getRowStyle(ModelData model, int rowIndex,
				ListStore<ModelData> ls) {
			if (model.get("id").equals("-1")) {
				return "iplantc-reconcile";
			} else {
				return ".x-grid3-cell-inner";
			}
		}
	}

}
