package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;

public interface IOverviewImageData {

	public abstract BufferedImage getOverviewImage(int treeId, String layoutId);
}
