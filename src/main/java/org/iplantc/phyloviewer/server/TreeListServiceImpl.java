package org.iplantc.phyloviewer.server;

import org.iplantc.phyloviewer.client.services.TreeListService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TreeListServiceImpl extends RemoteServiceServlet implements TreeListService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1593366473133954060L;

	@Override
	public String getTreeList() {
		
		ITreeData data = (ITreeData) this.getServletContext().getAttribute(Constants.TREE_DATA_KEY);
		return data.getTrees();
	}	
}
