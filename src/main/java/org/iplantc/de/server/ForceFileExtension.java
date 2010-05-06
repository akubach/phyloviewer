package org.iplantc.de.server;

import org.iplantc.treedata.model.File;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class ForceFileExtension extends AbstractTransformer
{
	private String extension;

	public ForceFileExtension()
	{
	}

	@Override
	protected Object doTransform(Object obj, String encoding) throws TransformerException
	{
		File file;
		if(obj instanceof Object[])
		{
			file = (File)((Object[])obj)[0];
		}
		else
		{
			file = (File)obj;
		}
		if(!file.getName().toLowerCase().endsWith(extension))
		{
			file.setName(file.getName() + extension);
		}
		return obj;
	}

	public void setExtension(String extension)
	{
		this.extension = "." + extension;
	}

	public String getExtension()
	{
		return extension.substring(1);
	}
}
