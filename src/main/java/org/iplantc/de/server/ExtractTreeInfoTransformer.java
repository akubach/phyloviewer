package org.iplantc.de.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.iplantc.treedata.model.File;
import org.iplantc.treedata.model.Tree;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

public class ExtractTreeInfoTransformer extends AbstractTransformer
{

	@Override
	protected Object doTransform(Object arg0, String arg1) throws TransformerException
	{
		Collection<Tree> trees;
		if(arg0 instanceof Collection<?>)
		{
			trees = (Collection<Tree>)arg0;
		}
		else if(arg0 instanceof File)
		{
			trees = ((File)arg0).getTrees();
		}
		else
		{
			throw new TransformerException(MessageFactory
					.createStaticMessage("Received object that was not a File or list of Trees"));
		}
		List<TreeInfo> treeInfos = new LinkedList<TreeInfo>();

		for(Tree tree : trees)
		{
			TreeInfo treeInfo = new TreeInfo();
			treeInfo.setId(tree.getId() == null ? null : tree.getId().toString());
			treeInfo.setFilename(tree.getFile().getName());
			treeInfo.setTreeName(tree.getLabel());
			treeInfo.setUploaded(tree.getFile().getUploaded().toString());
			treeInfos.add(treeInfo);
		}

		return treeInfos;
	}

}
