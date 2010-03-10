package org.iplantc.iptol.client.JobConfiguration;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class Species extends BaseModelData {

	public Species(String species) {
		this.set("name", species);
	}

	public String getName() {
		return this.get("name").toString();

	}

}
