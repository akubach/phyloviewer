package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeImageAsync {

	void getTreeImageURL(int treeID, String layoutID, int width, int height, AsyncCallback<String> callback);
}
