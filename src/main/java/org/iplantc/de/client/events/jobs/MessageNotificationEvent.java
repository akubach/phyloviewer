package org.iplantc.de.client.events.jobs;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event represents a need to send notification messages to the controller / job view class
 * 
 * @see org.iplantc.de.client.jobs.JobView
 * @author sriram
 * 
 */
public class MessageNotificationEvent extends GwtEvent<MessageNotificationEventHandler>
{

	public static final GwtEvent.Type<MessageNotificationEventHandler> TYPE = new GwtEvent.Type<MessageNotificationEventHandler>();

	/**
	 * Indicates the type of message that needs to be sent as a notification.
	 */
	public enum MessageType
	{
		ALERT, INFORMATION, ERROR
	}

	private String msg;
	private MessageType msgType;

	public MessageNotificationEvent(String msg, MessageType type)
	{
		this.setMsg(msg);
		this.setMsgType(type);
	}

	@Override
	protected void dispatch(MessageNotificationEventHandler handler)
	{
		handler.onMessage(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<MessageNotificationEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsgType(MessageType msgType)
	{
		this.msgType = msgType;
	}

	public MessageType getMsgType()
	{
		return msgType;
	}

}
