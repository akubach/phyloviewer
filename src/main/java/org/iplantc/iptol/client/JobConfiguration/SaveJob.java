package org.iplantc.iptol.client.JobConfiguration;

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
	
	public boolean save() {

		return true;
	}
	
}
