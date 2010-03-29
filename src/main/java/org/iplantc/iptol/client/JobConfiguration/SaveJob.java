package org.iplantc.iptol.client.JobConfiguration;

import org.iplantc.iptol.client.services.JobServices;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
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
	
	public SaveJob(String jobname, String jsonParams) {
		setParams(jsonParams);
		setJobName(jobname);
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
		
		JobServices.saveJob(this.getParams(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
			//	System.out.println("Job Saved" + result);
			//	JSONObject obj = JSONParser.parse(result).isObject();
			//	Window.alert("jobid->" + obj.get("id").toString());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
}
