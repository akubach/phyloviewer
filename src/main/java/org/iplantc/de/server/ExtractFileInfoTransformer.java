package org.iplantc.de.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.treedata.info.FileInfo;
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
			fileInfo.setId(f.getId() == null ? null : f.getId().toString());
			fileInfo.setName(f.getName());
			fileInfo.setUploaded(f.getUploaded() == null ?
					"" : f.getUploaded().toString());
			fileInfo.setType(f.getType() == null ?
					"" : f.getType().getDescription());
			fileInfos.add(fileInfo);
		}

		return fileInfos;
	}

}
