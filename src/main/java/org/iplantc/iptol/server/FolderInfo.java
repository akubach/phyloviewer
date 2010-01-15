package org.iplantc.iptol.server;

import java.util.LinkedList;
import java.util.List;

public class FolderInfo {
	private Long id;
	private String label;
	private List<FolderInfo> subfolders = new LinkedList<FolderInfo>();
	private List<FileInfo> files = new LinkedList<FileInfo>();
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setSubfolders(List<FolderInfo> subfolders) {
		this.subfolders = subfolders;
	}
	public List<FolderInfo> getSubfolders() {
		return subfolders;
	}
	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}
	public List<FileInfo> getFiles() {
		return files;
	}
}
