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

import org.iplantc.phyloviewer.client.Constants;
import org.iplantc.phyloviewer.client.FetchTree;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FetchTreeImpl extends RemoteServiceServlet implements FetchTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8599135298345599286L;

	public void init() {
		System.out.println("Starting FetchTreeImpl");
		this.getServletContext().setAttribute("org.iplantc.phyloviewer.server.FetchTreeImpl", this);
	}
	
	@Override
	public String fetchTree(int tree) {
		System.out.println("FetchTreeImpl.fetchTree(int tree): fetching tree #" + tree);
		try {
			
			if ( Constants.SMALL_TREE == tree ) {
				return Constants.TREE_DATA;
				//return readURL ( new URL ( "http://genji.iplantcollaborative.org/data/M4056.json" ) );
			}
			else if ( Constants.FIFTY_K_TAXONS == tree ) {
				return readURL(new URL("http://genji.iplantcollaborative.org/data/50K_final_newick.json"));
			}
			else if ( Constants.ONE_HUNDRED_K_TAXONS == tree ) {
				return readURL(new URL("http://genji.iplantcollaborative.org/data/tree100k.json"));
			}
			else if ( Constants.NCBI_TAXONOMY == tree ) {
				return readURL(new URL("http://genji.iplantcollaborative.org/data/ncbi-taxonomy.json"));
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
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
