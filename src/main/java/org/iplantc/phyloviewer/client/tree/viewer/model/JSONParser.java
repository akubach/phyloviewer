package org.iplantc.phyloviewer.client.tree.viewer.model;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSONParser {

	public static Tree parseJSON(String json) {
				
		JSONValue value = com.google.gwt.json.client.JSONParser.parse(json);
		
		JSONObject object = value.isObject();
		if ( object != null ) {
			JSONValue root = object.get("root");
			if ( root == null ) {
				return null;
			}
			JSONObject rootObject = root.isObject();
			if ( rootObject != null ) {
				Node rootNode=JSONParser.parseNode(rootObject);
				Tree tree = new Tree();
				tree.setRootNode(rootNode);
				return tree;
			}
		}
		
		return null;
	}
	
	private static Node parseNode(JSONObject object) {
		Node node = new Node();
		
		// Set the node's name.
		JSONValue nameValue = object.get ("name");
		parseNameValue(node, nameValue);
		
		// Add the node's children, if any.
		JSONValue childrenValue = object.get ("children");
		parseChildrenValue(node, childrenValue);
		
		return node;
	}

	private static void parseNameValue(Node node, JSONValue nameValue) {
		if ( nameValue != null ) {
			JSONString nameString = nameValue.isString();
			if ( nameString != null ) {
				node.setLabel(nameString.stringValue());
			}
		}
	}

	private static void parseChildrenValue(Node node, JSONValue childrenValue) {
		if ( childrenValue != null ) {
			JSONArray childrenArray = childrenValue.isArray();
			if ( childrenArray != null ) {
				int size = childrenArray.size();
				for(int i=0;i<size;++i) {
					JSONValue value = childrenArray.get(i);
					JSONObject childObject = value.isObject();
					if ( childObject != null ) {
						node.addChild(parseNode(childObject));
					}
				}
			}
		}
	}
}
