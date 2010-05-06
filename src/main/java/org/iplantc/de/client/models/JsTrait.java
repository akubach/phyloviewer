package org.iplantc.de.client.models;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Models a native JavaScript representation of a Trait. 
 */
public class JsTrait extends JavaScriptObject
{

	protected JsTrait()
	{

	}

	// JSNI methods to get Tree info
	public final native String getFilename() /*-{ return this.filename; }-*/;

	public final native String getId() /*-{ return this.id; }-*/;

	public final native String getUploaded() /*-{ return this.uploaded; }-*/;

}
