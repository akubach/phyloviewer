package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.List;

/**
 * A MatrixRow represents a single row in a Trait Table.
 * Each row contains the name of the taxon and an ordered list of trait values
 * stored as strings.  The id of the taxon is also provided so that the row
 * can be uniquely identified.  For example, in the future, a user may wish
 * to modify and/or delete a row by specifying its taxon id.
 *
 * @author Donald A. Barre
 */
public class MatrixRow {

	private String id;  // taxon id
	private List<String> values = new ArrayList<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getValues() {
		return values;
	}

	public void addValue(String value) {
		values.add(value);
	}
}
