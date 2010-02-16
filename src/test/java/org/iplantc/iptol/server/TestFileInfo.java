package org.iplantc.iptol.server;

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple unit test for FileInfo data transfer object.  
 * 
 * Given that the class is really just a POJO and will not have complex logic 
 * one might argue this is not needed.  It appears as a "canary test" and 
 * could be removed if its' existence is upsetting. 
 * 
 * @author lenards
 *
 */
public class TestFileInfo {
	private FileInfo fileInfo;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fileInfo = new FileInfo();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		fileInfo = null;
	}
	
	/**
	 * A dead simple test to verify the object is constructed and that values 
	 * can be set via getters/setters.  File under: "Canary Tests" 
	 */
	@Test
	public void testObjectConstruction() {
		Assert.assertNotNull(fileInfo);

		String now = new Date().toString();
		
		Assert.assertNotSame(Long.valueOf(650), fileInfo.getId());
		Assert.assertNotSame("simpleTest", fileInfo.getName());
		Assert.assertNotSame(now, fileInfo.getUploaded());
				
		fileInfo.setId(Long.valueOf(650));
		fileInfo.setName("simpleTest");
		fileInfo.setUploaded(now);
		
		Assert.assertEquals(Long.valueOf(650), fileInfo.getId());
		Assert.assertEquals("simpleTest", fileInfo.getName());
		Assert.assertEquals(now, fileInfo.getUploaded());
	}
}
