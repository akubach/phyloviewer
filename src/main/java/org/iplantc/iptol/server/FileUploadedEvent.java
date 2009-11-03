package org.iplantc.iptol.server;


public interface FileUploadedEvent {
	void fileUploaded(String data) throws UploadException;
}
