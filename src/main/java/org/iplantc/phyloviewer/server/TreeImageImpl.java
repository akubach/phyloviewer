/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.services.TreeImage;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TreeImageImpl extends RemoteServiceServlet implements TreeImage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6030698564239584673L;

	public String getTreeImageURL(String treeID, String layoutID, RenderTree renderer, int width, int height) {
		return "/renderTree?" + "treeID=" + treeID + "&layoutID=" + layoutID + "&width=" + width + "&height=" + height;
	}
}
