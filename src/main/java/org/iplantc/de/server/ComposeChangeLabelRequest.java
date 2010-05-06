package org.iplantc.de.server;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class ComposeChangeLabelRequest extends AbstractMessageAwareTransformer
{

	Logger log = Logger.getLogger(ComposeChangeLabelRequest.class);

	@Override
	public Object transform(MuleMessage message, String encoding) throws TransformerException
	{
		Object request[] = new Object[2];

		request[0] = Long.valueOf(message.getStringProperty("id", ""));
		request[1] = ((JSONObject)message.getPayload()).get("label");

		return request;
	}
}
