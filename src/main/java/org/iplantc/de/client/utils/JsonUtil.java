package org.iplantc.de.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Provides JSON utility operations. 
 */
public class JsonUtil
{
	/**
	 * Returns a JavaScript array representation of JSON argument data.
	 * @param <T> type of the elements contains in the JavaScript Array
	 * @param json a string representing data in JSON format
	 * @return a JsArray of type T
	 */
	public static final native <T extends JavaScriptObject> JsArray<T> asArrayOf(String json)
	/*-{
		return eval(json);
	}-*/;
}
