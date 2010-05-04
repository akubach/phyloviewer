package org.iplantc.de.client.JobConfiguration.contrast;

import com.google.gwt.core.client.JavaScriptObject;

public class TraitInfo extends JavaScriptObject {

	protected TraitInfo() {

	}

	// JSNI methods to get Tree info
	public final native String getFilename() /*-{ return this.filename; }-*/;

	public final native String getId() /*-{ return this.id; }-*/;

	public final native String getUploaded() /*-{ return this.uploaded; }-*/;

}
