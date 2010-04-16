package org.iplantc.iptol.server;

import java.util.List;

import org.iplantc.treedata.info.FileInfo;

public interface FileUploadedEvent {
	List<FileInfo> fileUploaded(String data, String filename) throws UploadException;
}
