package org.iplantc.de.server;

import org.iplantc.treedata.model.File;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Sets the content disposition in the HTTP Response Header so that the browser
 * will ask the user where they want to save the file.
 * @author Donald A. Barre
 */
public class SetFileAttachment extends AbstractMessageAwareTransformer {

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		File file = (File) message.getPayload();
		if (file != null) {
			message.setProperty("Content-Disposition", "attachment, filename=" + file.getName());
		}

        return message.getPayload();
	}
}
