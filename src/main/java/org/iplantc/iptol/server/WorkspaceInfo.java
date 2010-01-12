package org.iplantc.iptol.server;

public class WorkspaceInfo {
	private Long id;
	private FolderInfo rootFolder;

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setRootFolder(FolderInfo rootFolder) {
		this.rootFolder = rootFolder;
	}
	public FolderInfo getRootFolder() {
		return rootFolder;
	}
}
