package org.iplantc.iptol.server;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.Tree;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class ExtractTreeInfoTransformer extends AbstractTransformer {

	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		File file = (File)arg0;
		List<TreeInfo> trees = new LinkedList<TreeInfo>();
		
		for (Tree tree : file.getTrees()) {
			TreeInfo treeInfo = new TreeInfo();
			treeInfo.setId(tree.getId());
			treeInfo.setFilename(file.getName());
			treeInfo.setTreeName(tree.getLabel());
			treeInfo.setUploaded(file.getUploaded());
			trees.add(treeInfo);
		}
		
		return trees;
	}

}
