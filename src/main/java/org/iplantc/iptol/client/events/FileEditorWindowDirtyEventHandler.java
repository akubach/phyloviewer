package org.iplantc.iptol.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileEditorWindowDirtyEventHandler extends EventHandler 
{
	void onDirty(FileEditorWindowDirtyEvent event);
}
