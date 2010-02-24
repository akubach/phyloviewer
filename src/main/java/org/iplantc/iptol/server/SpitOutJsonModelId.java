package org.iplantc.iptol.server;

import org.iplantc.treedata.model.Model;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

/**
 * Spits out the ID of a Model as a JSON string.
 * @author Donald A. Barre
 */
public class SpitOutJsonModelId extends AbstractTransformer {

	@Override
	protected Object doTransform(Object payload, String encoding)
			throws TransformerException {

		if (!(payload instanceof Model)) {
			throw new TransformerException(MessageFactory.createStaticMessage("Not a Model: " + payload.getClass().getName()));
		}

		Model model = (Model) payload;
		return "{\"id\":\"" + model.getId() + "\"}";
	}
}
