package org.iplantc.iptol.server;

import java.util.ArrayList;
import java.util.List;

public class MatrixRow {

	private Long id;  // taxon id
	private List<String> values = new ArrayList<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getValues() {
		return values;
	}

	public void addValue(String value) {
		values.add(value);
	}
}
