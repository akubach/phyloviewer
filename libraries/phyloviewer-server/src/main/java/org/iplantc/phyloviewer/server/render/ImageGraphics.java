package org.iplantc.phyloviewer.server.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageGraphics extends Java2DGraphics
{
	private BufferedImage image;

	public ImageGraphics(int width, int height)
	{
		super();

		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();

		// Turn on anti-aliasing.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setClip(0, 0, width, height);
		g2d.setBackground(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		this.setGraphics2D(g2d);

		this.setViewport(0, 0, width, height);
		this.setProjection(0, 1.0, 0, 1.0);
	}

	public BufferedImage getImage()
	{
		return image;
	}
}
