package org.iplantc.de.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsonUtil {
	public static final native <T extends JavaScriptObject> JsArray<T> asArrayOf(String json) /*-{
		return eval(json);
	}-*/;
}
