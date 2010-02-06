package org.iplantc.iptol.server;

public class MatrixInfo {
	private String filename;
	private String uploaded;
	private Long id;

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

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
