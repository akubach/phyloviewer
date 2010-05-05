package org.iplantc.de.client.jobs;

import java.util.ArrayList;

import org.iplantc.de.client.models.JobStep;

import com.extjs.gxt.ui.client.widget.ContentPanel;

/**
 * 
 * @author sriram
 * 
 */
public interface JobView {

	public ContentPanel getWizard();

	public void setJobStep(int step);

	public ArrayList<JobStep> getJobConfigSteps();

}
