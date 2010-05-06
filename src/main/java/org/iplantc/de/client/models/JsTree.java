/**
 * 
 */
package org.iplantc.de.client.models;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Models a native JavaScript representation of a Tree.
 *  
 * @author sriram
 */
public class JsTree extends JavaScriptObject
{

	protected JsTree()
	{

	}

	// JSNI methods to get Tree info
	public final native String getId() /*-{ return this.id; }-*/;

	public final native String getFilename() /*-{ return this.filename; }-*/;

	public final native String getTreename() /*-{return this.treeName; }-*/;

	public final native String getUploaded() /*-{ return this.uploaded; }-*/;

}
