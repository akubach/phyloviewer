package org.iplantc.iptol.client.JobConfiguration.contrast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.Card;
import org.iplantc.iptol.client.JobConfiguration.DataSelectedEvent;
import org.iplantc.iptol.client.JobConfiguration.JobParams;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEvent;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEvent.MessageType;
import org.iplantc.iptol.client.services.TraitServices;
import org.iplantc.iptol.client.services.TreeServices;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.js.JsonConverter;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides a widget to compare tree and trait species. Users can re-order
 * species using drag and drop to match the species.
 * 
 * @author sriram
 * 
 */
public class Reconcile extends Card {

	private VerticalPanel widget;
	private HorizontalPanel content;
	private HorizontalPanel info;
	private Grid<Species> treeSpecies;
	private Grid<Species> traitSpecies;
	private HandlerManager eventbus;
	private ListStore<Species> treeStore;
	private ListStore<Species> traitStore;
	
	private JsArray<SpeciesInfo> treeSpeciesNames;
	private JsArray<SpeciesInfo> traitSpeciesNames;
	
	private Timer t ;

	private boolean isTreeServiceComplete;
	private boolean isTraitServiceComplete;
	
	private ArrayList<String> treeids;
	private ArrayList<String> traitids;
	
	
	public IndepdentContrastJobView view;
	
	//for service calls timeout
	private static final int TIMEOUT = 5000; 
	
