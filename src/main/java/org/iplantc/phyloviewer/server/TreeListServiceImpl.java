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
		
		JSONObject result = new JSONObject();
		JSONArray trees = new JSONArray();
		
		try {
			trees.put(buildJSONForTree("1401c25e-0d69-4705-b6f3-90f489a582d2","Small"));
			trees.put(buildJSONForTree("bda55c01-e4ac-4039-aecd-7702853b70d9","50K"));
			trees.put(buildJSONForTree("a448601d-cc54-47e0-b9a8-b0a4e8a0e131","100K"));
			trees.put(buildJSONForTree("401b32c1-fee5-47e8-a6f9-92efb8b0da87","NCBI taxonomy"));
			
			result.put("trees",trees);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Simulate some delay.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		return result.toString();
	}

	private JSONObject buildJSONForTree(String id, String name) throws JSONException {
		JSONObject tree = new JSONObject();
		tree.put("id", id);
		tree.put("name", name);
		return tree;
	}
}
