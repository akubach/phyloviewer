package org.iplantc.iptol.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.mule.MuleServer;

/**
 * A class to accept files from the client. This class extends the UploadAction
 * class provided by the GWT Upload library
 * 
 * @author sriram
 * 
 */
public class FileUploadServlet extends UploadAction {

	public static final long MAX_FILE_SIZE = 3145728;

	public static final int UPLOAD_DELAY = 0;

	/**
	 * This method is automatically called for file upload request
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private FileUploadedEvent fileUploadedEvent;

	@SuppressWarnings("unchecked")
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> fileItems) throws UploadActionException {

		this.maxSize = MAX_FILE_SIZE;
		this.uploadDelay = UPLOAD_DELAY;
		String filetype = null;

		List data_list = new ArrayList();
		Map root = new HashMap();
		JSONObject json = null;
		for (FileItem item : fileItems) {
			if (!item.isFormField()) {
				try {
					String contents = item.getString();
					List<TreeInfo> trees = fileUploadedEvent.fileUploaded(contents, item.getName());
					for (TreeInfo treeInfo : trees) {
						Map map = new HashMap();
						map.put("File Name", treeInfo.getFilename());
						map.put("Uploaded Date/Time", treeInfo.getUploaded().toString());
						map.put("Label", treeInfo.getTreeName());

						data_list.add(map);
					}
					JSONArray jsonArray = JSONArray.fromObject(data_list);
					root.put("data", jsonArray);

					json = JSONObject.fromObject(root);

					System.out.println("filename ==>" + item.getName()
							+ " size ==>" + item.getSize());
				} catch (Exception e) {
					e.printStackTrace();
					throw new UploadActionException("Upload failed!");
				}
			} else {
				if (item.getFieldName().equals("file-type")) {
					filetype = new String(item.get());
				}
			}
		}
		// remove files from session. this avoids duplicate submissions
		removeSessionFileItems(request, false);
		if (json != null)
			return json.toString();
		else
			return null;

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		fileUploadedEvent = (FileUploadedEvent) MuleServer.getMuleContext()
				.getRegistry().lookupObject("fileUploadedEvent");
		super.init(config);
	}

	public void setFileUploadedEvent(FileUploadedEvent fileUploadedEvent) {
		this.fileUploadedEvent = fileUploadedEvent;
	}
}
