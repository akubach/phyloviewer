package org.iplantc.iptol.client.JobConfiguration.contrast;

import java.util.List;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.Card;
import org.iplantc.iptol.client.JobConfiguration.JobParams;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * @author sriram Builds the confirmation step for independent contrast job.
 * 
 */
public class ConfirmJobDetails extends Card {

	private VerticalPanel panel;
	private HorizontalPanel info;
	private ContentPanel selectedtrees;
	private ListView<Tree> treesList;
	private ContentPanel selectedtraits;
	private ListView<Trait> traitsList;
	private ListStore<Tree> treeStore;
	private ListStore<Trait> traitStore;
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);
	private ContentPanel optionalParams;

	private HandlerManager eventbus;

	/**
	 * Create a new instance of ConfirmDetails
	 * 
	 * @param step
	 *            step number
	 * @param eventbus
	 *            event bus for handling events
	 */
	public ConfirmJobDetails(int step, HandlerManager eventbus) {
		this.eventbus = eventbus;
		this.step = step;
		panel = new VerticalPanel();
		info = new HorizontalPanel();
		selectedtrees = new ContentPanel();
		treesList = new ListView<Tree>();
		selectedtraits = new ContentPanel();
		traitsList = new ListView<Trait>();
		optionalParams = new ContentPanel();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setJobParams(JobParams params) {
		// clear store first
		treeStore.removeAll();
		traitStore.removeAll();

		treeStore.add((List<Tree>) params.get("trees"));
		traitStore.add((List<Trait>) params.get("traits"));

		optionalParams.addText("<span>"
				+ displayStrings.printCorrelationsRegressions()
				+ ": "
				+ params.get(displayStrings.printCorrelationsRegressions())
						.toString() + "</span>");
		optionalParams.addText("<span>"
				+ displayStrings.printContrasts()
				+ ": "
				+ params.get(displayStrings.printContrasts()).toString()
						.equals("true") + "</span>");
		optionalParams.addText("<span>" + displayStrings.printDataSets() + ": "
				+ params.get(displayStrings.printDataSets()).toString()
				+ "</span>");

	}

	/**
	 * Put together all the widgets
	 */
	@Override
	public VerticalPanel assembleView() {
		panel.setHeight(250);
		panel.setAutoHeight(true);
		panel.setSpacing(5);

		info.addText("<span>" + displayStrings.confirmJob() + "</span>");

		selectedtrees.setAutoHeight(true);
		selectedtrees.setHeaderVisible(true);
		selectedtrees.setHeading(displayStrings.selectedTrees());

		treesList.setDisplayProperty("treename");
		treeStore = new ListStore<Tree>();

		treesList.setStore(treeStore);
		selectedtrees.add(treesList);

		selectedtraits.setAutoHeight(true);
		selectedtraits.setHeaderVisible(true);
		selectedtraits.setHeading(displayStrings.selectedTraits());

		traitsList.setDisplayProperty("filename");
		traitStore = new ListStore<Trait>();
		traitsList.setStore(traitStore);

		selectedtraits.add(traitsList);

		optionalParams.setHeading(displayStrings.optionalParameters());
		optionalParams.setBodyBorder(true);
		optionalParams.setBorders(true);
		optionalParams.setStyleAttribute("background-color", "white");
		panel.setLayout(new VBoxLayout());

		panel.add(info);
		panel.add(selectedtrees);
		panel.add(selectedtraits);
		panel.add(optionalParams);

		return panel;
	}
}
