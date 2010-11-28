package org.iplantc.phyloviewer.viewer;

import com.google.gwt.core.client.JavaScriptObject;

public class JSTreeData extends JavaScriptObject  {

	protected JSTreeData() {
		
	}
	
	public final native int getId() /*-{ return this.id; }-*/;
	public final native String getName() /*-{return this.name; }-*/;
}
