package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeImage")
public interface TreeImage extends RemoteService {

	/*
	 * Get the url to download the overview image for the tree and layout.
	 */
	String getTreeImageURL(String treeID, String layoutID, RenderTree renderer, int width, int height);
}
