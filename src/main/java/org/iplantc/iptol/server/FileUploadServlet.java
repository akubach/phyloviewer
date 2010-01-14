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
 * class provided by the GWT Upload library. The executeAction method must be overridden for
 * custom behavior. 
 * 
 * @author sriram
 * 
 */
public class FileUploadServlet extends UploadAction {

	public static final long MAX_FILE_SIZE = 3145728;

	public static final int UPLOAD_DELAY = 0;
	
	public static final String FILE_NAME = "name";
	public static final String LABEL = "label";
	public static final String DATE_TIME = "uploaded";
	public static final String DESCRIPTION = "type";
	public static final String ID = "id";

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
		String json = null;
		for (FileItem item : fileItems) {
			if (!item.isFormField()) {
				try {
					String contents = new String(item.get());
					System.out.println("contents==>" + contents + "name==>" + item.getName());
					List<TreeInfo> trees = fileUploadedEvent.fileUploaded(
							contents, item.getName());
					for (TreeInfo treeInfo : trees) {
						Map map = new HashMap();
						//mock parsing status for now. 
						//TODO create status object and a method to retrieve status of parsing jobs
						map.put("Status","Ready");
						map.put(FILE_NAME, treeInfo.getFilename());
						map.put(DATE_TIME, treeInfo.getUploaded()
								.toString());
						map.put(LABEL, treeInfo.getTreeName());
						//mock description of the uploded file
						map.put("type","A Nexus file with tree");
						data_list.add(map);
					}
					JSONArray jsonArray = JSONArray.fromObject(data_list);
					json = jsonArray.toString();
					//root.put("data", jsonArray);
					System.out.println("filename ==>" + item.getName()
						+ " size ==>" + item.getSize());
					System.out.println("json==> " + json);
				} catch (Exception e) {
					e.printStackTrace();
					json = null;
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
		
		return json;
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
