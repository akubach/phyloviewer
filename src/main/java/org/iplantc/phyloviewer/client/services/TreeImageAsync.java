package org.iplantc.phyloviewer.client.services;

import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeImageAsync {

	void getTreeImage(String json, int width, int height, Boolean showTaxonLabels,
			AsyncCallback<String> callback);

	void getRemoteTreeImage(String treeID, int width, int height,
			Boolean showTaxonLabels, AsyncCallback<String> callback);

	void getTreeImageURL(String treeID, String layoutID, RenderTree renderer,
			int width, int height, AsyncCallback<String> callback);
}
