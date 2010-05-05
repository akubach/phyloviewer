package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iplantc.de.client.DEDisplayStrings;
import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.ErrorHandler;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.JobSavedEvent;
import org.iplantc.de.client.events.JobSavedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.FileUploadedEvent;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.Job;
import org.iplantc.de.client.models.JsJob;
import org.iplantc.de.client.models.JsFile;
import org.iplantc.de.client.services.FolderServices;
import org.iplantc.de.client.services.JobServices;
import org.iplantc.de.client.utils.JsonUtil;
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
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
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

	private Button btnDownload;

	private Grid<Job> grid;

	private ListStore<Job> jobs;

	private String caption;

	private String workspaceId;

	public static final int JOB_CHECK_INTERVAL = 5000;

	public static final int JOB_CHECK_TIMEOUT = 50000;

	public enum JOB_STATUS {
		READY, ERROR, RUNNING, COMPLETED
	}

	private DEDisplayStrings displayStrings = (DEDisplayStrings) GWT
			.create(DEDisplayStrings.class);

	private DEErrorStrings errorStrings = (DEErrorStrings) GWT
			.create(DEErrorStrings.class);

	public JobStatusPanel(String caption, String idWorkspace) {
		super();
		this.caption = caption;
		this.workspaceId = idWorkspace;
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
		grid.setTitle(displayStrings.jobPanelToolTip());

		this.setHeading(caption);
		this.setTopComponent(buildGridToolBar());
		this.add(grid);
	}

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
		btnStart.setIcon(Resources.ICONS.play());
		btnStart.setHeight(23);
		btnStart.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (grid.getSelectionModel().getSelectedItems().size() > 0) {
					if (JobStatusPanel.JOB_STATUS.READY.toString()
							.equalsIgnoreCase(
									grid.getSelectionModel().getSelectedItem()
											.get("status").toString())) {
						startJob(grid.getSelectionModel().getSelectedItem()
								.get("id").toString());
					} else {
						ErrorHandler.post(displayStrings.jobCannotRun());
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

		btnDownload = new Button("Download Result");
		btnDownload.setIcon(Resources.ICONS.download());
		btnDownload.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				downloadResult();
			}

		});

		t.add(btnStart);
		t.add(btnDelete);
		t.add(btnRefresh);
		t.add(new FillToolItem());
		t.add(btnDownload);
		return t;
	}

	private void downloadResult() {
		if (grid.getSelectionModel().getSelectedItems().size() <= 0
				|| grid.getSelectionModel().getSelectedItem().get("status")
						.equals(JobStatusPanel.JOB_STATUS.READY)
				|| grid.getSelectionModel().getSelectedItem().get("status")
						.equals(JobStatusPanel.JOB_STATUS.COMPLETED)) {
			MessageBox.alert("Alert", displayStrings.downloadResult(), null);
		} else {
			downloadResult(grid.getSelectionModel().getSelectedItem().get("name").toString());
		}
	}

	private void startJob(final String jobid) {

		JobServices.runContrastJob(jobid, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				org.iplantc.de.client.ErrorHandler.post(errorStrings
						.runJobError());
			}

			@Override
			public void onSuccess(String result) {
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
				org.iplantc.de.client.ErrorHandler.post(errorStrings
						.deleteJobError());

			}

			@Override
			public void onSuccess(String result) {
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
	private final native JsArray<JsJob> asArrayofJobData(String json) /*-{
		return eval(json);
	}-*/;

	private void getContrastJobs() {
		JobServices.getContrastJobs(workspaceId, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				JsArray<JsJob> jobinfos = asArrayofJobData(result);
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
				org.iplantc.de.client.ErrorHandler.post(errorStrings
						.getJobsError());

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
			JobServices.getContrastJobs(workspaceId,
					new AsyncCallback<String>() {

						@Override
						public void onSuccess(String result) {
							JsArray<JsJob> jobinfos = asArrayofJobData(result);
							Job j = null;
							ArrayList<Job> jobs = new ArrayList<Job>();
							Date d = null;
							for (int i = 0; i < jobinfos.length(); i++) {
								if (jobid.equals(jobinfos.get(i).getId()
										.toString())) {
									if (jobinfos.get(i).getStatus().equals(
											JobStatusPanel.JOB_STATUS.ERROR.toString())
											|| jobinfos
													.get(i)
													.getStatus()
													.equals(
															JobStatusPanel.JOB_STATUS.COMPLETED.toString())) {
										cancelTimer();
										pushResultFiletoWorkSpace(jobinfos.get(
												i).getName());
									}
										d = new Date(Long.parseLong(jobinfos.get(i)
												.getCreationDate()));
										j = new Job(jobinfos.get(i).getId(), jobinfos
												.get(i).getName(), d.toString(),
												jobinfos.get(i).getStatus());

										jobs.add(j);
										break;
								}

								// Window.alert("got update for job");

							}

							if (jobs.size() > 0) {
								updateJobStatus(jobs);
							}

						}

						@Override
						public void onFailure(Throwable caught) {
							org.iplantc.de.client.ErrorHandler
									.post(errorStrings.getJobsError());
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
	 *
	 * @author sriram
	 *
	 */
	class JobGridViewConfig extends GridViewConfig {
		@Override
		public String getRowStyle(ModelData model, int rowIndex,
				ListStore<ModelData> ls) {
	    	if (model.get("status").equals(JobStatusPanel.JOB_STATUS.ERROR.toString())) {
				return "iplantc-job-error-status";
			} else {
				return "iplantc-job-panel";
			}
		}
	}

	private void pushResultFiletoWorkSpace(final String jobname) {
		FolderServices.getListofFiles(workspaceId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());

			}

			@Override
			public void onSuccess(String result) {
				String successFileName = jobname + ".txt";
				String errorFileName = jobname + ".err";
				JsArray<JsFile> fileinfos = JsonUtil.asArrayOf(result);
				JsFile info = null;
				for (int i = 0; i < fileinfos.length(); i++) {
					info = fileinfos.get(i);
					if (info.getName().equals(successFileName)
							|| info.getName().equals(errorFileName)) {
						FileUploadedEvent event = new FileUploadedEvent("",
								info,null);
						EventBus.getInstance().fireEvent(event);
						break;
					}
				}
			}

		});
	}

	private void downloadResult(final String jobname) {
		FolderServices.getListofFiles(workspaceId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				ErrorHandler.post(errorStrings.retrieveFiletreeFailed());

			}

			@Override
			public void onSuccess(String result) {
				String successFileName = jobname + ".txt";
				String errorFileName = jobname + ".err";
				JsArray<JsFile> fileinfos = JsonUtil.asArrayOf(result);
				JsFile info = null;
				String address = null;
				for (int i = 0; i < fileinfos.length(); i++) {
					info = fileinfos.get(i);
					if (info.getName().equals(successFileName)
							|| info.getName().equals(errorFileName)) {
						//address = "http://" + Window.Location.getHostName() + ":14444/files/" + info.getId() + "/content";
						address = Window.Location.getProtocol() + "://" + Window.Location.getHost()
				        + Window.Location.getPath() + "files/" + info.getId() + "/content.gdwnld";
						break;
					}
				}

				if(address != null){
					Window.open(address,null,"width=100,height=100");
				} else {
					// TODO displayStrings is not where error messages go, move to errorStrings
					ErrorHandler.post(errorStrings.downloadResultError());
				}
			}

		});
	}

}
