package org.iplantc.phyloviewer.server.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;

public class ImageGraphics extends Java2DGraphics {

	private BufferedImage image;
	private Box2D screenBounds = new Box2D();
	
	public ImageGraphics(int width, int height) {
		super();
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();
		g2d.setClip(0, 0, width, height);
		g2d.setBackground(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		this.setGraphics2D(g2d);
		
		screenBounds.setMin(new Vector2(0,0));
		screenBounds.setMax(new Vector2(width,height));
		
		this.setAffineTransform(AffineTransform.getScaleInstance(width-1, height-1)); //subtracting a pixel here to make sure lines on the bottom row get drawn
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Boolean isCulled(Box2D bbox) {
		if ( !bbox.valid() )
			return false;

		return !screenBounds.intersects(bbox);
	}
}
