package org.iplantc.de.server;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Determine if this is a save or a saveAs and create the payload based upon that
 * decision.
 * 
 * @author Donald A. Barre
 */
public class BuildPayloadForSaveOrSaveAs extends AbstractMessageAwareTransformer
{

	private static final String DE_SAVE_FILE_METHOD = "DE_SAVE_FILE_METHOD";
	private static final String SAVE_METHOD = "save";
	private static final String SAVE_AS_METHOD = "saveAs";

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

		Object[] payload = null;
		Object[] args = (Object[])message.getPayload();

		Long fileId = null;
		if(args[0] instanceof Long)
		{
			fileId = (Long)args[0];
		}
		Long copiedFromFileId = (Long)args[3];
		if(fileId != null && fileId.equals(copiedFromFileId))
		{
			payload = new Object[2];
			payload[0] = args[3];
			payload[1] = args[1];
			message.setStringProperty(DE_SAVE_FILE_METHOD, SAVE_METHOD);
		}
		else
		{
			Long folderId = (args.length == 6) ? (Long)args[5] : null;
			if(folderId == null)
			{
				payload = new Object[4];
				payload[0] = args[1];
				payload[1] = args[2];
				payload[2] = args[3];
				payload[3] = args[4];
			}
			else
			{
				payload = new Object[5];
				payload[0] = args[1];
				payload[1] = args[2];
				payload[2] = args[3];
				payload[3] = args[4];
				payload[4] = args[5];
			}
			message.setStringProperty(DE_SAVE_FILE_METHOD, SAVE_AS_METHOD);
		}
		return payload;
	}
}
