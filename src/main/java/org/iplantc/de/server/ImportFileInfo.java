package org.iplantc.de.server;

/**
 * The ImportFileInfo contains the arguments for uploading data files.  It must
 * contian the file's name and it's contents.  The folderId is optional.  If
 * specified, the file is uploaded into the corresponding folder.  If not specified,
 * the default upload folder is used.
 * @author Donald A. Barre
 */
public class ImportFileInfo {

	private String fileName;
	private String fileContents;
	private String folderId;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContents() {
		return fileContents;
	}

	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
}
