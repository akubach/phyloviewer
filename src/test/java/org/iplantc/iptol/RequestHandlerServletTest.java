/**
 * 
 */
package org.iplantc.iptol;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.MockHttpServletRequest;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



/**
 * @author sriram
 *
 */
public class RequestHandlerServletTest {
	
	Mockery context = new Mockery();
	RequestHandlerServlet servlet;
	HttpServletResponse response;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		 response = context.mock(HttpServletResponse.class);
		 servlet = new RequestHandlerServlet();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		response = null;
		servlet = null;
	}

	/**
	 * Test method for {@link org.ipc.iptol.web.RequestHandlerServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	@Ignore
	public void testDoGetHttpServletRequestHttpServletResponse() {
	}

	/**
	 * Test method for {@link org.ipc.iptol.web.RequestHandlerServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws ServletException 
	 * @throws IOException 
	 */
	@Test
	@Ignore
	public void testDoPostHttpServletRequestHttpServletResponse() throws IOException, ServletException {
	}

	/**
	 * Test method for {@link org.ipc.iptol.web.RequestHandlerServlet#uploadFile(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public void testUploadFileValidFile() throws IOException {
		String file = "-----1234\r\n" +
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
		HttpServletRequest request = new MockHttpServletRequest(file.getBytes(),"multipart/form-data; boundary=---1234");
		byte[] content =servlet.uploadFile(request,response);
		System.out.println("file content=" + new String(content));
		Assert.assertEquals("This is the content of the file\n", new String(content));
	}
	/**
	 * Test method for {@link org.ipc.iptol.web.RequestHandlerServlet#uploadFile(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public void testUploadFileNoFile() throws IOException {
		String file = "-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"\"\r\n" +
                "\r\n" +
                "\r\n" +
                "-----1234--\r\n";
		HttpServletRequest request = new MockHttpServletRequest(file.getBytes(),"multipart/form-data; boundary=---1234");
		byte[] content =servlet.uploadFile(request,response);
		System.out.println("file content=" + new String(content) + ":");
		Assert.assertEquals(content.length, 0);
	}
	/**
	 * Test method for {@link org.ipc.iptol.web.RequestHandlerServlet#uploadFile(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 */
	@Test
	public void testUploadFileNoFileField() throws IOException {
		String file = "-----1234\r\n" +
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
		HttpServletRequest request = new MockHttpServletRequest(file.getBytes(),"multipart/form-data; boundary=---1234");
		byte[] content =servlet.uploadFile(request,response);
		Assert.assertNull(content);
	}
	

}
