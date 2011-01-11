package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeIntersectServiceAsync {

	void intersectTree(int treeId, double x, double y,AsyncCallback<String> callback);
}
