package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeImage")
public interface TreeImage extends RemoteService {

	String getTreeImage(String json, int width, int height, Boolean showTaxonLabels);
	String getTreeImageURL(String treeID, String layoutID, RenderTree renderer, int width, int height);
	String getRemoteTreeImage(String treeID, int width, int height, Boolean showTaxonLabels);
}
