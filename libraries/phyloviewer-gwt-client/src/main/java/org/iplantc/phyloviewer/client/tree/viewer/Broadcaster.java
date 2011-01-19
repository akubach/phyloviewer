package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.tree.viewer.BroadcastCommand;

/**
 * Adapter interface for facilitating communication between different components. There is
 * a conceptual channel where components for passing messages between adapters.
 * 
 * @author amuir
 * 
 */
public interface Broadcaster
{
	void setBroadcastCommand(BroadcastCommand cmdBroadcast);
}
