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
import org.iplantc.phyloviewer.model.ConvertToJSON;
import org.iplantc.phyloviewer.model.NewickParser;
import org.iplantc.phyloviewer.shared.layout.LayoutCladogram;
import org.iplantc.phyloviewer.shared.model.Tree;
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
					NewickParser parser = new NewickParser();
					InputStreamReader reader = new InputStreamReader(item.getInputStream());
					Tree tree = parser.parse(reader);

					if(tree != null)
					{
						LayoutCladogram layout = new LayoutCladogram();
						layout.setUseBranchLengths(true);
						layout.layout(tree);

						out.write("{\"tree\":");
						try
						{
							ConvertToJSON.buildJSON(tree).write(out);
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
		}
		catch(FileUploadException e)
		{
			e.printStackTrace(out);
		}
		catch(IOException e)
		{
			e.printStackTrace(out);
		}
	}
}
