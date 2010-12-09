package org.iplantc.recon.server;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.ITree;

public class TreeWrapper implements ITree {

	org.iplantc.phyloparser.model.Tree tree;
	
	public TreeWrapper(org.iplantc.phyloparser.model.Tree tree) {
		this.tree = tree;
	}
	
	@Override
	public void setRootNode(INode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public INode getRootNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
