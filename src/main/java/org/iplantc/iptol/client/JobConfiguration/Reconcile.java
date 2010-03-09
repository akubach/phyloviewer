package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.event.shared.HandlerManager;
/**
 * Provides a widget to compare tree and trait species.
 * Users can re-order species using drag and drop to match the species.
 * @author sriram
 *
 */
public class Reconcile {

	VerticalPanel widget;
	HorizontalPanel content;
	HorizontalPanel info;
	Grid<Species> treeSpecies;
	Grid<Species> traitSpecies;
	private HandlerManager eventbus;
	private int step;
	
	public Reconcile(int step, HandlerManager eventbus) {
		widget = new VerticalPanel();
		widget.setLayout(new VBoxLayout());
		info = new HorizontalPanel();
		content = new HorizontalPanel();
		content.setLayout(new HBoxLayout());
		this.step = step; 
	}

	public VerticalPanel assembleView() {

		widget.setScrollMode(Scroll.AUTO);
		widget.setHeight(275);

		ArrayList<ColumnConfig> config = new ArrayList<ColumnConfig>();
		ColumnConfig name = new ColumnConfig("name", "Tree Data Species", 100);
		config.add(name);
		ColumnModel cm = new ColumnModel(config);

		ListStore<Species> store = new ListStore<Species>();
		store.add(mockTreeSpecies());
		treeSpecies = new Grid<Species>(store, cm);
		treeSpecies.setAutoHeight(true);
		treeSpecies.setAutoExpandColumn("name");

		ArrayList<ColumnConfig> config1 = new ArrayList<ColumnConfig>();
		ColumnConfig name1 = new ColumnConfig("name", "Trait Data Species", 100);
		config1.add(name1);
		ColumnModel cm1 = new ColumnModel(config1);

		ListStore<Species> store1 = new ListStore<Species>();
		store1.add(mockTraitSpecies());
		traitSpecies = new Grid<Species>(store1, cm1);
		traitSpecies.setAutoHeight(true);
		traitSpecies.setAutoExpandColumn("name");

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
		DataSelectedEvent event =null;
		event = new DataSelectedEvent(step,true);
		eventbus.fireEvent(event);
	}
	
	private ArrayList<Species> mockTreeSpecies() {
		ArrayList<Species> species = new ArrayList<Species>();
		Species A = new Species("A");
		Species B = new Species("B");
		Species C = new Species("C");
		Species D = new Species("D");
		species.add(A);
		species.add(B);
		species.add(C);
		species.add(D);
		species.add(new Species("E"));
		species.add(new Species("F"));
		species.add(new Species("G"));
		species.add(new Species("H"));
		species.add(new Species("I"));
		species.add(new Species("J"));
		species.add(new Species("K"));
		species.add(new Species("L"));
		species.add(new Species("M"));
		species.add(new Species("N"));
		species.add(new Species("O"));
		species.add(new Species("P"));
		species.add(new Species("Q"));
		return species;
	}

	private ArrayList<Species> mockTraitSpecies() {
		ArrayList<Species> species = new ArrayList<Species>();
		Species A = new Species("A");
		Species B1 = new Species("B");
		Species C = new Species("C");
		Species D = new Species("D");
		Species E = new Species("E");

		species.add(E);
		species.add(A);
		species.add(B1);
		species.add(C);
		species.add(D);

		return species;
	}

}
