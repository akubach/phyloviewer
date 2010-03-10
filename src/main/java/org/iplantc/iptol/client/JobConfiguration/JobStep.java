package org.iplantc.iptol.client.JobConfiguration;

import com.extjs.gxt.ui.client.data.BaseModel;

public class JobStep extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5835114258342501112L;

	// constructor
	public JobStep(int stepno, String name) {
		set("step", stepno);
		set("name", name);
	}

	public String getName() {
		return get("name");
	}

	public int getStep() {
		int ret = (Integer)get("step"); 
		return ret;
	}
}