/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.Tree;

public class JSONBuilder {

	Tree tree;
	
	public JSONBuilder(Tree tree) {
		this.tree = tree;
	}
	
	public String buildJson() {
		
		StringBuilder builder = new StringBuilder();
		if ( null == tree ) {
			return builder.toString();
		}
		
		builder.append("{\"name\": \"\", \"root\": ");
		
		this.outputNode(builder, tree.getRoot());
		
		builder.append('}');
		
		return builder.toString();
	}
	
	private void outputNode ( StringBuilder builder, Node node ) {
		
		if ( null == node ) {
			return;
		}
		
		builder.append("{\"name\": \"");
		if ( null != node.getName() ) {
			builder.append(node.getName());
		}
		
		builder.append("\"");
		
		int numChildren = node.getChildren().size();
		if ( numChildren > 0 ) {
			
			builder.append(", \"children\": [ ");
			
			for ( int i = 0; i < numChildren; ++i ) {
				this.outputNode(builder, node.getChildren().get(i));
				if ( i != numChildren - 1 ) {
					builder.append ( "," );
				}
			}
			
			builder.append("] ");
		}
		
		builder.append('}');
	}
}
