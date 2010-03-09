package org.iplantc.iptol.server;

/**
 * A FilePart represents one file that comes from an HTTP multipart body.
 * @author Donald A. Barre
 */
public class FilePart {

	private String filename;
	private String contents;

	public FilePart(String filename, String contents) {
		this.filename = filename;
		this.contents = contents;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
