package org.iplantc.phyloviewer.server;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderTree extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2634082637401140976L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		String treeID = request.getParameter("treeID");
		String layoutID = request.getParameter("layoutID");
		int width = Integer.parseInt(request.getParameter("width"));
		int height = Integer.parseInt(request.getParameter("height"));

		BufferedImage image = renderTreeImage(treeID, layoutID, width, height);

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);

			// Set the content parameters and write the bytes for the png.
			response.setContentType("image/png");
			response.setContentLength(out.size());

			ServletOutputStream stream = response.getOutputStream();
			out.writeTo(stream);
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BufferedImage renderTreeImage(String treeID, String layoutID,
			int width, int height) {

		BufferedImage overview =  this.getOverviewData().getOverviewImage(treeID, layoutID);
		
		// Resize the image to the requested size.
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D bg = image.createGraphics();
	    bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    bg.scale((double)width/overview.getWidth(), (double)height/overview.getHeight());
	    bg.drawImage(overview, 0, 0, null);
	    bg.dispose(); 
	    image.flush();
	    
		return image;
	}

	private IOverviewImageData getOverviewData() {
		return (IOverviewImageData) this.getServletContext().getAttribute(Constants.OVERVIEW_DATA_KEY);
	}
}
