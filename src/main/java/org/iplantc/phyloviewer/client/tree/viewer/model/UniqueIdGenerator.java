package org.iplantc.phyloviewer.client.tree.viewer.model;

public class UniqueIdGenerator {

	private static UniqueIdGenerator instance = new UniqueIdGenerator();
	static int nextId=0;
	
	static UniqueIdGenerator getInstance() {
		return instance;
	}
	
	int getNextId() {
		return nextId++;
	}
}
