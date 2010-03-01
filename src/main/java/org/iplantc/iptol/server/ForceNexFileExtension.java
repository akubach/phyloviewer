package org.iplantc.iptol.server;

import org.iplantc.phyloparser.model.FileData;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class ForceNexFileExtension extends AbstractTransformer {

	public ForceNexFileExtension() {
		registerSourceType(FileData.class);
		setReturnClass(FileData.class);
	}
	
	@Override
	protected Object doTransform(Object obj, String encoding)
			throws TransformerException {
		FileData fd = (FileData)obj;
		if (!fd.getName().toLowerCase().endsWith(".nex")) {
			fd.setName(fd.getName() + ".nex");
		}
		return fd;
	}
}
