/**
 * 
 */
package org.iplantc.iptol.client.JobConfiguration.contrast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.iplantc.iptol.client.ErrorHandler;
import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.Card;
import org.iplantc.iptol.client.JobConfiguration.DataSelectedEvent;
import org.iplantc.iptol.client.JobConfiguration.DataSelectedEventHandler;
import org.iplantc.iptol.client.JobConfiguration.EnableStepEvent;
import org.iplantc.iptol.client.JobConfiguration.JobParams;
import org.iplantc.iptol.client.JobConfiguration.JobStep;
import org.iplantc.iptol.client.JobConfiguration.JobToolBarSaveClickEvent;
import org.iplantc.iptol.client.JobConfiguration.JobToolBarSaveClickEventHandler;
import org.iplantc.iptol.client.JobConfiguration.JobView;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEvent;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEventHandler;
import org.iplantc.iptol.client.JobConfiguration.NavButtonClickEvent;
import org.iplantc.iptol.client.JobConfiguration.NavButtonEventClickEventHandler;
import org.iplantc.iptol.client.JobConfiguration.SaveJob;
import org.iplantc.iptol.client.JobConfiguration.MessageNotificationEvent.MessageType;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.tips.Tip;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

/**
 * @author sriram Builds the cards in the wizard for this job. Acts as a
 *         controller in initializing the cards and setting the appropriate card
 *         for each step.
 */
public class IndepdentContrastJobView implements JobView {

	private ContentPanel panel;
	private CardLayout layout;

	private Card selecttreesGrid;
	private Card selecttraitGrid;
	private Card reconcile;
	private Card selectparams;
	private Card confirm;

	private ArrayList<Card> cards;

	private JobParams params;

