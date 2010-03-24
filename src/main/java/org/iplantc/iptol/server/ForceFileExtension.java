package org.iplantc.iptol.server;

import org.iplantc.phyloparser.model.FileData;
import org.iplantc.treedata.model.File;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class ForceFileExtension extends AbstractTransformer {
	private String extension;
	
	public ForceFileExtension() {
		registerSourceType(File.class);
		setReturnClass(File.class);
	}
	
	@Override
	protected Object doTransform(Object obj, String encoding)
			throws TransformerException {
		File file = (File)obj;
		if (!file.getName().toLowerCase().endsWith(extension)) {
			file.setName(file.getName() + extension);
		}
		return file;
	}

	public void setExtension(String extension) {
		this.extension = "." + extension;
	}

	public String getExtension() {
		return extension.substring(1);
	}
}
