/**
 * 
 */
package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;
import java.util.List;



import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * @author sriram
 * 
 */
public class IndepdentContrastJobView implements JobView {

	ContentPanel panel;
	CardLayout layout;

	SelectTreesGrid selecttreesGrid;
	SelectTraitGrid selecttraitGrid;
	Reconcile reconcile;
	HandlerManager eventbus;
	

	// this must take a job config object from workspace service
	public IndepdentContrastJobView(HandlerManager eventbus) {
		panel = new ContentPanel();
		layout = new CardLayout();
		this.eventbus = eventbus;
	}
	
	/**
	 * get the wizard for configuring this job 
	 * 
	 */
	@Override
	public ContentPanel getWizard() {
		panel.setFrame(true);
		panel.setHeading("Independent Contrast");
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		panel.setLayout(layout);

		final LayoutContainer c1 = new LayoutContainer();
		selecttreesGrid = new SelectTreesGrid(0,eventbus);
		c1.add(selecttreesGrid.assembleView());
		panel.add(c1);

		final LayoutContainer c2 = new LayoutContainer();
		selecttraitGrid = new SelectTraitGrid(1,eventbus);
		c2.add(selecttraitGrid.assembleView());
		panel.add(c2);

		final LayoutContainer c3 = new LayoutContainer();
		reconcile = new Reconcile(2, eventbus);
		c3.add(reconcile.assembleView());
		panel.add(c3);

		eventbus.addHandler(JobToolBarNextClickEvent.TYPE, new JobToolBarNextClickEventHandler() {
			public void onNextClick(JobToolBarNextClickEvent next) {
				JobStep step = next.getStep();
				setStep(Integer.parseInt(step.get("step").toString()) + 1);
			}
		});
		
		eventbus.addHandler(JobToolBarPrevClickEvent.TYPE, new JobToolBarPrevClickEventHandler() {
			public void onPrevClick(JobToolBarPrevClickEvent prev) {
				JobStep step = prev.getStep();
				setStep(Integer.parseInt(step.get("step").toString()) - 1);
			}
		});
		
		eventbus.addHandler(NavButtonClickEvent.TYPE, new NavButtonEventClickEventHandler() {
			@Override
			public void onClick(NavButtonClickEvent navButton) {
				JobStep step = navButton.getStep();
				setStep(Integer.parseInt(step.get("step").toString()));
			}
		});
		
		
		layout.setActiveItem(panel.getItem(0));
		return panel;
	}
	/**
	 * set the view for the current step
	 */
	@Override
	public void setStep(int step) {
		if (getJobConfigSteps().get(step).get("name").toString().equals(
				"Confirm")) {
			final LayoutContainer c4 = new LayoutContainer();
			c4.add(buildConfirmationPanel());
			panel.add(c4);
		} else if(getJobConfigSteps().get(step).get("name").toString().equals(
				"Select Tree(s)")) {
			selecttreesGrid.isReadyForNext();
		} else if (getJobConfigSteps().get(step).get("name").toString().equals(
				"Select Traits")) {
			selecttraitGrid.isReadyForNext();
		} else if(getJobConfigSteps().get(step).get("name").toString().equals(
				"Reconcile")) {
			reconcile.isReadyForNext();
		}
		layout.setActiveItem(panel.getItem(step));
	}
	/**
	 * build the confirmation view for the job
	 * @return
	 */
	private VerticalPanel buildConfirmationPanel() {
		VerticalPanel panel = new VerticalPanel();
		panel.setHeight(250);
		panel.setAutoHeight(true);
		panel.setSpacing(5);
		
		HorizontalPanel info = new HorizontalPanel();
		info.addText("<span>Review your job details and press save to save this job.</span>");
		
		ContentPanel selectedtrees = new ContentPanel();
		selectedtrees.setSize(200, 75);
		selectedtrees.setHeaderVisible(true);
		selectedtrees.setHeading("Selected Trees");

		ListView<Tree> trees = new ListView<Tree>();
		trees.setDisplayProperty("treename");
		ListStore<Tree> store = new ListStore<Tree>();
		store.add(selecttreesGrid.getSelectedData());
		trees.setStore(store);

		selectedtrees.add(trees);

		ContentPanel selectedtraits = new ContentPanel();
		selectedtraits.setSize(200, 75);
		selectedtraits.setHeaderVisible(true);
		selectedtraits.setHeading("Selected Traits");

		ListView<Trait> traits = new ListView<Trait>();
		traits.setDisplayProperty("traitblock");
		ListStore<Trait> store1 = new ListStore<Trait>();
		// Window.alert("->" + selecttraitGrid.getSelectedData());
		store1.add(selecttraitGrid.getSelectedData());
		traits.setStore(store1);

		selectedtraits.add(traits);

		panel.setLayout(new VBoxLayout());
		panel.add(info);
		panel.add(selectedtrees);
		panel.add(selectedtraits);

		return panel;
	}

	/**
	 * get the steps involved in configuring this job
	 * 
	 * @return
	 */
	@Override
	public ArrayList<JobStep> getJobConfigSteps() {
		ArrayList<JobStep> steps = new ArrayList<JobStep>();
		steps.add(new JobStep(0, "Select Tree(s)"));
		steps.add(new JobStep(1, "Select Traits"));
		steps.add(new JobStep(2, "Reconcile"));
		steps.add(new JobStep(3, "Confirm"));
		return steps;
	}

}
