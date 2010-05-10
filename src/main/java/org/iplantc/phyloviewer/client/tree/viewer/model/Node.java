package org.iplantc.phyloviewer.client.tree.viewer.model;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.phyloviewer.client.tree.viewer.math.Box2D;
import org.iplantc.phyloviewer.client.tree.viewer.math.Vector2;


public class Node {

	private String _label = "";
	private List<Node> _children = new LinkedList<Node>();
	private Vector2 _position = new Vector2();
	private Box2D _boundingBox = new Box2D();

	public Node() {
		
	}
	
	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		_label = label;
	}
	
	public void addChild(Node node) {
		_children.add(node);
	}
	
	public int getNumberOfChildren() {
		return _children.size();
	}
	
	public Node getChild(int index) {
		return _children.get(index);
	}

	public Vector2 getPosition() {
		return _position;
	}

	public void setPosition(Vector2 position) {
		_position = position;
	}
	
	public Box2D getBoundingBox() {
		return _boundingBox;
	}

	public void setBoundingBox(Box2D boundingBox) {
		_boundingBox = boundingBox;
	}

	public Boolean isLeaf() {
		return 0 == _children.size();
	}
	
	public int getNumberOfLeafNodes() {
		int count = 0;
		if (this.isLeaf()) {
			return 1;
		}
		else {
			for ( int i = 0; i < this._children.size(); ++i ) {
				count += this._children.get(i).getNumberOfLeafNodes();
			}
		}
		return count;
	}

	private int _findMaximumDepthToLeafImpl(int currentDepth) {
		int localMaximum = currentDepth;
		if (!this.isLeaf()) {
			for ( int i = 0; i < this._children.size(); ++i ) {
				int depth = this._children.get(i)._findMaximumDepthToLeafImpl ( currentDepth + 1 );

		        if ( depth > localMaximum )
		        {
		          localMaximum = depth;
		        }
			}
		}
		
		return localMaximum;
	}

	public int findMaximumDepthToLeaf() {
		return this._findMaximumDepthToLeafImpl ( 0 );
	}
	
	public String findLabelOfFirstLeafNode() {
		if ( this.isLeaf() ) {
			return this.getLabel();
		}
		
		return this._children.get(0).findLabelOfFirstLeafNode();
	}
}
