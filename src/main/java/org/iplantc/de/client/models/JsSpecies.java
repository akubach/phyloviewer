package org.iplantc.de.client.models;

import com.google.gwt.core.client.JavaScriptObject;

public class JsSpecies extends JavaScriptObject {

	protected JsSpecies() {

	}

	// JSNI methods to get Tree info
	public final native String getName() /*-{ return this.name; }-*/;

	public final native String getId() /*-{ return this.id; }-*/;

}
