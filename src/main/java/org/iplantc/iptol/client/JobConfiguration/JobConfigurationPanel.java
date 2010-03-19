package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;

import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.contrast.IndepdentContrastJobView;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.Tip;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * A widget that renders job configuration wizard
 * 
 * @author sriram
 * 
 */
public class JobConfigurationPanel extends ContentPanel {

	private Dialog popup;
	private ContentPanel navpanel;
	private HandlerManager eventbus;
	private JobView icj;
	private ArrayList<ToggleButton> stepBtns;
	private JobToolBar toolbar;
	private BorderLayout layout;
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);

	public JobConfigurationPanel(HandlerManager eventbus) {
		super();
		this.eventbus = eventbus;
		this.setHeaderVisible(false);
		layout = new BorderLayout();
		this.setLayout(layout);
	}

	public void assembleView() {
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 150,
				100, 250);
		data.setMargins(new Margins(0, 5, 0, 0));
		data.setSplit(true);
		data.setCollapsible(true);
		data.setFloatable(true);
		icj = new IndepdentContrastJobView(eventbus);
		// this.add(buildNavigation(), data);
		buildNavigationPanel();
		this.add(navpanel, data);
		buildBottomComponent();
		this.setBottomComponent(toolbar);

		eventbus.addHandler(EnableStepEvent.TYPE, new EnableStepEventHandler() {
			@Override
			public void enableStep(EnableStepEvent es) {
				stepBtns.get(es.getStepno()).setEnabled(es.isEnable());
			}
		});

		BorderLayoutData wizardData = new BorderLayoutData(LayoutRegion.CENTER,
				150, 100, 250);
		wizardData.setMargins(new Margins(0, 5, 0, 0));
		wizardData.setSplit(true);
		wizardData.setCollapsible(true);
		wizardData.setFloatable(true);
		this.add(icj.getWizard(), wizardData);

		popup = new Dialog();
		popup.getButtonBar().hide();
		popup.setLayout(new FitLayout());
		popup.setHeight(500);
		popup.setWidth(600);
		popup.setHeading(displayStrings.configureNewJob());
		popup.add(this);
		popup.setModal(true);
		popup.show();
	}

	/**
	 * Build the navigation panel. This allows users to navigate between
	 * different job configuration steps.
	 * 
	 * @return
	 */
	private void buildNavigationPanel() {
		navpanel = new ContentPanel();
		navpanel.setFrame(true);
		navpanel.setHeaderVisible(true);
		ArrayList<JobStep> steps = icj.getJobConfigSteps();
		stepBtns = new ArrayList<ToggleButton>();
		ToggleButton stepBtn = null;
		for (JobStep step : steps) {
			stepBtn = new ToggleButton(step.getName());
			stepBtn.setEnabled(step.isDefaultEnable());
			stepBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					handleToogleClick(event.getSource());
				}

			});
			navpanel.add(stepBtn);
			stepBtns.add(stepBtn);
		}
		// first step down by default
		stepBtns.get(0).setEnabled(true);
		stepBtns.get(0).setDown(true);

	}

	private void handleToogleClick(Object source) {
		ToggleButton button = (ToggleButton) source;
		ToggleButton btn = null;
		JobStep step = null;
		for (int i = 0; i < stepBtns.size(); i++) {
			btn = stepBtns.get(i);
			if (btn.getText().equals(button.getText())) {
				btn.setDown(true);
				updateStep(i);
				step = new JobStep(i, stepBtns.get(i).getText(), true);
				NavButtonClickEvent event = new NavButtonClickEvent(step);
				eventbus.fireEvent(event);
				// first step
				if (i == 0) {
					toolbar.getFinish().disable();

				} // last step
				else if (i + 1 == stepBtns.size()) {
					toolbar.getFinish().enable();

				}
			} else {
				btn.setDown(false);
			}
		}

	}

	// set title for current step
	private void updateStep(int rowIndex) {
		navpanel.setHeading(displayStrings.steps() + " " + (rowIndex + 1)
				+ " of " + stepBtns.size());
	}

	private void hidePanel() {
		popup.hide();
	}

	/**
	 * Build bottom component toolbar with prev, next buttons
	 * 
	 * @return
	 */
	private void buildBottomComponent() {
		toolbar = new JobToolBar();

		toolbar.getFinish().disable();

		toolbar.getSave().addListener(Events.OnClick,
				new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {

						final MessageBox box = MessageBox.prompt(displayStrings
								.jobname(), displayStrings.newNameForJob());
						box.addCallback(new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								// TODO: call service to add job
								/*
								 * Job job = new
								 * Job("",be.getValue(),"Not Started");
								 * 
								 * RPCFacade.addJob(job,new
								 * AsyncCallback<List<Job>>() {
								 * 
								 * @Override public void onSuccess(List<Job>
								 * result) { eventbus.fireEvent(new
								 * JobStatusChangeEvent(result)); }
								 * 
								 * @Override public void onFailure(Throwable
								 * caught) { // TODO: handle failure } });
								 */

								// popup.hide();
							}
						});
					}

				});

		toolbar.getCancel().addListener(Events.OnClick,
				new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent be) {
						hidePanel();
					}
				});
	}

}
