package org.iplantc.iptol.server;

import org.apache.log4j.Logger;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * When a file is uploaded, the HTTP body, i.e. payload, contains
 * @author Donald A. Barre
 */
public class ParseUploadPayload extends AbstractMessageAwareTransformer {

	private static final String WORKSPACE_ID = "workspaceId";
	private static final String FOLDER_ID = "folderId";
	private static final String INVALID_ID = null;

	private static final Logger LOG = Logger.getLogger(ParseUploadPayload.class);

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		if (!(message.getPayload() instanceof String)) {
			throw new TransformerException(MessageFactory.createStaticMessage("Upload Payload is not a string"));
		}

		String contentType = (String) message.getProperty("Content-Type");
		if (contentType == null) {
			throw new TransformerException(MessageFactory.createStaticMessage("No content-type in multi-part upload"));
		}

		LOG.debug("Content-Type is " + contentType);
		LOG.debug((String) message.getPayload());

		MultiPartParser parser = new MultiPartParser(contentType, (String) message.getPayload());
		FilePart filePart = parser.readNextPart();
		LOG.debug("***** FILE CONTENTS ***");
		LOG.debug(filePart.getContents());

		/*
		 * The folder is optional.  If not specified, the default upload folder is used.
		 */
		Long folderId = null;
		if (message.getProperty(FOLDER_ID) != null) {
			folderId = Long.valueOf(message.getStringProperty(FOLDER_ID, INVALID_ID));
		}

		/*
		 * Construct the new payload (file contents, filename, workspaceId, optional folderId).
		 */
		Object newPayload[] = new Object[folderId == null ? 3 : 4];
		newPayload[0] = filePart.getContents();
		newPayload[1] = filePart.getFilename();
		newPayload[2] = Long.valueOf(message.getStringProperty(WORKSPACE_ID, INVALID_ID));
		if (folderId != null) {
			newPayload[3] = folderId;
		}

		return newPayload;
	}
}
