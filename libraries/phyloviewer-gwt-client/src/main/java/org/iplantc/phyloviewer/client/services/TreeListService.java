package org.iplantc.phyloviewer.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeList")
public interface TreeListService extends RemoteService {

	String getTreeList();
}
