/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

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
