package org.iplantc.iptol.server;

public class MockFileUploadedEvent implements FileUploadedEvent {

	public FileInfo fileUploaded(String data, String filename) throws UploadException {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("foo");
		fileInfo.setType("type");
		fileInfo.setUploaded("now!");
		fileInfo.setId(1L);
		return fileInfo;
	}

}
