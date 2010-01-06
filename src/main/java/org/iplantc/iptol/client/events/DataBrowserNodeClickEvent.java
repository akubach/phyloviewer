package org.iplantc.iptol.client.events;

import org.iplantc.iptol.client.File;
import org.iplantc.iptol.client.FileInfo;

import com.google.gwt.event.shared.GwtEvent;

public class DataBrowserNodeClickEvent extends GwtEvent <DataBrowserNodeClickEventHandler>{

	public static final GwtEvent.Type<DataBrowserNodeClickEventHandler> TYPE = new GwtEvent.Type<DataBrowserNodeClickEventHandler>();
	
	private File file;
	
	public DataBrowserNodeClickEvent(File file) {
		this.setFile(file);
	}
	
	
	@Override
	protected void dispatch(DataBrowserNodeClickEventHandler handler) {
		handler.onNodeClick(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DataBrowserNodeClickEventHandler> getAssociatedType() {
		return TYPE;
	}


	public void setFile(File file) {
		this.file = file;
	}


	public File getFile() {
		return file;
	}

}
