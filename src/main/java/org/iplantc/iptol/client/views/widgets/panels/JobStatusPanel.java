package org.iplantc.iptol.client.views.widgets.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolDisplayStrings;
import org.iplantc.iptol.client.JobConfiguration.Job;
import org.iplantc.iptol.client.JobConfiguration.JobInfo;
import org.iplantc.iptol.client.events.JobSavedEvent;
import org.iplantc.iptol.client.events.JobSavedEventHandler;
import org.iplantc.iptol.client.images.Resources;
import org.iplantc.iptol.client.services.JobServices;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JobStatusPanel extends ContentPanel {

	private Button btnStart;

	private Button btnDelete;

	private Button btnRefresh;

	private Grid<Job> grid;

	private ListStore<Job> jobs;

	private String caption;

	public static final int JOB_CHECK_INTERVAL = 5000;

	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
			.create(IptolDisplayStrings.class);

	public JobStatusPanel(String caption) {
		super();
		this.caption = caption;
		jobs = new ListStore<Job>();
		assembleView();
	}

	public void assembleView() {
		getContrastJobs();
		grid = new Grid<Job>(jobs, buildColumnModel());
		grid.setHeight(300);
		grid.setWidth(525);
		grid.getView().setViewConfig(new JobGridViewConfig());
		EventBus eventbus = EventBus.getInstance();
		eventbus.removeHandlers(JobSavedEvent.TYPE);
		eventbus.addHandler(JobSavedEvent.TYPE, new JobSavedEventHandler() {

			@Override
			public void onJobSaved(JobSavedEvent jse) {
				updateStore(jse.getJobs());
			}
		});
		grid.getView().setEmptyText(displayStrings.noJobs());
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setStripeRows(true);
		this.setHeading(caption);
		this.setTopComponent(buildGridToolBar());
		this.add(grid);
	}

	// ////////////////////////////////////////
	private ColumnModel buildColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("name", "Name", 150));
		configs.add(new ColumnConfig("created", "Date Submitted", 200));
		configs.add(new ColumnConfig("status", "Status", 150));
		return new ColumnModel(configs);
	}

	private void setStore(ArrayList<Job> jobs) {
		this.jobs.removeAll();
		this.jobs.add(jobs);
	}

	private ToolBar buildGridToolBar() {
		ToolBar t = new ToolBar();
		btnStart = new Button("Start");
		btnStart.setIcon(Resources.ICONS.add());
		btnStart.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (grid.getSelectionModel().getSelectedItems().size() > 0) {
					if ("READY".equalsIgnoreCase(grid.getSelectionModel()
							.getSelectedItem().get("status").toString())) {
						startJob(grid.getSelectionModel().getSelectedItem()
								.get("id").toString());
					} else {
						MessageBox.alert("Alert",
								displayStrings.jobCannotRun(), null);
					}
				} else {
					MessageBox.alert("Alert", displayStrings.noJobSelected(),
							null);
				}
			}

		});

		btnDelete = new Button("Delete");
		btnDelete.setIcon(Resources.ICONS.cancel());
		btnDelete.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (grid.getSelectionModel().getSelectedItems().size() > 0) {
					MessageBox.confirm("Confirm", displayStrings
							.jobDeleteConfirm(),
							new Listener<MessageBoxEvent>() {
								@Override
								public void handleEvent(MessageBoxEvent be) {
									Button btn = be.getButtonClicked();
									if (btn.getText().equalsIgnoreCase("yes")) {
										deleteJob(grid.getSelectionModel()
												.getSelectedItem().get("id")
												.toString());
									}
								}
							});

				} else {
					MessageBox.alert("Alert", displayStrings.noJobSelected(),
							null);
				}
			}

		});

		btnRefresh = new Button("Refresh");
		btnRefresh.setIcon(Resources.ICONS.refresh());
		btnRefresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				getContrastJobs();
			}

		});

		t.add(btnStart);
		t.add(btnDelete);
		t.add(btnRefresh);
		return t;
	}

	private void startJob(final String jobid) {

		JobServices.runContrastJob(jobid, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				org.iplantc.iptol.client.ErrorHandler.post("Error starting this job!");
			}

			@Override
			public void onSuccess(String result) {
				// Window.alert("success-->" + result);
				Info.display("Information", displayStrings.jobSubmitted());
				// now check for job completion. Poll the job table
				checkJobStatus(jobid);
			}
		});

	}

	// for now we check for one job at a time
	private void checkJobStatus(final String jobid) {
		JobStatusCheckTimer timer = new JobStatusCheckTimer(jobid);
		timer.scheduleRepeating(JOB_CHECK_INTERVAL);
	}

	private void deleteJob(String jobid) {

		JobServices.deleteContrastJob(jobid, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				org.iplantc.iptol.client.ErrorHandler.post("Error deleting job!");

			}

			@Override
			public void onSuccess(String result) {
				// Window.alert("success ->" + result);
				jobs.remove(grid.getSelectionModel().getSelectedItem());
				Info.display("Information", displayStrings.jobDeleted());

			}
		});

	}

	private void updateStore(ArrayList<Job> jobs) {
		this.jobs.add(jobs);
		Info.display("Information", displayStrings.jobSaved());
	}

	private void updateJobStatus(ArrayList<Job> jobstoUpdate) {
		for (Job j : jobstoUpdate) {
			for (Job k : this.jobs.getModels()) {
				if (j.get("id").equals(k.get("id"))) {
					this.jobs.remove(k);
					this.jobs.add(j);
				}
			}
		}
	}

	/**
	 * A native method to eval returned json
	 * 
	 * @param json
	 * @return
	 */
	private final native JsArray<JobInfo> asArrayofJobData(String json) /*-{
		return eval(json);
	}-*/;

	private void getContrastJobs() {
		JobServices.getContrastJobs(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				JsArray<JobInfo> jobinfos = asArrayofJobData(result);
				Job j = null;
				ArrayList<Job> jobs = new ArrayList<Job>();
				Date d = null;
				for (int i = 0; i < jobinfos.length(); i++) {
					d = new Date(Long.parseLong(jobinfos.get(i)
							.getCreationDate()));
					j = new Job(jobinfos.get(i).getId(), jobinfos.get(i)
							.getName(), d.toString(), jobinfos.get(i)
							.getStatus());
					jobs.add(j);
				}
				setStore(jobs);
			}

			@Override
			public void onFailure(Throwable caught) {
				org.iplantc.iptol.client.ErrorHandler.post("Error getting the list of jobs!");

			}
		});
	}
	/**
	 * 
	 * @author sriram checks the status of the given job in scheduled manner
	 *
	 */
	class JobStatusCheckTimer extends Timer {

		String jobid;

		JobStatusCheckTimer(String jobid) {
			this.jobid = jobid;
		}

		@Override
		public void run() {
			JobServices.getContrastJobs(new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					JsArray<JobInfo> jobinfos = asArrayofJobData(result);
					Job j = null;
					ArrayList<Job> jobs = new ArrayList<Job>();
					Date d = null;
					for (int i = 0; i < jobinfos.length(); i++) {
						if (jobid.equals(jobinfos.get(i).getId().toString())
								&& (jobinfos.get(i).getStatus().equals("ERROR") || jobinfos
										.get(i).getStatus().equals("COMPLETED"))) {
							d = new Date(Long.parseLong(jobinfos.get(i)
									.getCreationDate()));
							j = new Job(jobinfos.get(i).getId(), jobinfos
									.get(i).getName(), d.toString(), jobinfos
									.get(i).getStatus());
							jobs.add(j);
							//Window.alert("got update for job");
							cancelTimer();
							break;
						}

					}

					if (jobs.size() > 0) {
						updateJobStatus(jobs);
					}

				}

				@Override
				public void onFailure(Throwable caught) {
					org.iplantc.iptol.client.ErrorHandler.post("Error getting the list of jobs!");
					cancelTimer();
				}

			});
		}

		private void cancelTimer() {
			this.cancel();
		}

	}
	
	/**
	 * set cell text color to red for error status
	 * @author sriram
	 *
	 */
	class JobGridViewConfig extends GridViewConfig {
		@Override
		public String getRowStyle(ModelData model, int rowIndex,
				ListStore<ModelData> ls) {
			if (model.get("status").equals("ERROR")) {
				return "jobErrorStatus";
			} else {
				return ".x-grid3-cell-inner";
			}
		}
	}

}
