package org.iplantc.de.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.iplantc.de.client.services.MultiPartServiceWrapper;
import org.iplantc.de.client.views.panels.FileUploadPanel;

/**
 * A class to accept files from the client.
 * 
 * This class extends the UploadAction class provided by the GWT Upload library. The
 * executeAction method must be overridden for custom behavior.
 * 
 * @author sriram
 * 
 */
public class FileUploadServlet extends UploadAction
{
	private static final long serialVersionUID = 1L;

	/**
	 * The logger for error and informational messages.
	 */
	private static Logger LOG = Logger.getLogger(FileUploadServlet.class);

	// TODO: can use a format string like this to format the file disposition
	// public static final String FMT_DISPOSITION = "name=\"file\"; filename=\"%1$s\"";

	/**
	 * Performs the necessary operations for an upload action.
	 */
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> fileItems)
			throws UploadActionException
	{
		String json = null;
		String idFolder = null;
		String idWorkspace = null;
		String filename = null;
		String bodyFile = null;

		LOG.debug("Upload Action started.");

		for(FileItem item : fileItems)
		{
			if(item.isFormField())
			{
				String name = item.getFieldName();
				String contents = new String(item.get());

				if(name.equals(FileUploadPanel.HDN_PARENT_ID_KEY))
				{
					idFolder = contents;
				}
				else if(name.equals(FileUploadPanel.HDN_WORKSPACE_ID_KEY))
				{
					idWorkspace = contents;
				}
			}
			else
			{
				filename = item.getName();
				bodyFile = new String(item.get());
			}
		}

		// do we have enough information to make a service call?
		if(sufficientData(idWorkspace, idFolder, filename, bodyFile))
		{
			json = invokeService(request, idFolder, idWorkspace, filename, bodyFile);
		}

		// remove files from session. this avoids duplicate submissions
		removeSessionFileItems(request, false);

		LOG.debug("FileUploadServlet::executeAction - JSON returned: " + json);
		return json;
	}

	/**
	 * Handles the invocation of the file upload service.
	 * 
	 * @param request current HTTP request
	 * @param idFolder the folder identifier for where the file will be related
	 * @param idWorkspace the workspace identifier for a user's workspace
	 * @param filename the name of the file being uploaded
	 * @param fileContents the content of the file
	 * @param fileContents
	 * @return a string representing data in JSON format.
	 * @throws UploadActionException
	 */
	private String invokeService(HttpServletRequest request, String idFolder, String idWorkspace,
			String filename, String fileContents) throws UploadActionException
	{
		String json = null;
		MultiPartServiceWrapper wrapper = createServiceWrapper(idFolder, idWorkspace, filename,
				fileContents);

		// call the RESTful service and get the results.
		try
		{
			DEServiceDispatcher dispatcher = new DEServiceDispatcher();
			dispatcher.setContext(getServletContext());
			dispatcher.setRequest(request);
			json = dispatcher.getServiceData(wrapper);
			LOG.debug("FileUploadServlet::executeAction - Making service call.");
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			throw new UploadActionException(e.getMessage());
		}
		return json;
	}

	/**
	 * Constructs and configures a multi-part service wrapper.
	 * 
	 * @param idFolder the folder identifier for where the file will be related
	 * @param idWorkspace the workspace identifier for a user's workspace
	 * @param filename the name of the file being uploaded
	 * @param fileContents the content of the file
	 * @return
	 */
	private MultiPartServiceWrapper createServiceWrapper(String idFolder, String idWorkspace,
			String filename, String fileContents)
	{
		// build our address
		// TODO: get these strings into a constants file
		// can use 'format-string' style approach:
		// {0}://{1}:{2}/workspaces/{3}/folders/{4}/files % http. localhost, 14444,
		// idWorkspace, idFolder
		String address = "http://localhost:14444/workspaces/" + idWorkspace + "/folders/" + idFolder
				+ "/files";

		// TODO: Should there be a FileServices class that is wrapping all of this like
		// FolderServices/etc.???
		// build our wrapper
		MultiPartServiceWrapper wrapper = new MultiPartServiceWrapper(MultiPartServiceWrapper.Type.POST,
				address);
		// TODO: format string or CONSTANT
		String disposition = "name=\"file\"; filename=\"" + filename + "\"";
		wrapper.addPart(fileContents, disposition);
		return wrapper;
	}

	/**
	 * Determines if sufficient data is present to perform an action.
	 * 
	 * @param idWorkspace the workspace identifier for a user's workspace
	 * @param idFolder the folder identifier for where the file will be related
	 * @param filename the name of the file being uploaded
	 * @param fileContents the content of the file
	 * @return true if all argument have valid values; otherwise false
	 */
	private boolean sufficientData(String idWorkspace, String idFolder, String filename,
			String fileContents)
	{
		return idWorkspace != null && idFolder != null && filename != null && fileContents != null;
	}
}
