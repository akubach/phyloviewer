package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseOverviewImage implements IOverviewImageData {

	private static ConcurrentHashMap<String, BufferedImage> images;
	
	public DatabaseOverviewImage() {
		images = new ConcurrentHashMap<String, BufferedImage>();
	}
	
	@Override
	public BufferedImage getOverviewImage(String treeId, String layoutId) {
		return images.get(treeId);
	}

	@Override
	public void setOverviewImage(String treeId, String layoutId,
			BufferedImage image) {
		images.put(treeId, image);
	}

}
