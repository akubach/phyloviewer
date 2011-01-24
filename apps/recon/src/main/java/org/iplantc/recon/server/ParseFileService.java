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
import org.iplantc.phyloviewer.model.ConvertToJSON;
import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.json.JSONException;

public class ParseFileService extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4067088738895724853L;

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// application/json as the content type doesn't work on the client (html gets added to the
		// response)
		response.setContentType("text/html");

		PrintWriter out = null;

		// Parse the request
		try
		{
			out = response.getWriter();

			List items = upload.parseRequest(request);

			Iterator iter = items.iterator();
			while(iter.hasNext())
			{
				FileItem item = (FileItem)iter.next();

				if(!item.isFormField())
				{
					org.iplantc.phyloparser.parser.NewickParser parser = new org.iplantc.phyloparser.parser.NewickParser();

					InputStreamReader reader = new InputStreamReader(item.getInputStream());
					FileData data = parser.parse(reader, false);

					org.iplantc.phyloparser.model.Tree tree = null;

					List<Block> blocks = data.getBlocks();
					for(Block block : blocks)
					{
						if(block instanceof TreesBlock)
						{
							TreesBlock trees = (TreesBlock)block;
							tree = trees.getTrees().get(0);
						}
					}

					/*
					 * StyleById styleMap = new StyleById();
					 * 
					 * styleMap.put("0", new Style("0", new NodeStyle("#FF0000", 4.0), new
					 * LabelStyle("#000000"), new GlyphStyle(Defaults.TRIANGLE_FILL_COLOR,
					 * Defaults.TRIANGLE_OUTLINE_COLOR, 1.0), new BranchStyle("#000000", 1.0)));
					 * 
					 * styleMap.put("1", new Style("1", new NodeStyle("#0000FF", 4.0), new
					 * LabelStyle("#00FF00"), new GlyphStyle(Defaults.TRIANGLE_FILL_COLOR,
					 * Defaults.TRIANGLE_OUTLINE_COLOR, 1.0), new BranchStyle("#000000", 1.0)));
					 */

					org.iplantc.phyloviewer.shared.model.Node node = new ConvertDataModels()
							.convertDataModels(tree.getRoot());
					tree = null;

					org.iplantc.phyloviewer.shared.model.Tree myTree = new org.iplantc.phyloviewer.shared.model.Tree();
					myTree.setRootNode(node);

					LayoutCladogram layout = new LayoutCladogram();
					layout.layout(myTree);

					out.write("{\"tree\":");
					try
					{
						ConvertToJSON.buildJSON(myTree).write(out);
					}
					catch(JSONException e)
					{
						out.write("{}");
					}
					out.write(",\"layout\":");
					try
					{
						ConvertToJSON.buildJSON(layout).write(out);
					}
					catch(JSONException e)
					{
						out.write("{}");
					}
					out.write("}");
				}
			}
		}
		catch(FileUploadException e)
		{
			e.printStackTrace(out);
		}
		catch(IOException e)
		{
			e.printStackTrace(out);
		}
		catch(ParserException e)
		{
			e.printStackTrace(out);
		}
	}

	private class ConvertDataModels
	{
		int nextId = 0;

		public org.iplantc.phyloviewer.shared.model.Node convertDataModels(
				org.iplantc.phyloparser.model.Node node)
		{
			List<Node> myChildren = node.getChildren();

			int len = myChildren.size();
			org.iplantc.phyloviewer.shared.model.Node[] children = new org.iplantc.phyloviewer.shared.model.Node[len];

			int id = nextId++;

			for(int i = 0;i < len;i++)
			{
				Node myChild = myChildren.get(i);

				org.iplantc.phyloviewer.shared.model.Node child = convertDataModels(myChild);
				children[i] = child;
			}

			String label = node.getName();

			// create a Node for the current node
			org.iplantc.phyloviewer.shared.model.Node rNode = new org.iplantc.phyloviewer.shared.model.Node(
					id, label);
			rNode.setChildren(children);

			return rNode;
		}
	}
}
