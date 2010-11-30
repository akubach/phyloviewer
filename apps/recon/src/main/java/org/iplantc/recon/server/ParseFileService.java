package org.iplantc.recon.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.iplantc.phyloparser.exception.ParserException;
import org.iplantc.phyloparser.model.FileData;
import org.iplantc.phyloparser.model.Node;
import org.iplantc.phyloparser.model.block.Block;
import org.iplantc.phyloparser.model.block.TreesBlock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseFileService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4067088738895724853L;

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// application/json as the content type doesn't work on the client (html gets added to the response)
		response.setContentType("text/html");
		
		PrintWriter out = null;
		
		// Parse the request
		try {
			out = response.getWriter();
				
			List items = upload.parseRequest(request);
			
			Iterator iter = items.iterator();
			while(iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				
				if(!item.isFormField()) {
					
					org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();
					
					InputStreamReader reader = new InputStreamReader ( item.getInputStream() );
					FileData data = parser.parse(reader,false);
					
					org.iplantc.phyloparser.model.Tree tree = null;
					
					List<Block> blocks = data.getBlocks();
					for ( Block block : blocks ) {
						if ( block instanceof TreesBlock ) {
							TreesBlock trees = (TreesBlock) block;
							tree = trees.getTrees().get( 0 );
						}
					}
					
					// TODO: write out json with style info.
					JSONObject object = new JSONBuilder().buildJSON(tree);
					out.write(object.toString());
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace(out);
		} catch (IOException e) {
			e.printStackTrace(out);
		} catch (ParserException e) {
			e.printStackTrace(out);
		} catch (JSONException e) {
			e.printStackTrace(out);
		}
	}
	
	private class JSONBuilder {
		int nextId = 0;
		
		JSONObject buildJSON(org.iplantc.phyloparser.model.Tree tree) throws JSONException {
			nextId = 0;
			JSONObject object = new JSONObject();
			object.put("root", buildJSON(tree.getRoot()));
			object.put("id",0);
			return object;
		}
		
		JSONObject buildJSON(org.iplantc.phyloparser.model.Node node) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("id", nextId);
			++nextId;
			
			object.put("name", node.getName());
			
			List<Node> myChildren = node.getChildren();
			Iterator<Node> iter = myChildren.iterator();
			JSONArray children = new JSONArray();
			while(iter.hasNext()) {
				children.put(buildJSON(iter.next()));
			}
			object.put("children", children);
			return object;
		}
	}
}
