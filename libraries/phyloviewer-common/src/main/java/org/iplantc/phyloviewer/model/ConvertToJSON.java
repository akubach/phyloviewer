package org.iplantc.phyloviewer.model;

import org.iplantc.phyloviewer.shared.model.Node;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvertToJSON
{
	static JSONObject buildJSON(Node node) throws JSONException
	{
		JSONObject object = new JSONObject();
		object.put("id", node.getId());

		object.put("name", node.getLabel());
		object.put("branchLength", node.getBranchLength());

		Node[] myChildren = node.getChildren();
		if(myChildren != null )
		{
			JSONArray children = new JSONArray();
			for(Node child : myChildren)
			{
				children.put(buildJSON(child));
			}
			object.put("children", children);
		}
		
		return object;
	}
	
	/**
	 * Convert node to JSON
	 * @param Node to convert
	 * @return JSON string
	 */
	public static String getJSON(Node node)
	{
		try
		{
			return buildJSON(node).toString();
		}
		catch(JSONException e)
		{
		}
		
		return "{}";
	}
}
