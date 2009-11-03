package org.iplantc.iptol.server;


public interface FileUploadedEvent {
	void fileUploaded(byte data[]) throws UploadException;
}
