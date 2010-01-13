package org.iplantc.iptol.client;

import com.google.gwt.core.client.JavaScriptObject;
/**
 * A class that contains native methods which returns the field with a json.
 * simulate pojo
 * @author sriram
 *
 */
public class FileInfo extends JavaScriptObject{

	protected FileInfo() {
		
	}
	
	//JNSI methods to get File info
	
	public final native String getName() /*-{ return this.name; }-*/;
	public final native String getLabel() /*-{return this.label; }-*/;
	public final native String getUploaded() /*-{ return this.uploaded; }-*/;
	public final native String getType() /*-{ return this.type; }-*/;
	public final native String getId() /*-{ return this.id; }-*/;
	
}
