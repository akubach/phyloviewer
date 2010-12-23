package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public abstract class MessagePayloadEvent<H extends EventHandler> extends GwtEvent<H> {

	JSONObject message;
	JSONObject payload;
	
	public MessagePayloadEvent(JSONObject message, JSONObject payload) {
		this.message = message;
		this.payload = payload;
	}
	
	public JSONObject getMessage() {
		return this.message;
	}
	
	public String getMessageString() {
		String message="";
		
		if(this.message!=null) {
			JSONString value = this.message.get("message").isString();
			if(value != null) {
				message = value.stringValue();
			}
		}
		
		return message;
	}
	
	public JSONObject getPayload() {
		return this.payload;
	}
}
