/**
 * 
 */
package org.iplantc.iptol;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mule.MuleServer;

/**
 * @author sriram 
 * A servlet to handle request from iPToL web app. Currently this
 *         code just handles0. file upload request.
 */
public class RequestHandlerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FileUploadedEvent fileUploadedEvent;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		byte[] databytes = null;
		if (request.getContentType().substring(0, 9).equals("multipart")) {
			databytes = uploadFile(request, response);
		}
		fileUploadedEvent.fileUploaded(databytes);
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("confirmUploadTreeFile.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return byte[] byte array representation of the uploaded file
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public byte[] uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		List<FileItem> items = null;
		FileItemFactory factory = new DiskFileItemFactory();
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		// Parse the request
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		Iterator<FileItem> iter = items.iterator();
		//currently one file upload at a time
		if (iter != null) {
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (!item.isFormField()) {
					request.setAttribute("uploded_filename", item.getName());
					request
							.setAttribute("uploaded_date", new Date()
									.toString());
					// return byte array contents of file
					return item.get();
				}
			}
		}

		return null;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		fileUploadedEvent = (FileUploadedEvent) MuleServer.getMuleContext()
				.getRegistry().lookupObject("fileUploadedEvent");
		super.init(config);
	}
}
