package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.core.client.JavaScriptObject;

public class JobInfo extends JavaScriptObject {

	protected JobInfo() {
		
	}
	//JNSI methods to get File info	
	public final native String getName() /*-{ return this.name; }-*/;
	public final native String getId() /*-{ return this.id; }-*/;	
	public final native String getCreationDate() /*-{return this.created}-*/;
	public final native String getStatus() /*-{ return this.status}-*/;
}
