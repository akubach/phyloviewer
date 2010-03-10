package org.iplantc.iptol.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.iplantc.iptol.client.services.ServiceCallWrapper;
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
	public static final String TYPE = "type";
	public static final String ID = "id";

	/**
	 * This method is automatically called for file upload request
	 */
	private static final long serialVersionUID = 1L;
	private FileUploadedEvent fileUploadedEvent;

	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> fileItems) throws UploadActionException {

		this.maxSize = MAX_FILE_SIZE;
		this.uploadDelay = UPLOAD_DELAY;

		String json = null;
		String idFolder = null;
		String idWorkspace = null;
		String filename = null;
		String bodyFile = null;
		
		for (FileItem item : fileItems) {
			if(item.isFormField()){
				String name = item.getFieldName();
				String contents = new String(item.get());
				
				if(name.equals("folderid"))
				{
					idFolder = contents;
				}
				else if(name.equals("workspaceid"))
				{
					idWorkspace = contents;
				}					
			}
			else{
				filename = item.getName();
				bodyFile = new String(item.get());
				/*try {
					String contents = new String(item.get());
					System.out.println("contents==>" + contents + "name==>" + item.getName());
					
					List<FileInfo> fileInfos = fileUploadedEvent.fileUploaded(
							contents, item.getName());
					for (FileInfo fileInfo: fileInfos) {
						Map map = new HashMap();
						//mock parsing status for now. 
						//TODO create status object and a method to retrieve status of parsing jobs
						map.put(FILE_NAME, fileInfo.getName());
						map.put(DATE_TIME, fileInfo.getUploaded());
						map.put(LABEL, fileInfo.getName());
						map.put(TYPE,fileInfo.getType());
						map.put(ID,fileInfo.getId());
						
						data_list.add(map);
						JSONArray jsonArray = JSONArray.fromObject(data_list);
						json = jsonArray.toString();
						//root.put("data", jsonArray);
						System.out.println("filename ==>" + item.getName()
							+ " size ==>" + item.getSize());
						System.out.println("json==> " + json);
					} 
				} catch (Exception e) {
					e.printStackTrace();
					json = null;
					throw new UploadActionException("Upload failed!");
				}*/
			} 
		}
		
		//do we have enough information to make a service call?
		if(idWorkspace != null && idFolder != null && filename != null && bodyFile != null)
		{
			String address = "http://localhost:14444/workspace/" + idWorkspace + "/files";
			String body = "filename=" + filename  + "&p;folderId=" + idFolder + "&f;" + bodyFile;
			ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST,address,body);
			
			IptolServiceDispatcher dispatcher = new IptolServiceDispatcher();
			json = dispatcher.getServiceData(wrapper);
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
