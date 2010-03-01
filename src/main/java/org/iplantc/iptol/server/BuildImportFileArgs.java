package org.iplantc.iptol.server;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Builds the arguments for the ImportFile service.
 * @author Donald A. Barre
 */
public class BuildImportFileArgs extends AbstractMessageAwareTransformer {

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		ImportFileInfo importFileInfo = (ImportFileInfo) message.getPayload();
		Object newPayload[];
		if (importFileInfo.getFolderId() == null) {
		    newPayload = new Object[2];
		    newPayload[0] = importFileInfo.getFileContents();
		    newPayload[1] = importFileInfo.getFileName();
		}
		else {
			newPayload = new Object[3];
            newPayload[0] = importFileInfo.getFileContents();
			newPayload[1] = importFileInfo.getFileName();
			newPayload[2] = Long.valueOf(importFileInfo.getFolderId());
		}

        return newPayload;
	}
}
