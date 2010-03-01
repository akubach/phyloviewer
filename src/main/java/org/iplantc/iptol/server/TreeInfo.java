package org.iplantc.iptol.server;

public class TreeInfo {
	private String filename;
	private String uploaded;
	private String treeName;
	private String id;

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}

	public String getUploaded() {
		return uploaded;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public String getTreeName() {
		return treeName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
