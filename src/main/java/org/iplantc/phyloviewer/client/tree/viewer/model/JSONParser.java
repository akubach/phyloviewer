package org.iplantc.phyloviewer.client.tree.viewer.model;

public class JSONParser {

	public final static native JsTree getTree(String json) /*-{ return eval(json); }-*/;
	
	public static ITree parseJSON(String json) {		
		return getTree( "(" + json + ")" );
	}
}
