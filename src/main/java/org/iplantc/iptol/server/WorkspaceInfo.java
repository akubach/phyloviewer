package org.iplantc.iptol.server;

public class WorkspaceInfo {

	private String id;
	private String uploadFolderId;
	private FolderInfo rootFolder;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getUploadFolderId() {
		return uploadFolderId;
	}

	public void setUploadFolderId(String uploadFolderId) {
		this.uploadFolderId = uploadFolderId;
	}

	public void setRootFolder(FolderInfo rootFolder) {
		this.rootFolder = rootFolder;
	}

	public FolderInfo getRootFolder() {
		return rootFolder;
	}
}
