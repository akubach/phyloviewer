package org.iplantc.iptol.server;

/**
 * A data transfer object for File domain object.
 *
 * @see org.iplantc.treedata.model.File
 */
public class FileInfo {
	private String name;
	private String uploaded;
	private String id;
	private String type;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUploaded() {
		return uploaded;
	}
	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
}
