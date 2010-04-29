package org.iplantc.de.server;

/**
 * A FilePart represents one file that comes from an HTTP multipart body.
 * @author Donald A. Barre
 */
public class FilePart extends Part {

	private String filename;

	public FilePart(String name, String filename, String contents) {
		super(name, contents);
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
