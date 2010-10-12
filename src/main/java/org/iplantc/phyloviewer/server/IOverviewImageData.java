package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;

public interface IOverviewImageData {

	public abstract void setOverviewImage(String treeId, String layoutId,BufferedImage image);
	public abstract BufferedImage getOverviewImage(String treeId, String layoutId);
}
