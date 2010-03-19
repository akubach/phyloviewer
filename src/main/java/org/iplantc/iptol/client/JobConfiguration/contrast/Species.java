package org.iplantc.iptol.client.JobConfiguration.contrast;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class Species extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Species(String id, String species) {
		this.set("id", id);
		this.set("name", species);
	}

	public String getId() {
		return this.get("id").toString();
	}

	public String getName() {
		return this.get("name").toString();

	}

	// for reconcling, if they have same spelling , they are same
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		Species s = (Species) obj;
		return this.getName().equalsIgnoreCase(s.getName());
	}

	public int hashCode() {
		// select primes for hashing
		return 31 * 7 + this.getName().hashCode();
	}
}
