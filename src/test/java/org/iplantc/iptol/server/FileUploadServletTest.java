package org.iplantc.iptol.server;

import gwtupload.server.exceptions.UploadActionException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.MockHttpServletRequest;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class MockHttpServletRequestWithSession extends MockHttpServletRequest {
	
	HttpSession session;
	
	public MockHttpServletRequestWithSession(byte[] requestData,
			String strContentType) {
		super(requestData, strContentType);
		session = new MockHttpSession();
		
	}

	@Override
	public HttpSession getSession() {
		return session;
	}
}

public class FileUploadServletTest {

	private FileUploadServlet servlet;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		 servlet = new FileUploadServlet();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		servlet = null;
	}
	
	@Test
	public void testMock() {
		HttpServletRequest request = new  MockHttpServletRequestWithSession(new byte[0],"multipart/form-data; boundary=---1234");
		Assert.assertNotNull(request);
		Assert.assertNotNull(request.getSession());
	}
	@SuppressWarnings("unchecked")
	@Test
	public void executeActionTest() throws UnsupportedEncodingException, FileUploadException, UploadActionException{
		String request_body = "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"file\"; filename=\"foo.tab\"\r\n" +
        "Content-Type: text/whatever\r\n" +
        "\r\n" +
        "This is the content of the file\n" +
        "\r\n" +
        "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"field\"\r\n" +
        "\r\n" +
        "fieldValue\r\n" +
        "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"multi\"\r\n" +
        "\r\n" +
        "value1\r\n" +
        "-----1234\r\n" +
        "Content-Disposition: form-data; name=\"multi\"\r\n" +
        "\r\n" +
        "value2\r\n" +
        "-----1234--\r\n";

		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        HttpServletRequest request = new MockHttpServletRequestWithSession(request_body.getBytes(), "multipart/form-data; boundary=---1234");
        System.out.println("session=" + request.getSession());
        List fileItems = upload.parseRequest(request);
		String json = servlet.executeAction(request, fileItems);
		Assert.assertNotNull(json);
	
	}
	@Test
	public void testUploadFileNoFile() throws IOException, FileUploadException, UploadActionException {
		String request_body = "-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"\"\r\n" +
                "\r\n" +
                "\r\n" +
                "-----1234--\r\n";
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        HttpServletRequest request = new MockHttpServletRequestWithSession(request_body.getBytes(), "multipart/form-data; boundary=---1234");
        List fileItems = upload.parseRequest(request);
        String json=servlet.executeAction(request, fileItems);
        Assert.assertNotNull(json);
	}
	/**
	 * Test method for {@link org.ipc.iptol.web.RequestHandlerServlet#uploadFile(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 * @throws FileUploadException 
	 * @throws UploadActionException 
	 */
	@Test
	public void testUploadFileNoFileField() throws IOException, FileUploadException, UploadActionException {
		String request_body = "-----1234\r\n" +
                "Content-Disposition: form-data; name=\"field1\"\r\n" +
                "\r\n" +
                "fieldValue\r\n" +
                "-----1234\n" + // NOTE \r missing
                "Content-Disposition: form-data; name=\"submitName.x\"\r\n" +
                "\r\n" +
                "42\r\n" +
                "-----1234\n" + // NOTE \r missing
                "Content-Disposition: form-data; name=\"submitName.y\"\r\n" +
                "\r\n" +
                "21\r\n" +
                "-----1234\r\n" +
                "Content-Disposition: form-data; name=\"field2\"\r\n" +
                "\r\n" +
                "fieldValue2\r\n" +
                "-----1234--\r\n";
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        HttpServletRequest request = new MockHttpServletRequestWithSession(request_body.getBytes(), "multipart/form-data; boundary=---1234");
        List fileItems = upload.parseRequest(request);
		String json = servlet.executeAction(request, fileItems);
		Assert.assertNull(json);
}
}
