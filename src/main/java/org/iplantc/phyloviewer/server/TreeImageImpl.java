/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.iplantc.phyloviewer.client.services.TreeImage;
import org.iplantc.phyloviewer.client.tree.viewer.layout.ILayout;
import org.iplantc.phyloviewer.client.tree.viewer.model.Tree;
import org.iplantc.phyloviewer.client.tree.viewer.render.CameraCircular;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;
import org.iplantc.phyloviewer.server.render.Java2DGraphics;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TreeImageImpl extends RemoteServiceServlet implements TreeImage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6030698564239584673L;

	@Override
	public String getTreeImage(String json, int width, int height,
			Boolean showTaxonLabels) {
		
		try {
			String showText = showTaxonLabels ? "on" : "off";
			String address = "http://genji.iplantcollaborative.org/cgi-bin/v2/create_image?use_branch_lengths=off&show_text="
					+ showText;
			address += "&width=" + width + "&height=" + height;
			
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
	
			// send post
			OutputStreamWriter outRemote = new OutputStreamWriter(connection.getOutputStream());
			outRemote.write(json);
			outRemote.flush();
			outRemote.close();
	
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			
			String line;
		    while ((line = br.readLine()) != null) {
		        buffer.append(line);
		    }
		    
			String res = buffer.toString();
			connection.disconnect();
			
			return res;
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return "";
	}
	
	@Override
	public String getRemoteTreeImage(String treeID, int width, int height, Boolean showTaxonLabels) {
		Tree tree = getNodeService().fetchTree(treeID);
		String json = tree.getJSON();
		return getTreeImage(json, width, height, showTaxonLabels);
	}

	/* TODO finish this and add it to the TreeImage service interface */
	public String getTreeImage(String treeID, String layoutID, RenderTree renderer, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// generate an image from the actual layout
		ILayout layout = getLayoutService().getLayout(layoutID);
		Tree tree = getNodeService().fetchTree(treeID);
		Java2DGraphics graphics = new Java2DGraphics(image.createGraphics());
		CameraCircular camera = new CameraCircular();
		
		//TODO camera config
		
		renderer.renderTree(tree, layout, graphics, camera, null);
		
		//TODO write image to server file system (where?) and send back URL

		return null;
	}
	
	private RemoteLayoutServiceImpl getLayoutService() {
		return (RemoteLayoutServiceImpl) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.RemoteLayoutServiceImpl");
	}
	
	private RemoteNodeServiceImpl getNodeService() {
		return (RemoteNodeServiceImpl) this.getServletContext().getAttribute("org.iplantc.phyloviewer.server.RemoteNodeServiceImpl");
	}
}
