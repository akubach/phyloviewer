/**
 * 
 */
package org.iplantc.iptol.client.JobConfiguration;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author sriram
 * 
 */
public class Tree extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Tree(String filename, String treename, String uploaded) {
		set("filename", filename);
		set("treename", treename);
		set("uploaded", uploaded);
	}

	public String getFilename() {
		return get("filename").toString();
	}

	public String getTreename() {
		return get("treename").toString();
	}

	public String getUploaded() {
		return get("uploded").toString();
	}
}
