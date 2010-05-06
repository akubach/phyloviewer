package org.iplantc.de.server;

import org.iplantc.treedata.command.MultiResourceDeleteCmd;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Sets the workspace ID in the MultiResourceDeleteCmd.
 * 
 * @author Donald A. Barre
 */
public class SetWorkspaceId extends AbstractMessageAwareTransformer
{

	private static final String WORKSPACE_ID = "workspaceId";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage
	 * , java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding) throws TransformerException
	{

		MultiResourceDeleteCmd cmd = (MultiResourceDeleteCmd)message.getPayload();
		String workspaceId = message.getStringProperty(WORKSPACE_ID, null);
		if(workspaceId != null)
		{
			cmd.setWorkspaceId(Long.valueOf(workspaceId));
		}

		return cmd;
	}
}
