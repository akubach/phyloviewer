package org.iplantc.recon.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.math.Vector2;
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
			
			out.write(layoutToJSON(layout).toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	static JSONObject vectorToJSON(Vector2 vector) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("x",vector.getX());
		object.put("y",vector.getY());
		return object;
	}
	
	static JSONObject boundingBoxToJSON(Box2D box) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("min",vectorToJSON(box.getMin()));
		object.put("max",vectorToJSON(box.getMax()));
		return object;
	}
	
	static JSONObject layoutToJSON(LayoutCladogram layout) throws JSONException {
		JSONObject object = new JSONObject();
		
		JSONObject positions = new JSONObject();
		JSONObject bounds = new JSONObject();
		
		Set<Integer> keys = layout.keySet();
		Iterator<Integer> iter = keys.iterator();
		while(iter.hasNext()) {
			Integer key = iter.next();
			Vector2 position = layout.getPosition(key);
			Box2D box = layout.getBoundingBox(key);
			positions.put(key.toString(), vectorToJSON(position));
			bounds.put(key.toString(), boundingBoxToJSON(box));
		}
		
		object.put("positions", positions);
		object.put("bounds", bounds);
		
		return object;
	}
}
