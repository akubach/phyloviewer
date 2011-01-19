package org.iplantc.recon.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.model.Tree;
import org.json.JSONException;
import org.json.JSONObject;

public class LayoutService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2607722375152684106L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		BufferedReader br;
		PrintWriter out = null;
		
		try {
			out = response.getWriter();
			
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String json = br.readLine();
			
			response.setContentType("text/html");
			
			JSONObject object = new JSONObject(json);
			Tree tree = BuildTreeFromJSON.buildTree(object);
			
			LayoutCladogram layout = new LayoutCladogram(0.8,1.0);
			layout.setUseBranchLengths(true);
			layout.layout(tree);
			
			out.write(layout.toJSON());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
