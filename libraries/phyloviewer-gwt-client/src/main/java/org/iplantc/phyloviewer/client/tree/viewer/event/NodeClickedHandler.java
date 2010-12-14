/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.client.tree.viewer.event;

import org.iplantc.phyloviewer.shared.model.INode;

public interface NodeClickedHandler {

	public abstract void onNodeClicked(INode node);
}
