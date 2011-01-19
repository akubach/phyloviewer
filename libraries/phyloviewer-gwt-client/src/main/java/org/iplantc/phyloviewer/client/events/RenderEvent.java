package org.iplantc.phyloviewer.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RenderEvent extends GwtEvent<RenderHandler> {

	public static final Type<RenderHandler> TYPE = new Type<RenderHandler>();
	
	public RenderEvent() {
	}

	@Override
	protected void dispatch(RenderHandler handler) {
		handler.onRender(this);
	}

	@Override
	public Type<RenderHandler> getAssociatedType() {
		return TYPE;
	}
}
