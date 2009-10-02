/**
 * 
 */
package org.ipc.iptol.web;

import java.io.DataInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mule.MuleServer;
import org.mule.api.MuleContext;

/**
 * @author sriram
 * A servlet to handle request from iPToL web app.
 * Currently this coded just to handle file upload request.
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
			throws IOException {
		byte[] databytes;
		databytes = uploadFile(request, response);
		
		fileUploadedEvent.fileUploaded(databytes);
	}
	
	/**
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return byte[] byte array representation of the uploaded file
	 * @throws IOException
	 */
	public byte[] uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
	
		String contentType = request.getContentType();
		System.out.println(":"+ contentType +":");
		
		if ((contentType != null)
				&& (contentType.indexOf("multipart/form-data") >= 0)) {
			DataInputStream in = new DataInputStream(request.getInputStream());
			int formDataLength = request.getContentLength();
			byte dataBytes[] = new byte[formDataLength];
			int byteRead = 0;
			int totalBytesRead = 0;

			while (totalBytesRead < formDataLength) {
				byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
				totalBytesRead += byteRead;
			}

			String file = new String(dataBytes);
			System.out.println(file);

			// retrieve file name
			// String saveFile = file.substring(file.indexOf("filename=\"") +
			// 10);
			// saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
			// saveFile = saveFile.substring(saveFile.lastIndexOf("\\") +
			// 1,saveFile.indexOf("\""));

			int lastIndex = contentType.lastIndexOf("=");
			String boundary = contentType.substring(lastIndex + 1, contentType
					.length());

			int pos;
			pos = file.indexOf("filename=\"");
			pos = file.indexOf("\n", pos) + 1;
			pos = file.indexOf("\n", pos) + 1;
			pos = file.indexOf("\n", pos) + 1;

			int boundaryLocation = file.indexOf(boundary, pos) - 4;
			int startPos = ((file.substring(0, pos)).getBytes()).length;
			int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;

			System.out
					.println("contents==>" + file.substring(startPos, endPos));
			dataBytes = file.substring(startPos, endPos).getBytes();
			return dataBytes;

		} else {
			return null;
		}

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		fileUploadedEvent = (FileUploadedEvent)MuleServer.getMuleContext().getRegistry().lookupObject("fileUploadedEvent");
		super.init(config);
	}
}
