package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;
import java.util.Date;

import org.iplantc.iptol.client.EventBus;
import org.iplantc.iptol.client.IptolErrorStrings;
import org.iplantc.iptol.client.events.JobSavedEvent;
import org.iplantc.iptol.client.services.JobServices;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
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
	private IptolErrorStrings errorStrings = (IptolErrorStrings) GWT.create(IptolErrorStrings.class);
	
	
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
				JsArray<JobInfo> jobinfos = asArrayofJobData(sb.toString());
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
				org.iplantc.iptol.client.ErrorHandler.post(errorStrings.saveJobError());
				
			}
		});
		
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

	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	public String getWorkspaceId() {
		return workspaceId;
	}
	
}
