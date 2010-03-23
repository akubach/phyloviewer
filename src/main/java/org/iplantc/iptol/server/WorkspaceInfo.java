package org.iplantc.iptol.server;

public class WorkspaceInfo {

	private String id;
	private String uploadFolderId;
	private FolderInfo homeFolder;

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

	public void setHomeFolder(FolderInfo homeFolder) {
		this.homeFolder = homeFolder;
	}

	public FolderInfo getHomeFolder() {
		return homeFolder;
	}
}
