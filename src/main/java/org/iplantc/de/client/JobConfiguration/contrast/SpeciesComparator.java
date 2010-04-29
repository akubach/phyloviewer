package org.iplantc.de.client.JobConfiguration.contrast;

import java.util.Comparator;

public class SpeciesComparator implements Comparator<Species> {

	@Override
	public int compare(Species o1, Species o2) {
		return o1.get("name").toString().compareToIgnoreCase(
				o2.get("name").toString());
	}

}
