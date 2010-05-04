package org.iplantc.de.client.JobConfiguration;

import java.util.HashMap;

/**
 * Represents parameters for a given job.
 * 
 * @author sriram 
 */
public class JobParams {

	public HashMap<String, Object> params;

	/**
	 * create new instance of JobParams
	 */
	public JobParams() {
		params = new HashMap<String, Object>();
	}

	public void add(String key, Object value) {
		params.put(key, value);
	}

	public Object remove(String key) {
		return params.remove(key);
	}

	public Object get(String key) {
		return params.get(key);
	}
	
	public HashMap getParameters() {
		return params;
	}
}
