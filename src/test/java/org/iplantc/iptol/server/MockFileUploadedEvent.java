package org.iplantc.iptol.server;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

public class MockFileUploadedEvent implements FileUploadedEvent {

	public List<FileInfo> fileUploaded(String data, String filename) throws UploadException {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("foo");
		fileInfo.setType("type");
		fileInfo.setUploaded("now!");
		fileInfo.setId("1");
		return Collections.singletonList(fileInfo);
	}

}
