package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeImageAsync {

	void getTreeImage(String json, int width, int height, Boolean showTaxonLabels,
			AsyncCallback<String> callback);
}
