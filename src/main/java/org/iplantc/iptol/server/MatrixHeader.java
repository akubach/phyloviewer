package org.iplantc.iptol.server;

/**
 * A Matrix Header represents the header for a single column in a trait
 * table.  Each column is identified by a name and an id.  The first column in
 * a trait table is always the Species column.  The remaining columns are
 * the traits (characters).  Each trait column has a name and an id that
 * identifies the trait.  The id is necessary for future operations, e.g.
 * a user wishes to delete an entire column identified by the trait id.
 *
 * @author Donald A. Barre
 */
public class MatrixHeader {

	private String id;
	private String label;

	public MatrixHeader(Long id, String label) {
		this.id = (id == null) ? null : id.toString();
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
