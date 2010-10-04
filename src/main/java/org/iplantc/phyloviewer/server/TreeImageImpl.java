/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.iplantc.phyloviewer.client.services.TreeImage;
import org.iplantc.phyloviewer.client.tree.viewer.render.RenderTree;

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
	
	public String getTreeImageURL(String treeID, String layoutID, RenderTree renderer, int width, int height) {
		return "/renderTree?" + "treeID=" + treeID + "&layoutID=" + layoutID + "&width=" + width + "&height=" + height;
	}
}
