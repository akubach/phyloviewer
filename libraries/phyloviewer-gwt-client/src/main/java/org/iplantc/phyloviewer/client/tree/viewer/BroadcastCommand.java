package org.iplantc.phyloviewer.client.tree.viewer;

/**
 * This is the command for pushing json messages from adapters to the outer container.
 * These messages will then be "broadcast" to other adapters sharing the container.
 * 
 * @author amuir
 * 
 */
public interface BroadcastCommand
{
	void broadcast(String jsonMsg);
}
