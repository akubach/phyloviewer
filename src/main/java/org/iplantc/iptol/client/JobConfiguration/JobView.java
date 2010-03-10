package org.iplantc.iptol.client.JobConfiguration;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public interface JobView {

	public ContentPanel getWizard();
	
	public void setStep(int step);
	
	public ArrayList<JobStep> getJobConfigSteps();
	
}
