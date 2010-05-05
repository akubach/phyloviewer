package org.iplantc.de.client.jobs;

import java.util.ArrayList;
import java.util.Date;

import org.iplantc.de.client.DEErrorStrings;
import org.iplantc.de.client.EventBus;
import org.iplantc.de.client.events.JobSavedEvent;
import org.iplantc.de.client.models.Job;
import org.iplantc.de.client.models.JsJob;
import org.iplantc.de.client.services.JobServices;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author sriram Save job to DE
 *
 */
public class SaveJob {

	private String jobName;
	private String params;
	private String workspaceId;
	private DEErrorStrings errorStrings = (DEErrorStrings) GWT.create(DEErrorStrings.class);
	
	
	public SaveJob(String jobname, String jsonParams, String workspaceId) {
		setParams(jsonParams);
		setJobName(jobname);
		setWorkspaceId(workspaceId);
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getParams() {
		return params;
	}

	public void setJobName(String jobname) {
		this.jobName = jobname;
	}

	public String getJobName() {
		return jobName;
	}
	
	public void save() {
		
	 JobServices.saveContrastJob(this.getParams(),workspaceId ,new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				StringBuffer sb = new StringBuffer();
				sb.append("[" + result + "]");
				JsArray<JsJob> jobinfos = asArrayofJobData(sb.toString());
				Job j = null;
				Date d  = null;
				ArrayList<Job> jobs = new ArrayList<Job>();
				for (int i=0;i<jobinfos.length();i++) {
					d = new Date (Long.parseLong(jobinfos.get(i).getCreationDate()));
					j = new Job(jobinfos.get(i).getId(), jobinfos.get(i).getName(), d.toString(), jobinfos.get(i).getStatus());
					jobs.add(j);
				}
				
				EventBus eventbus = EventBus.getInstance();
				JobSavedEvent jse = new JobSavedEvent(jobs);
				eventbus.fireEvent(jse);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				org.iplantc.de.client.ErrorHandler.post(errorStrings.saveJobError());
				
			}
		});
		
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

	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	public String getWorkspaceId() {
		return workspaceId;
	}
	
}
