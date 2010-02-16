package org.iplantc.iptol.server;

import org.iplantc.treedata.model.Folder;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Builds the arguments for the RenameFolder service.
 * @author Donald A. Barre
 */
public class BuildRenameFolderArgs extends AbstractMessageAwareTransformer {

	private static final String WORKSPACE_ID = "workspaceId";
	private static final String FOLDER_ID = "folderId";
	private static final String INVALID_ID = "-1";

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		Object newPayload[] = new Object[3];

		newPayload[0] = Long.valueOf(message.getStringProperty(WORKSPACE_ID, INVALID_ID));
		newPayload[1] = Long.valueOf(message.getStringProperty(FOLDER_ID, INVALID_ID));

		Folder folder = (Folder) message.getPayload();
		newPayload[2] = folder.getLabel();

        return newPayload;
	}
}
