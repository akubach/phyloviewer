/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;

public class ParseTree extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2532260393364629170L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		
		response.setContentType("application/json");
		
	    PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String newick = request.getParameter("newickData");
		String name = request.getParameter("name");
		int id = loadNewickString(newick, name);
		out.write("{\"id\":"+id+"}");
	    out.close();
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			
			String name = request.getParameter("name");

			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String newick = br.readLine();
			
			int id = loadNewickString(newick,name);
			out.write("{\"id\":"+id+"}");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int loadNewickString(String newick, String name ) {
		
		org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
		FileData data = null;
		try {
			data = parser.parse(newick);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		org.iplantc.phyloparser.model.Tree tree = null;
		
		List<Block> blocks = data.getBlocks();
		for ( Block block : blocks ) {
			if ( block instanceof TreesBlock ) {
				TreesBlock trees = (TreesBlock) block;
				tree = trees.getTrees().get( 0 );
			}
		}

		if ( tree != null ) {
			JSONBuilder builder = new JSONBuilder ( tree );
			String json = builder.buildJson();

			int id = LoadTreeData.loadTreeDataFromJSON(json, name, this.getServletContext());
			return id;
		}
		
		return -1;
	}
}
