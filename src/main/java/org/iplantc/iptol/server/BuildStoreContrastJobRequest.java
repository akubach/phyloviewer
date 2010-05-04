package org.iplantc.iptol.server;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class BuildStoreContrastJobRequest extends AbstractMessageAwareTransformer {

	@Override
	public Object transform(MuleMessage message, String arg1)
			throws TransformerException {
		Object request[] = new Object[2];

		request[0] = Long.valueOf(message.getStringProperty("id", ""));
	    request[1] = message.getPayload();

		return request;
	}

}
