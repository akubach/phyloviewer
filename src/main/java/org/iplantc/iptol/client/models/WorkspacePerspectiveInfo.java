package org.iplantc.iptol.client.models;

import com.google.gwt.core.client.JavaScriptObject;

public class WorkspacePerspectiveInfo extends JavaScriptObject 
{
	protected WorkspacePerspectiveInfo() 
	{		
	}
	
	//JSNI methods to get File info	
	public final native String getName() /*-{ return this.name; }-*/;
	public final native String getId() /*-{ return this.id; }-*/;	
}
