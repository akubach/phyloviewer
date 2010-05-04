package org.iplantc.de.server;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import edu.emory.mathcs.backport.java.util.Collections;

public class ExtractUploadFileInfoTransformer extends AbstractTransformer {
	private static ExtractFileInfoTransformer fileInfoTransformer = new ExtractFileInfoTransformer();
	
	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		Object[] fileResults = (Object[]) arg0;
		Object createdFile = fileResults[0];
		Object deletedFile = fileResults[1];
		Map<String, Object> result = new HashMap<String, Object>();
		if (createdFile != null) {
			result.put("created", fileInfoTransformer.transform(createdFile));
		}
		if (deletedFile != null) {
			if (deletedFile instanceof Long) {
				result.put("deletedIds", Collections.singletonList(deletedFile.toString()));				
			}
		}
		return result;
	}

}
