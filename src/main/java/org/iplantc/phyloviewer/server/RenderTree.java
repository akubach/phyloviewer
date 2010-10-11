package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.ITree;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTreeCladogram;
import org.iplantc.phyloviewer.server.render.ImageGraphics;

public class RenderTree extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2634082637401140976L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
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
			response.setContentLength ( out.size() );
			
			ServletOutputStream stream = response.getOutputStream();
			out.writeTo(stream);
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	 public BufferedImage renderTreeImage(String treeID, String layoutID, int width, int height) {
		 
		 ImageGraphics graphics = new ImageGraphics(width,height);
			
		 // generate an image from the actual layout
		 ILayout layout = getLayoutService().getLayout(layoutID);
		 ITree tree = getNodeService().fetchTree(treeID);
		
		 RenderTreeCladogram renderer = new RenderTreeCladogram(); //TODO make this use the same renderer as the view it is an overview of
		 renderer.setCollapseOverlaps(false);
		 renderer.setDrawLabels(false);
		
		 renderer.renderTree(tree, layout, graphics, null, null);
		
		 return graphics.getImage();
	}
	
	private RemoteLayoutServiceImpl getLayoutService() {
		return (RemoteLayoutServiceImpl) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.RemoteLayoutServiceImpl");
	}
	
	private RemoteNodeServiceImpl getNodeService() {
		return (RemoteNodeServiceImpl) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.RemoteNodeServiceImpl");
	}
}
