package org.iplantc.iptol.client.JobConfiguration;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class Trait extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Trait(String filename, String traitblock, String uploaded) {
		set("filename", filename);
		set("traitblock", traitblock);
		set("uploaded", uploaded);
	}

	public String getFilename() {
		return get("filename").toString();
	}

	public String getTraitblock() {
		return get("traitblock").toString();
	}

	public String getUploaded() {
		return get("uploded").toString();
	}
}
