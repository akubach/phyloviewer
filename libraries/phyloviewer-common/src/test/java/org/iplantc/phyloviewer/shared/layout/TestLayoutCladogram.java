package org.iplantc.phyloviewer.shared.layout;

import org.iplantc.phyloviewer.shared.model.Node;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.junit.Test;

import junit.framework.TestCase;

public class TestLayoutCladogram extends TestCase {

	Tree tree;
	Node root;
	
	@Override
	protected void setUp() {
		root = new Node();
		root.setId(0);
		
		Node a = new Node();
		a.setBranchLength(1.0);
		a.setId(1);
		
		Node b = new Node();
		b.setBranchLength(2.0);
		b.setId(2);
		
		root.addChild(a);
		root.addChild(b);
		
		tree = new Tree();
		tree.setRootNode(root);
	}
	
	@Test
	public void testLayoutDefault() {
		LayoutCladogram layout = new LayoutCladogram();
		layout.layout(tree);
		
		assertEquals(0.0,layout.getPosition(0).getX());
		assertEquals(0.5,layout.getPosition(0).getY());
		
		assertEquals(0.8,layout.getPosition(1).getX());
		assertEquals(0.75,layout.getPosition(1).getY());
		
		assertEquals(0.8,layout.getPosition(2).getX());
		assertEquals(0.25,layout.getPosition(2).getY());
	}
	
	@Test
	public void testLayoutBranchLengths() {
		LayoutCladogram layout = new LayoutCladogram();
		layout.setUseBranchLengths(true);
		layout.layout(tree);
		
		assertEquals(0.0,layout.getPosition(0).getX());
		assertEquals(0.5,layout.getPosition(0).getY());
		
		assertEquals(0.4,layout.getPosition(1).getX());
		assertEquals(0.75,layout.getPosition(1).getY());
		
		assertEquals(0.8,layout.getPosition(2).getX());
		assertEquals(0.25,layout.getPosition(2).getY());
	}
}
