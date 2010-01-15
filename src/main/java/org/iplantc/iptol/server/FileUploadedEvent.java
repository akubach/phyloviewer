package org.iplantc.iptol.server;

public interface FileUploadedEvent {
	FileInfo fileUploaded(String data, String filename) throws UploadException;
}
