package org.iplantc.phyloviewer.viewer.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeListServiceAsync {

	void getTreeList(AsyncCallback<String> callback);
}
