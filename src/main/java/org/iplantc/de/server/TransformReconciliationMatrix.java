package org.iplantc.de.server;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.iplantc.runcontrast.ContrastJobConfiguration;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class TransformReconciliationMatrix extends AbstractTransformer {

	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		JSONObject obj = (JSONObject)arg0;
		ContrastJobConfiguration cjc = (ContrastJobConfiguration) JSONObject.toBean(obj, ContrastJobConfiguration.class);
		Map<Long, Long> reconcileMatrix = new HashMap<Long, Long>();

		if (obj.get("reconciliation") != null) {
			Map oldReconcileMatrix = (Map) obj.get("reconciliation");
			for (Object keyObj : oldReconcileMatrix.keySet()) {
				String key = (String)keyObj;
				Long longKey = Long.valueOf(key);
				Long longValue = Long.valueOf((String)oldReconcileMatrix.get(key));
				reconcileMatrix.put(longKey, longValue);
			}
		}
		cjc.setReconciliation(reconcileMatrix);
		return cjc;
	}

}
