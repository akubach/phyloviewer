package org.iplantc.iptol.server;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class ComposeChangeLabelRequest extends
		AbstractMessageAwareTransformer {

	Logger log = Logger.getLogger(ComposeChangeLabelRequest.class);
	
	@Override
	public Object transform(MuleMessage arg0, String arg1)
			throws TransformerException {
		Object request[] = new Object[2];
		InputStream is = (InputStream)arg0.getPayload();
		request[0] = Long.valueOf(arg0.getStringProperty("id", ""));
		byte[] newLabel = new byte[512];
		int bytesRead = 0, lastRead;
		try {
			do {
				lastRead = is.read(newLabel, bytesRead, 512 - bytesRead);
				if (lastRead != -1) {
					bytesRead += lastRead;
				}
			} while (lastRead != -1 && bytesRead < 512);
		} catch (IOException e) {
			throw new TransformerException(this, e);
		}
		request[1] = new String(newLabel, 0, bytesRead);
		
		return request;
	}

}
