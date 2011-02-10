/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center
 * This software is licensed under the CC-GNU GPL version 2.0 or later.
 * License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.viewer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int loadNewickString(String newick, String name ) {
		IImportTreeData importer = (IImportTreeData) this.getServletContext().getAttribute(Constants.IMPORT_TREE_DATA_KEY);
		
		if(importer != null) {
			int id = importer.importFromNewick(newick, name);
			return id;
		}
		
		return -1;
	}
}
