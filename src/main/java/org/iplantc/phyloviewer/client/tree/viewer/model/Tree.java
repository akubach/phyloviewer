package org.iplantc.phyloviewer.client.tree.viewer.model;


public class Tree {

	private Node _root = null;
	
	public Tree() {
	}
	
	public void setRootNode ( Node node ) {
		_root = node;
	}
	
	public Node getRootNode() {
		return _root;
	}
}
