package org.iplantc.phyloviewer.client.events;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class EventFactory {

	private EventFactory() {}
	
	public static JSONObject createMessage(String message) {
		JSONObject object = new JSONObject();
		object.put("message", new JSONString(message));
		return object;
	}
	
	public static DataPayloadEvent createRenderEvent() {
		return new DataPayloadEvent(createMessage(Messages.MESSAGE_RENDER),new JSONObject());
	}

	public static DataPayloadEvent createNodeClickedEvent(JavaScriptObject payload) {
		return new DataPayloadEvent(createMessage(Messages.MESSAGE_NODE_CLICKED),new JSONObject(payload));
	}
}
