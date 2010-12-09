package org.iplantc.recon.server;

import java.util.Comparator;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.render.style.IStyle;

public class NodeWrapper implements INode {

	org.iplantc.phyloparser.model.Node node;
	
	public NodeWrapper(org.iplantc.phyloparser.model.Node node) {
		this.node = node;
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNumberOfChildren() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public INode[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INode getChild(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isLeaf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfLeafNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int findMaximumDepthToLeaf() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String findLabelOfFirstLeafNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sortChildrenBy(Comparator<INode> comparator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getData(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setData(String string, Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStyleId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStyle getStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
