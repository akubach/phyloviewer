package org.iplantc.de.client.JobConfiguration.contrast;

import com.google.gwt.core.client.JavaScriptObject;

public class SpeciesInfo extends JavaScriptObject {

	protected SpeciesInfo() {

	}

	// JSNI methods to get Tree info
	public final native String getName() /*-{ return this.name; }-*/;

	public final native String getId() /*-{ return this.id; }-*/;

}
