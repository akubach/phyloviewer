/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.iplantc.phyloviewer.client.DemoTree;
import org.iplantc.phyloviewer.client.FetchTree;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FetchTreeImpl extends RemoteServiceServlet implements FetchTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8599135298345599286L;

	public void init() {
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.FetchTreeImpl", this);
	}
	
	@Override
	public String fetchTree(DemoTree tree) {
		if (tree.data != null) {
			return tree.data;
		} else {
			try
			{
				return readURL(new URL(tree.url));
			}
			catch(MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		
		return "{}";
	}

	private String readURL(URL url) {
		
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			connection.setDoOutput(true);

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
