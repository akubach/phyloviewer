package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeImage")
public interface TreeImage extends RemoteService {

	/*
	 * Get the url to download the overview image for the tree and layout.
	 */
	String getTreeImageURL(int treeID, String layoutID, int width, int height);
}
