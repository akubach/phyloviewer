package org.iplantc.de.client.jobs.contrast;

import java.util.Comparator;

import org.iplantc.de.client.models.Species;

/**
 * Defines comparator for evaluating Species.
 * 
 * @see org.iplantc.de.client.models.Species
 */
public class SpeciesComparator implements Comparator<Species>
{

	@Override
	public int compare(Species o1, Species o2)
	{
		return o1.get("name").toString().compareToIgnoreCase(o2.get("name").toString());
	}

}
