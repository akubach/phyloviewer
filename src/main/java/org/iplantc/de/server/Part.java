package org.iplantc.de.server;

/**
 * A FilePart represents one file that comes from an HTTP multipart body.
 * @author Donald A. Barre
 */
public class Part {

	private String name;
	private String contents;

	public Part(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
