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
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JobStatusPanel extends ContentPanel {

		
	private Button btnStart;
	
	private Button btnDelete;
	
	private Grid<Job> grid;

	private ListStore<Job> jobs;
	
	private String caption;
	
	private IptolDisplayStrings displayStrings = (IptolDisplayStrings) GWT
	.create(IptolDisplayStrings.class);
	

	public JobStatusPanel(String caption) 	{	
		super();
		this.caption = caption;
		jobs = new ListStore<Job>();
		assembleView();
	}
	

	public void assembleView() {
		getContrastJobs();
		grid = new Grid<Job>(jobs, buildColumnModel());
		grid.setHeight(300);
		grid.setWidth(550);
		EventBus eventbus = EventBus.getInstance();
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
			
	//////////////////////////////////////////
	private ColumnModel buildColumnModel(){
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("name","Name",150));  			   
		configs.add(new ColumnConfig("created","Date Submitted", 200));
		configs.add(new ColumnConfig("status","Status",150));	
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
				if(grid.getSelectionModel().getSelectedItems().size() > 0) {
				   startJob(grid.getSelectionModel().getSelectedItem().get("id").toString());
				} else {
					MessageBox.alert("Alert", displayStrings.noJobSelected(),null);
				}
			}
		
		});
		
		btnDelete = new Button("Delete");
		btnDelete.setIcon(Resources.ICONS.cancel());
		btnDelete.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if(grid.getSelectionModel().getSelectedItems().size() > 0) {
					   deleteJob(grid.getSelectionModel().getSelectedItem().get("id").toString());
					} else {
						MessageBox.alert("Alert", displayStrings.noJobSelected(),null);
					}
				
			}
		
		});
		t.add(btnStart);
		t.add(btnDelete);
		return t;
	}

	
	private void startJob(String jobid) {
	
		JobServices.runContrastJob(jobid, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error->" + caught.toString());
				
			}

			@Override
			public void onSuccess(String result) {
				Window.alert("success" + result.toString());
				
			}
		});
		
	}
	
	
	private void deleteJob(String jobid) {
		
		JobServices.deleteContrastJob(jobid, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error->" + caught.toString());
				
			}

			@Override
			public void onSuccess(String result) {
				Window.alert("success" + result.toString());
				
			}
		});
		
	}
	
	private void updateStore(ArrayList<Job> jobs) {
		this.jobs.add(jobs);
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
				for (int i=0;i<jobinfos.length();i++) {
					d = new Date (Long.parseLong(jobinfos.get(i).getCreationDate()));
					j = new Job(jobinfos.get(i).getId(), jobinfos.get(i).getName(), d.toString(), jobinfos.get(i).getStatus());
					jobs.add(j);
				}
				setStore(jobs);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	

}
