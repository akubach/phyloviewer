package org.iplantc.phyloviewer.client.tree.viewer.model;

public class UniqueIdGenerator {

	private static UniqueIdGenerator instance = new UniqueIdGenerator();
	static int nextId=0;
	
	public static UniqueIdGenerator getInstance() {
		return instance;
	}
	
	public void reset() {
		nextId = 0;
	}
	
	public int getNextId() {
		return nextId++;
	}
}
