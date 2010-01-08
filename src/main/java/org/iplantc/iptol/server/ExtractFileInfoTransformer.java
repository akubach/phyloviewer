package org.iplantc.iptol.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.treedata.model.File;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

public class ExtractFileInfoTransformer extends AbstractTransformer {
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object doTransform(Object source, String encoding)
			throws TransformerException {
		Collection<File> files;
		if (source instanceof Collection<?>) {
			files = (Collection<File>)source;
		} else if (source instanceof File) {
			List<File> tmpList = new LinkedList<File>();
			tmpList.add((File)source);
			files = tmpList;
		} else {
			throw new TransformerException(
				MessageFactory.createStaticMessage(
					"Received object that was not a File or list of Files"));
		}		

		List<FileInfo> fileInfos = new LinkedList<FileInfo>();
		
		for (File f : files) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setId(f.getId());
			fileInfo.setName(f.getName());
			fileInfo.setUploaded(f.getUploaded());
			fileInfo.setType(f.getType());
			fileInfos.add(fileInfo);
		}
		
		return fileInfos;
	}

}
