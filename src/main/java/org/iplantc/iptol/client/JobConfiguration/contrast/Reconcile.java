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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
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

	public IndepdentContrastJobView view;

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
		treeSpecies.getView().setViewConfig(new GridViewConfig() {
			@Override
			public String getRowStyle(ModelData model, int rowIndex,
					ListStore<ModelData> ls) {
				if (model.get("name").equals("")) {
					return "reconclie";
				} else {
					return ".x-grid3-cell-inner";
				}
			}
		});

		// set cell bg color to red if there is no match
		traitSpecies.getView().setViewConfig(new GridViewConfig() {
			@Override
			public String getRowStyle(ModelData model, int rowIndex,
					ListStore<ModelData> ls) {
				if (model.get("name").equals("")) {
					return "reconclie";
				} else {
					return ".x-grid3-cell-inner";
				}
			}
		});

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

	@Override
	public void setJobParams(JobParams params) {

		// have to get the tree and trait species for selected tree and traits
		buildStore();

	}

	private void buildStore() {

		boolean matched = false;

		boolean error = false;

		boolean warnings = false;

		JsArray<SpeciesInfo> tree_species = mockTreeSpecies();
		JsArray<SpeciesInfo> trait_species = mockTraitSpecies();

		ArrayList<Species> treeSpecies = new ArrayList<Species>();
		ArrayList<Species> traitSpecies = new ArrayList<Species>();
		ArrayList<Species> unmatchedTreeSpecies = new ArrayList<Species>();

		// tranform into arraylist for easy processing
		for (int i = 0; i < tree_species.length(); i++) {
			treeSpecies.add(new Species(tree_species.get(i).getId(),
					tree_species.get(i).getName()));
		}

		for (int j = 0; j < trait_species.length(); j++) {
			traitSpecies.add(new Species(trait_species.get(j).getId(),
					trait_species.get(j).getName()));
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

	private JsArray<SpeciesInfo> mockTreeSpecies() {
		String json = "{\"species\":["
				+ "{\"id\":\"1\",\"name\":\"Aphriza_virgata\"},"
				+ "{\"id\":\"2\",\"name\":\"Bartramia_longicauda\"},"
				+ "{\"id\":\"3\",\"name\":\"Eudromias_morinellu\"},"
				+ "{\"id\":\"4\",\"name\":\"Calidris_mauri\"},"
				+ "{\"id\":\"5\",\"name\":\"Limosa_limosa\"},"
				+ "{\"id\":\"6\",\"name\":\"Tringa_erythropus\"},"
				+ "{\"id\":\"7\",\"name\":\"Jacana_jacana\"},"
				+ "{\"id\":\"8\",\"name\":\"Haematopus_finschi\"},"
				+ "{\"id\":\"9\",\"name\":\"Numenius_phaeopus\"},"
				+ "{\"id\":\"10\",\"name\":\"Limnodromus_griseus\"}" + "]}";

		JSONObject obj = JSONParser.parse(json).isObject();
		String evalString = obj.get("species").toString();
		JsArray<SpeciesInfo> species = asArrayOfSpecies(evalString);
		return species;
	}

	private JsArray<SpeciesInfo> mockTraitSpecies() {
		String json = "{\"species\":["
				+ "{\"id\":\"1\",\"name\":\"Aphriza_virgata\"},"
				+ "{\"id\":\"2\",\"name\":\"Limnodromus_griseus\"},"
				+ "{\"id\":\"3\",\"name\":\"Eudromias_morinellu\"},"
				+ "{\"id\":\"4\",\"name\":\"Jacana_jacana\"},"
				+ "{\"id\":\"5\",\"name\":\"Limosa_limosa\"},"
				+ "{\"id\":\"6\",\"name\":\"Tringa_erythropus\"},"
				+ "{\"id\":\"7\",\"name\":\"Calidris_mauri\"},"
				+ "{\"id\":\"8\",\"name\":\"Haematopus_finschi\"},"
				+ "{\"id\":\"9\",\"name\":\"Numenius_phaeopus\"},"
				+ "{\"id\":\"11\",\"name\":\"Numenius_phaeopus11\"},"
				+ "{\"id\":\"10\",\"name\":\"Bartramia_longicauda\"}" + "]}";

		JSONObject obj = JSONParser.parse(json).isObject();
		String evalString = obj.get("species").toString();
		JsArray<SpeciesInfo> species = asArrayOfSpecies(evalString);
		return species;
	}

	private final native JsArray<SpeciesInfo> asArrayOfSpecies(String json) /*-{
																			return eval(json);
																			}-*/;

}
