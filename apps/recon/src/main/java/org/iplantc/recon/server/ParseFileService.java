package org.iplantc.recon.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.iplantc.phyloviewer.shared.render.style.BranchStyle;
import org.iplantc.phyloviewer.shared.render.style.GlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.IBranchStyle;
import org.iplantc.phyloviewer.shared.render.style.IGlyphStyle;
import org.iplantc.phyloviewer.shared.render.style.ILabelStyle;
import org.iplantc.phyloviewer.shared.render.style.INodeStyle;
import org.iplantc.phyloviewer.shared.render.style.IStyle;
import org.iplantc.phyloviewer.shared.render.style.LabelStyle;
import org.iplantc.phyloviewer.shared.render.style.NodeStyle;
import org.iplantc.phyloviewer.shared.render.style.Style;
import org.iplantc.phyloviewer.shared.render.style.StyleById;
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
		
					StyleById styleMap = new StyleById();
					styleMap.put("0", new Style("0", new NodeStyle("#FF0000", 2.0), 
							new LabelStyle("#000000"),
							new GlyphStyle("#FFFF00", "#00FFFF", 1.0),
							new BranchStyle("#00FF00", 1.0)));
					
					styleMap.put("1", new Style("1", new NodeStyle("#FF00FF", 4.0), 
							new LabelStyle("#FF00FF"),
							new GlyphStyle("#00FF00", "#005500", 1.0),
							new BranchStyle("#00FF55", 3.0)));
					
					// TODO: write out json with style info.
					JSONObject object = new JSONBuilder().buildJSON(tree,styleMap);
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
				
		JSONObject buildJSON(org.iplantc.phyloparser.model.Tree tree,StyleById styleMap) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("tree",this.buildJSON(tree));
			object.put("styleMap",this.buildJSON(styleMap));
			return object;
		}
		
		JSONObject buildJSON(IBranchStyle style) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("strokeColor", style.getStrokeColor());
			object.put("lineWidth", style.getLineWidth());
			return object;
		}
		JSONObject buildJSON(IGlyphStyle style) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("strokeColor", style.getStrokeColor());
			object.put("fillColor", style.getFillColor());
			object.put("lineWidth", style.getLineWidth());
			return object;
		}
		JSONObject buildJSON(ILabelStyle style) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("color", style.getColor());
			return object;
		}
		JSONObject buildJSON(INodeStyle style) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("color", style.getColor());
			object.put("pointSize", style.getPointSize());
			return object;
		}
		JSONObject buildJSON(IStyle style) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("branchStyle", this.buildJSON(style.getBranchStyle()));
			object.put("glyphStyle", this.buildJSON(style.getGlyphStyle()));
			object.put("labelStyle", this.buildJSON(style.getLabelStyle()));
			object.put("nodeStyle", this.buildJSON(style.getNodeStyle()));
			return object;
		}
		
		JSONObject buildJSON(StyleById styleMap) throws JSONException {
			JSONObject object = new JSONObject();
			
			Set<String> keys = styleMap.getKeys();
			Iterator<String> iterator = keys.iterator();
			while(iterator.hasNext()) {
				String key = iterator.next();
				object.put(key, this.buildJSON(styleMap.get(key)));
			}
			
			return object;
		}
		
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
			
			if(nextId % 3 != 0) {
				int styleId = nextId % 2;
				object.put("styleId",Integer.toString(styleId));
			}
			
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
