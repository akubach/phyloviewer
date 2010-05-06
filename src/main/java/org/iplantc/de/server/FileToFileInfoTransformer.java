package org.iplantc.de.server;

import org.iplantc.treedata.info.FileInfo;
import org.iplantc.treedata.model.File;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

/**
 * Transform from a model File object to a data transfer object, FileInfo. 
 */
public class FileToFileInfoTransformer extends AbstractTransformer
{

	@Override
	protected Object doTransform(Object source, String encoding) throws TransformerException
	{
		File f = null;
		if(source instanceof File)
		{
			f = (File)source;
		}
		else
		{
			throw new TransformerException(MessageFactory
					.createStaticMessage("Received object that was not a File or list of Files"));
		}

		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(f.getId() == null ? null : f.getId().toString());
		fileInfo.setName(f.getName());
		fileInfo.setUploaded(f.getUploaded() == null ? "" : f.getUploaded().toString());
		fileInfo.setType(f.getType() == null ? "" : f.getType().getDescription());
		fileInfo.setStatus(f.getStatus() == null ? "" : f.getStatus().name());

		return fileInfo;
	}

}
