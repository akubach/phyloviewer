package org.iplantc.iptol.server;

import java.util.List;

public interface FileUploadedEvent {
	List<FileInfo> fileUploaded(String data, String filename) throws UploadException;
}
