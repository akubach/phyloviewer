package org.iplantc.recon.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsVector2 extends JavaScriptObject {

	protected JsVector2(){}
	
	public final native double getX() /*-{ return this.x; }-*/;
	public final native double getY() /*-{ return this.y; }-*/;
}
