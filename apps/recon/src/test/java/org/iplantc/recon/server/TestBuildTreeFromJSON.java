package org.iplantc.recon.server;

import org.iplantc.phyloviewer.shared.model.INode;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import junit.framework.TestCase;

public class TestBuildTreeFromJSON extends TestCase {
	
	@Test
	public void testConvertWithBranchLengths() {
		String json="{\"tree\":{ \"root\":{\"id\":0,\"children\":[{\"styleId\":\"1\",\"id\":1,\"name\":\"a\",\"children\":[],\"branchLength\":1},{\"styleId\":\"0\",\"id\":2,\"name\":\"b\",\"children\":[],\"branchLength\":2}]}}}";
		JSONObject object;
		try {
			object = new JSONObject(json);
			Tree tree = BuildTreeFromJSON.buildTree(object);
			
			INode root=tree.getRootNode();
			assertEquals(0.0,root.getBranchLength());
			
			INode a = root.getChild(0);
			assertEquals(1.0,a.getBranchLength());
			
			INode b = root.getChild(1);
			assertEquals(2.0,b.getBranchLength());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
