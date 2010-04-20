/**
 * 
 */
package org.iplantc.iptol.client.JobConfiguration.contrast;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author sriram
 * 
 */
public class TreeInfo extends JavaScriptObject {

	protected TreeInfo() {

	}

	// JSNI methods to get Tree info
	public final native String getId() /*-{ return this.id; }-*/;
	public final native String getFilename() /*-{ return this.filename; }-*/;
	public final native String getTreename() /*-{return this.treeName; }-*/;
	public final native String getUploaded() /*-{ return this.uploaded; }-*/;

}
