package org.iplantc.iptol.server;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Builds the arguments for the RenameFolder service.
 * @author Donald A. Barre
 */
public class BuildSetMatrixArgs extends AbstractMessageAwareTransformer {

	private static final String MATRIX_ID = "matrixId";
	private static final String INVALID_ID = "-1";

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
	 */
	@Override
	public Object transform(MuleMessage message, String encoding)
			throws TransformerException {

		Object newPayload[] = new Object[2];

		newPayload[0] = Long.valueOf(message.getStringProperty(MATRIX_ID, INVALID_ID));
		newPayload[1] = message.getPayload();

        return newPayload;
	}
}
