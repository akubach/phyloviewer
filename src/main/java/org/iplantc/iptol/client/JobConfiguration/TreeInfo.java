/**
 * 
 */
package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author sriram
 * 
 */
public class TreeInfo extends JavaScriptObject {

	protected TreeInfo() {

	}

	// JNSI methods to get Tree info
	public final native String getFilename() /*-{ return this.filename; }-*/;

	public final native String getTreename() /*-{return this.treeName; }-*/;

	public final native String getUploaded() /*-{ return this.uploaded; }-*/;

}
