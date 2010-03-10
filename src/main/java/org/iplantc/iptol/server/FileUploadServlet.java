package org.iplantc.iptol.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.iplantc.iptol.client.services.ServiceCallWrapper;

/**
 * A class to accept files from the client. This class extends the UploadAction
 * class provided by the GWT Upload library. The executeAction method must be
 * overridden for custom behavior.
 * 
 * @author sriram
 * 
 */
public class FileUploadServlet extends UploadAction {
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method is automatically called for file upload request
	 */
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> fileItems) throws UploadActionException {
		String json = null;
		String idFolder = null;
		String idWorkspace = null;
		String filename = null;
		String bodyFile = null;

		for (FileItem item : fileItems) {
			if (item.isFormField()) {
				String name = item.getFieldName();
				String contents = new String(item.get());

				if (name.equals("folderid")) {
					idFolder = contents;
				} else if (name.equals("workspaceid")) {
					idWorkspace = contents;
				}
			} else {
				filename = item.getName();
				bodyFile = new String(item.get());
			}
		}

		// do we have enough information to make a service call?
		if (idWorkspace != null && idFolder != null && filename != null
				&& bodyFile != null) {
			//build our address
			String address = "http://localhost:14444/workspaces/" + idWorkspace
					+ "/folders/" + idFolder + "/files";
			
			//build our wrapper
			ServiceCallWrapper wrapper = new ServiceCallWrapper(
					ServiceCallWrapper.Type.POST_MULTIPART, address, bodyFile);
			
			//set our disposition
			wrapper.setDisposition("name=\"file\"; filename=\"" + filename
					+ "\"");

			//call the RESTful service
			IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
			json = dispatcher.getServiceData(wrapper);
		}

		// remove files from session. this avoids duplicate submissions
		removeSessionFileItems(request, false);

		return json;
	}
}