	//for time between checks
	private static final int CHECK_INTERVAL = 1000;

	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);

	public Reconcile(int step, HandlerManager eventbus) {
		widget = new VerticalPanel();
		widget.setLayout(new VBoxLayout());
		info = new HorizontalPanel();
		content = new HorizontalPanel();
		content.setLayout(new HBoxLayout());

		this.eventbus = eventbus;
		this.step = step;

		treeStore = new ListStore<Species>();
		traitStore = new ListStore<Species>();
		treeids = new ArrayList<String>();
		traitids = new ArrayList<String>();
	}

	@Override
	public VerticalPanel assembleView() {

		widget.setScrollMode(Scroll.AUTO);

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

		// match corresponding row
		treeSpecies.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@SuppressWarnings("unchecked")
			public void handleEvent(BaseEvent be) {
				GridEvent<Species> ge = (GridEvent<Species>) be;
				if (ge.getRowIndex() >= 0) {
					Species s = treeSpecies.getSelectionModel()
							.getSelectedItem();
					for (Species spe : traitSpecies.getStore().getModels()) {
						if (spe.get("name").toString().equals(
								s.get("name").toString())) {
							traitSpecies.getSelectionModel().select(false, spe);
							return;
						}
					}
					traitSpecies.getSelectionModel().deselectAll();
				}

			}

		});

		// match corresponding row
		traitSpecies.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@SuppressWarnings("unchecked")
			public void handleEvent(BaseEvent be) {
				GridEvent<Species> ge = (GridEvent<Species>) be;
				if (ge.getRowIndex() >= 0) {
					Species s = traitSpecies.getSelectionModel()
							.getSelectedItem();
					for (Species spe : treeSpecies.getStore().getModels()) {
						if (spe.get("name").toString().equals(
								s.get("name").toString())) {
							treeSpecies.getSelectionModel().select(false, spe);
							return;
						}
					}
					treeSpecies.getSelectionModel().deselectAll();
				}

			}

		});

		// set cell bg color to red if there is no match
		treeSpecies.getView().setViewConfig(new ReconcileGridViewConfig());

		// set cell bg color to red if there is no match
		traitSpecies.getView().setViewConfig(new ReconcileGridViewConfig());
		
		GridDragSource source1 = new GridDragSource(treeSpecies);
		source1.setGroup("tree");
		GridDragSource source2 = new GridDragSource(traitSpecies);
		source2.setGroup("trait");

		GridDropTarget target = new GridDropTarget(treeSpecies);
		target.setAllowSelfAsSource(true);
		target.setGroup("tree");
		target.setFeedback(Feedback.INSERT);

		GridDropTarget target1 = new GridDropTarget(traitSpecies);
		target1.setAllowSelfAsSource(true);
		target1.setGroup("trait");
		target1.setFeedback(Feedback.INSERT);

		content.add(treeSpecies);
		content.add(traitSpecies);
		info
				.add(new Html(
						"<span class=text>Match the species from tree and trait.Click and hold to select a species and drop at desired location.</span>"));
		info.setStyleAttribute("padding", "3px");
		widget.add(info);
		widget.add(content);
		return widget;
	}

	public void isReadyForNext() {
		DataSelectedEvent event = null;
		event = new DataSelectedEvent(step, true, null);
		eventbus.fireEvent(event);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setJobParams(JobParams params) {
		// have to get the tree and trait species for selected tree and traits
		ArrayList<Tree> trees = (ArrayList<Tree>) params.get("trees");
		ArrayList<Trait> traits = (ArrayList<Trait>)params.get("traits");
		ArrayList<String> tid = new ArrayList<String>();
		ArrayList<String> trid = new ArrayList<String>();
		if(trees != null ) {
			for (Tree t : trees) {
				tid.add((String)t.get("id"));
			}
		}
		
		if (traits != null) {
			for (Trait t : traits) {
				trid.add((String)t.get("id"));
			}
		}
		
		if(treeids.containsAll(tid) && traitids.containsAll(trid)) {
			//nochange in selected trees and traits. already reconciled if we are inside this if
			return;
		} else {
			treeids.clear();
			treeids.addAll(tid);
			traitids.clear();
			traitids.addAll(trid);
			getSpecies(treeids,traitids);
		}
	}

	/**
	 * build tree and trait store
	 */
	private void buildStore() {

		boolean matched = false;

		boolean error = false;

		boolean warnings = false;

		ArrayList<Species> treeSpecies = new ArrayList<Species>();
		ArrayList<Species> traitSpecies = new ArrayList<Species>();
		ArrayList<Species> unmatchedTreeSpecies = new ArrayList<Species>();

		// tranform into arraylist for easy processing
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

		// unmatached tree species. error condition
		for (Species s : unmatchedTreeSpecies) {
			treeStore.add(s);
			traitStore.add(new Species("-1", ""));
			error = true;
		}

		if (error) {
			MessageNotificationEvent event = new MessageNotificationEvent(
					displayStrings.matchingTreeSpecies(),
					MessageNotificationEvent.MessageType.ERROR);
			eventbus.fireEvent(event);
		}

		// unmatched trait species
		for (Species s : traitSpecies) {
			treeStore.add(new Species("-1", ""));
			traitStore.add(s);
			warnings = true;
		}

		if (warnings) {
			MessageNotificationEvent event = new MessageNotificationEvent(
					displayStrings.matchingTraitSpecies(),
					MessageNotificationEvent.MessageType.ERROR);
			eventbus.fireEvent(event);
		}

		DataSelectedEvent event;
		if (!error) {
			// must add matching tree and trait species here
			event = new DataSelectedEvent(step, true, null);
		} else {
			event = new DataSelectedEvent(step, false, null);
		}

		eventbus.fireEvent(event);
	}

//	private JsArray<SpeciesInfo> mockTreeSpecies() {
//		String json = "{\"species\":["
//				+ "{\"id\":\"1\",\"name\":\"Aphriza_virgata\"},"
//				+ "{\"id\":\"2\",\"name\":\"Bartramia_longicauda\"},"
//				+ "{\"id\":\"3\",\"name\":\"Eudromias_morinellu\"},"
//				+ "{\"id\":\"4\",\"name\":\"Calidris_mauri\"},"
//				+ "{\"id\":\"5\",\"name\":\"Limosa_limosa\"},"
//				+ "{\"id\":\"6\",\"name\":\"Tringa_erythropus\"},"
//				+ "{\"id\":\"7\",\"name\":\"Jacana_jacana\"},"
//				+ "{\"id\":\"8\",\"name\":\"Haematopus_finschi\"},"
//				+ "{\"id\":\"9\",\"name\":\"Numenius_phaeopus\"},"
//				+ "{\"id\":\"10\",\"name\":\"Limnodromus_griseus\"}" + "]}";
//
//		JSONObject obj = JSONParser.parse(json).isObject();
//		String evalString = obj.get("species").toString();
//		JsArray<SpeciesInfo> species = asArrayOfSpecies(evalString);
//		return species;
//	}
//
//	private JsArray<SpeciesInfo> mockTraitSpecies() {
//		String json = "{\"species\":["
//				+ "{\"id\":\"1\",\"name\":\"Aphriza_virgata\"},"
//				+ "{\"id\":\"2\",\"name\":\"Limnodromus_griseus\"},"
//				+ "{\"id\":\"3\",\"name\":\"Eudromias_morinellu\"},"
//				+ "{\"id\":\"4\",\"name\":\"Jacana_jacana\"},"
//				+ "{\"id\":\"5\",\"name\":\"Limosa_limosa\"},"
//				+ "{\"id\":\"6\",\"name\":\"Tringa_erythropus\"},"
//				+ "{\"id\":\"7\",\"name\":\"Calidris_mauri\"},"
//				+ "{\"id\":\"8\",\"name\":\"Haematopus_finschi\"},"
//				+ "{\"id\":\"9\",\"name\":\"Numenius_phaeopus\"},"
//				+ "{\"id\":\"11\",\"name\":\"Numenius_phaeopus11\"},"
//				+ "{\"id\":\"10\",\"name\":\"Bartramia_longicauda\"}" + "]}";
//
//		JSONObject obj = JSONParser.parse(json).isObject();
//		String evalString = obj.get("species").toString();
//		JsArray<SpeciesInfo> species = asArrayOfSpecies(evalString);
//		return species;
//	}

	private final native JsArray<SpeciesInfo> asArrayOfSpecies(String json) /*-{
																			return eval(json);
																			}-*/;
	
	private void getSpecies(ArrayList<String> treeids, ArrayList<String> traitids) {
		StringBuilder sb = new StringBuilder("{\"ids\":[");
		
		
		
		if(treeids != null) {
			for(String id : treeids) {
				sb.append("\"" + id + "\",");
			}
			//remove unwanted comma
			sb.deleteCharAt(sb.length() -1);
			sb.append("]}");
			
			TreeServices.getTreeSpecies(sb.toString(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					if(result != null) {
						JSONObject obj = JSONParser.parse(result).isObject();
						String evalString = obj.get("species").toString();
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
			
			if(traitids!=null) {
				TraitServices.getSpeciesNames(traitids.get(0), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						traitSpeciesNames = null;
						isTraitServiceComplete = true;
					}

					@Override
					public void onSuccess(String result) {
						if(result != null) {
							JSONObject obj = JSONParser.parse(result).isObject();
							String evalString = obj.get("species").toString();
							traitSpeciesNames = asArrayOfSpecies(evalString);
							isTraitServiceComplete = true;
						}
						
					}
				});
			}
			 
			
		 //check if services have completed.
	        t = new Timer() {
	    	int elapsed = 0;
			@Override
			public void run() {
				if(isTraitServiceComplete == true && isTreeServiceComplete == true) {
					t.cancel();
					buildStore();
				} else if (elapsed == TIMEOUT) {
					t.cancel();
				} else {
					elapsed = elapsed + CHECK_INTERVAL;
				}
			}
		};
		
		t.scheduleRepeating(CHECK_INTERVAL);
	}

	/**
	 * set cell bg color to red if there is no match
	 * @author sriram
	 *
	 */
	class ReconcileGridViewConfig extends GridViewConfig {
		@Override
		public String getRowStyle(ModelData model, int rowIndex,
				ListStore<ModelData> ls) {
			if (model.get("name").equals("")) {
				return "reconclie";
			} else {
				return ".x-grid3-cell-inner";
			}
		}
	}

}
