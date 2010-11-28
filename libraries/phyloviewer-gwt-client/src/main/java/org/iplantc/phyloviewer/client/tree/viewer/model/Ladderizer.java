/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.model;

import org.iplantc.phyloviewer.shared.model.INode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class Ladderizer {
	private Map<INode, Integer> subtreeSizes = new HashMap<INode, Integer>();
	private LadderizeComparator comparator = new LadderizeComparator();
	private Direction direction;
	
	public Ladderizer(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Ladderizes the children of a node 
	 * @return the number of nodes in this subtree
	 */
	public int ladderize(INode node) {
		
		int size = 1;
		
		for (int i = 0; i < node.getNumberOfChildren(); i++) {
			INode child = node.getChild(i);
			int subtreeSize = ladderize(child);
			subtreeSizes.put(child, subtreeSize);
			size += subtreeSize;
		}
		
		node.sortChildrenBy(comparator);
		
		return size;
	}
	
	public enum Direction { 
		UP(1), DOWN(-1); 
		private int value;
		private Direction(int value) {
			this.value = value;
		}
	}
	
	private class LadderizeComparator implements Comparator<INode> {
		@Override
		public int compare(INode node0, INode node1) {
			if (!subtreeSizes.containsKey(node0) || !subtreeSizes.containsKey(node1)) {
				throw new NullPointerException("subtreeSizes does not contain both nodes " + node0 + " and " + node1 + ". The child subtree sized must be calculated before they can be reordered.");
			}
			return direction.value * (subtreeSizes.get(node0) - subtreeSizes.get(node1));
		}
	}

}
