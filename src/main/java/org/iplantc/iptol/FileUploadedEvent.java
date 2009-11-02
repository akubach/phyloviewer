package org.iplantc.iptol;

public interface FileUploadedEvent {
	void fileUploaded(byte data[]) throws UploadException;
}
