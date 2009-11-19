package org.iplantc.iptol.server;

import java.util.Collections;
import java.util.List;

public class MockFileUploadedEvent implements FileUploadedEvent {

	public List<TreeInfo> fileUploaded(String data, String filename) throws UploadException {
		return Collections.emptyList();
	}

}