	private ArrayList<JobStep> steps;
	private String workspaceId;

	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);

	/**
	 * Create a new IndepdentContrastJobView
	 * 
	 * @param workspaceId
	 * 
	 * @param eventbus
	 *            event bus for handling events
	 */
	// this must take a job config object from workspace service
	public IndepdentContrastJobView(String workspaceId) {
		panel = new ContentPanel();
		layout = new CardLayout();
		params = new JobParams();
		cards = new ArrayList<Card>();
		this.workspaceId = workspaceId;
		removeHandlers();
		registerEventHandlers();
	}

	/**
	 * get the wizard for configuring this job
	 * 
	 */
	@Override
	public ContentPanel getWizard() {
		panel.setFrame(true);
		panel.setHeading(displayStrings.independentcontrast());
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		panel.setLayout(layout);

		final LayoutContainer c1 = new LayoutContainer();
		selecttreesGrid = new SelectTrees(0, workspaceId);
		c1.add(selecttreesGrid.assembleView());
		panel.add(c1);

		final LayoutContainer c2 = new LayoutContainer();
		selecttraitGrid = new SelectTraits(1, workspaceId);
		c2.add(selecttraitGrid.assembleView());
		panel.add(c2);

		final LayoutContainer c3 = new LayoutContainer();
		reconcile = new Reconcile(2);
		c3.add(reconcile.assembleView());
		panel.add(c3);

		final LayoutContainer c4 = new LayoutContainer();
		selectparams = new SelectOptionalParams(3);
		c4.add(selectparams.assembleView());
		panel.add(c4);

		final LayoutContainer c5 = new LayoutContainer();
		confirm = new ConfirmJobDetails(4);
		c5.add(confirm.assembleView());
		panel.add(c5);

		layout.setActiveItem(panel.getItem(0));

		// add the cards in order
		cards.add(selecttreesGrid);
		cards.add(selecttraitGrid);
		cards.add(reconcile);
		cards.add(selectparams);
		cards.add(confirm);

		return panel;
	}

	/**
	 * Add handlers for events
	 */
	private void registerEventHandlers() {
		EventBus eventbus = EventBus.getInstance();
		eventbus.addHandler(NavButtonClickEvent.TYPE,
				new NavButtonEventClickEventHandler() {
					@Override
					public void onClick(NavButtonClickEvent navButton) {
						JobStep step = navButton.getStep();
						setJobStep(step.getStepno());
					}
				});

		eventbus.addHandler(DataSelectedEvent.TYPE,
				new DataSelectedEventHandler() {
					@Override
					public void onDataSelected(DataSelectedEvent dse) {
						if (dse.isSelected()) {
							HashMap<String, Object> param = dse.getData();
							// collect data from the step if any
							if (param != null) {
								Iterator<String> it = param.keySet().iterator();
								while (it.hasNext()) {
									String key = (String) it.next();
									params.add(key, param.get(key));
								}
							}

							// update step status
							for (JobStep step : steps) {
								if (step.getStepno() == dse.getStep()) {
									step.setComlpete(true);
									// if not last step then
									if (steps.size() - 1 != dse.getStep()) {
										EventBus eventbus = EventBus
												.getInstance();
										EnableStepEvent event = new EnableStepEvent(
												step.getStepno() + 1, true);
										eventbus.fireEvent(event);
									}
								}
							}

						} else {
							for (int i = dse.getStep() + 1; i < steps.size(); i++) {
								steps.get(i).setComlpete(false);
								cards.get(i).reset();
								EventBus eventbus = EventBus.getInstance();
								EnableStepEvent event = new EnableStepEvent(
										steps.get(i).getStepno(), false);
								eventbus.fireEvent(event);
							}
						}

					}
				});

		eventbus.addHandler(MessageNotificationEvent.TYPE,
				new MessageNotificationEventHandler() {
					@Override
					public void onMessage(MessageNotificationEvent mne) {
						if (mne.getMsgType() == MessageType.ERROR) {
							ErrorHandler.post(mne.getMsg());
						} else {
							final Tip t = new Tip();
							t.setHeading(mne.getMsgType().name());
							t.setHeight(30);
							t.setWidth(panel.getWidth());
							t.addText(mne.getMsg());
							t.setClosable(true);
							t.showAt(panel.getPosition(false));
							Timer timer = new Timer() {
								public void run() {
									t.hide();
								}
							};

							timer.schedule(5000);
						}
					}
				});

		eventbus.addHandler(JobToolBarSaveClickEvent.TYPE,
				new JobToolBarSaveClickEventHandler() {
					@Override
					public void onSave(JobToolBarSaveClickEvent saveEvent) {
						SaveJob savejob = new SaveJob(saveEvent.getJobName(),
								contructParamsAsJson(saveEvent.getJobName()),
								workspaceId);
						savejob.save();
					}
				});

	}

	/**
	 * Convert job parameters into JSON format
	 * 
	 * @param jobname
	 * @return
	 */

	@SuppressWarnings("unchecked")
	private String contructParamsAsJson(String jobname) {
		StringBuilder s = new StringBuilder();
		s.append("{\"name\":" + "\"" + jobname + "\",\"treeIds\":[");
		ArrayList<Tree> trees = (ArrayList<Tree>) params.get("trees");
		ArrayList<Trait> traits = (ArrayList<Trait>) params.get("traits");
		if (trees != null) {
			for (Tree t : trees) {
				s.append("\"" + (String) t.get("id") + "\",");
			}
		}

		// delete last comma
		s.deleteCharAt(s.length() - 1);
		s.append("],\"traitId\":");
		if (traits != null) {
			for (Trait t : traits) {
				s.append("\"" + (String) t.get("id") + "\"");
			}
		}

		s.append(",\"includeCorrelations\":" + "\""
				+ params.get(displayStrings.printCorrelationsRegressions())
				+ "\",");
		s.append("\"includeContrasts\":" + "\""
				+ params.get(displayStrings.printContrasts()) + "\",");
		s.append("\"includeData\":" + "\""
				+ params.get(displayStrings.printDataSets())
				+ "\",\"reconciliation\":{");

		HashMap<String, String> reconciledValues = (HashMap<String, String>) params
				.get("reconciliation");
		if (reconciledValues != null) {
			String key = null;
			Iterator it = reconciledValues.keySet().iterator();
			while (it.hasNext()) {
				key = it.next().toString();
				s.append("\"" + key + "\":\""
						+ reconciledValues.get(key).toString() + "\",");
			}
			s.deleteCharAt(s.length() - 1);
		}
		s.append("}}");
		return s.toString();
	}

	/**
	 * clear handlers before adding again
	 */
	private void removeHandlers() {
		EventBus eventbus = EventBus.getInstance();
		eventbus.removeHandlers(NavButtonClickEvent.TYPE);
		eventbus.removeHandlers(DataSelectedEvent.TYPE);
		eventbus.removeHandlers(MessageNotificationEvent.TYPE);
		eventbus.removeHandlers(JobToolBarSaveClickEvent.TYPE);
	}

	/**
	 * set the data and view for the current step
	 */
	@Override
	public void setJobStep(int step) {

		if (step == selecttreesGrid.getStep()) {
			selecttreesGrid.setJobParams(params);
		} else if (step == selecttraitGrid.getStep()) {
			selecttraitGrid.setJobParams(params);
		} else if (step == selectparams.getStep()) {
			selectparams.setJobParams(params);
			selectparams.isReadyForNext();
		} else if (step == reconcile.getStep()) {
			reconcile.setJobParams(params);
		} else {
			confirm.setJobParams(params);
		}

		layout.setActiveItem(panel.getItem(step));
	}

	/**
	 * get the steps involved in configuring this job. This should come from
	 * meta-data service
	 * 
	 * @return
	 */
	@Override
	public ArrayList<JobStep> getJobConfigSteps() {
		steps = new ArrayList<JobStep>();
		steps.add(new JobStep(0, "Select Tree(s)", true));
		steps.add(new JobStep(1, "Select Traits", false));
		steps.add(new JobStep(2, "Reconcile Taxa", false));
		steps.add(new JobStep(3, "Select Params", false));
		steps.add(new JobStep(4, "Confirm", false));
		return steps;
	}

	public JobParams getParams() {
		return params;
	}

}
