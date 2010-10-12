package org.iplantc.phyloviewer.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSTreeList extends JavaScriptObject {

	protected JSTreeList() {
		
	}
	
	public final native <T extends JavaScriptObject> JsArray<T> getTrees() /*-{ return this.trees; }-*/;
	public final JSTreeData getTree(int index) { return (JSTreeData) this.getTrees().get(index); }
	
	public final int getNumberOfTrees()
	{
		if ( null == this.getTrees() )
			return 0;
		return this.getTrees().length();
	}
	
	private final static native JSTreeList getTrees(String json) /*-{ return eval(json); }-*/;
		
	public static JSTreeList parseJSON(String json) {		
		return getTrees( "(" + json + ")" );
	}
}
