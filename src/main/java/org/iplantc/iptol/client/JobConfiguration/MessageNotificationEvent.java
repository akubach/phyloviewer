package org.iplantc.iptol.client.JobConfiguration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to notfiy messages to the controller / job view class
 * 
 * @author sriram
 * 
 */
public class MessageNotificationEvent extends
		GwtEvent<MessageNotificationEventHandler> {

	public static final GwtEvent.Type<MessageNotificationEventHandler> TYPE = new GwtEvent.Type<MessageNotificationEventHandler>();

	// type of message
	public enum MessageType {
		ALERT, INFORMATION, ERROR
	}

	private String msg;

	private MessageType msgType;

	public MessageNotificationEvent(String msg, MessageType type) {
		this.setMsg(msg);
		this.setMsgType(type);
	}

	@Override
	protected void dispatch(MessageNotificationEventHandler handler) {
		handler.onMessage(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<MessageNotificationEventHandler> getAssociatedType() {
		return TYPE;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsgType(MessageType msgType) {
		this.msgType = msgType;
	}

	public MessageType getMsgType() {
		return msgType;
	}

}
