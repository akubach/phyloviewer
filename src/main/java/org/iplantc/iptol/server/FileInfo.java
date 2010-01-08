package org.iplantc.iptol.server;

import java.util.Date;

/** 
 * A data transfer object for File domain object. 
 * 
 * @see org.iplantc.treedata.model.File
 */
public class FileInfo {
	private String name; 
	private Date uploaded; 
	private Long id; 
	private String type;
	
	// The following are foreign keys and will likely change to objects or 
	// collections in the near future. 
	//private Long group; 
	//private Long type; 

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getUploaded() {
		return uploaded;
	}
	public void setUploaded(Date uploaded) {
		this.uploaded = uploaded;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}	
}
