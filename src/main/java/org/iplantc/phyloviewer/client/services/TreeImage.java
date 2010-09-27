package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeImage")
public interface TreeImage extends RemoteService {

	String getTreeImage(String json, int width, int height, Boolean showTaxonLabels);
	String getRemoteTreeImage(String treeID, int width, int height, Boolean showTaxonLabels);
}
