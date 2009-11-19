package org.iplantc.iptol.server;

import java.util.Date;

public class TreeInfo {
	private String filename;
	private Date uploaded;
	private String treeName;
	private Long id;
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setUploaded(Date uploaded) {
		this.uploaded = uploaded;
	}

	public Date getUploaded() {
		return uploaded;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public String getTreeName() {
		return treeName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
}
