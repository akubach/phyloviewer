package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.core.client.JavaScriptObject;

public class TraitInfo extends JavaScriptObject {

	protected TraitInfo() {

	}

	// JNSI methods to get Tree info
	public final native String getFilename() /*-{ return this.filename; }-*/;

	public final native String getTraitblockname() /*-{return this.traitblock; }-*/;

	public final native String getUploaded() /*-{ return this.uploaded; }-*/;

}
