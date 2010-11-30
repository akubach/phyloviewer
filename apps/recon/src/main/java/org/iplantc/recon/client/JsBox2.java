package org.iplantc.recon.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsBox2 extends JavaScriptObject {

	protected JsBox2(){}
	
	public final native JsVector2 getMin() /*-{ return this.min; }-*/;
	public final native JsVector2 getMax() /*-{ return this.max; }-*/;
}
