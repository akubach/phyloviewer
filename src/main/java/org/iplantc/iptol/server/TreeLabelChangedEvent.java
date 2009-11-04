package org.iplantc.iptol.server;

public interface TreeLabelChangedEvent {
	void treeLabelChanged(long id, String newLabel) throws TreeLabelChangedException;
}
