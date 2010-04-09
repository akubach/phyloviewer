package org.iplantc.iptol.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iplantc.iptol.client.services.ServiceCallWrapper;

@SuppressWarnings("serial")
public class FileDownloadServlet extends HttpServlet {

	//private static final Logger logger = Logger.getLogger(FileDownloadServlet.class);

	public void doGet (HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

		try {
			int startIndex = request.getServletPath().indexOf("files") + 6;
			int endIndex = request.getServletPath().indexOf("content") - 1;
			String fileId = request.getServletPath().substring(startIndex, endIndex);
			String address = "http://localhost:14444/files/" + fileId + "/content" ;

			//build our wrapper
			ServiceCallWrapper wrapper = new ServiceCallWrapper(address);

			//call the RESTful service and get the results.
			IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
			dispatcher.setContext(getServletContext());
			dispatcher.setRequest(request);
			String fileContents = dispatcher.getServiceData(wrapper);
			response.setContentType(dispatcher.getURLConnection().getContentType());
			response.setHeader("Content-Disposition", dispatcher.getURLConnection().getHeaderField("Content-Disposition"));
			PrintWriter out = response.getWriter();
			out.print(fileContents);
			out.close();
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
    }
}
