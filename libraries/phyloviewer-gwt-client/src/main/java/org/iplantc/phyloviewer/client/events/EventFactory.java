package org.iplantc.phyloviewer.client.events;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
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
	
	public static DataPayloadEvent createNodeClickedEvent(int nodeId,Vector2 position,Box2D bounds) {
		String json = "{\"nodeId\":" + nodeId + ",\"position\":" + position.toJSON() + ",\"boundingBox\":" + bounds.toJSON() + "}";
		return new DataPayloadEvent(createMessage(Messages.MESSAGE_NODE_CLICKED),new JSONObject(JsonUtils.safeEval(json)));
	}
}
