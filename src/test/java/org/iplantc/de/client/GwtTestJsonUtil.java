package org.iplantc.de.client;

import org.iplantc.de.client.models.JsFile;
import org.iplantc.de.client.utils.JsonUtil;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestJsonUtil extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "org.iplantc.de.discoveryenvironment";
	}

	/**
	 * Test Data from the application: 
	 * [{"id":"36805","name":"foo.nex","status":"","type":"NEXUS with trait data","uploaded":"2010-04-24 11:14:37.191"},
	 *  {"id":"37122","name":"AminoAcid.nex","status":"","type":"NEXUS with unrecognized data","uploaded":"2010-04-24 11:46:26.338"},
	 *  {"id":"19791","name":"geospizamissingtraits.fel.nx","status":"","type":"NEXUS with trait data","uploaded":"2010-04-24 11:02:07.818"},
	 *  {"id":"37129","name":"DNA.nex","status":"","type":"NEXUS with unrecognized data","uploaded":"2010-04-24 11:46:47.318"},
	 *  {"id":"36298","name":"avian_ovomucoids.nex","status":"","type":"NEXUS with unrecognized data","uploaded":"2010-04-24 11:14:08.161"},
	 *  {"id":"36717","name":"Complex.nex","status":"","type":"NEXUS with tree data","uploaded":"2010-04-24 11:14:25.726"},
	 *  {"id":"22391","name":"PDAP.fel.tre","status":"","type":"Newick","uploaded":"2010-04-24 11:03:48.631"},
	 *  {"id":"18523","name":"aq.tree.fel","status":"","type":"Newick","uploaded":"2010-04-24 10:51:46.071"},
	 *  {"id":"36289","name":"MesquiteEquivocalExample.nex","status":"","type":"NEXUS with tree data","uploaded":"2010-04-24 11:13:08.815"},
	 *  {"id":"654135","name":"M111c2x3x96c16c46c54.NX","status":"","type":"NEXUS with tree data","uploaded":"2010-04-26 15:26:48.346"},
	 *  {"id":"21609","name":"shorebirds.fel.trait.nex","status":"","type":"NEXUS with trait data","uploaded":"2010-04-24 11:03:30.923"},
	 *  {"id":"19188","name":"PDAP.fel.nex","status":"","type":"NEXUS with trait data","uploaded":"2010-04-24 10:52:02.406"},
	 *  {"id":"18041","name":"aq.fel.trait.nex","status":"","type":"NEXUS with trait data","uploaded":"2010-04-24 10:51:32.508"},
	 *  {"id":"671461","name":"excel file for upload.csv","status":"","type":"CSV trait data","uploaded":"2010-04-26 16:30:24.804"},
	 *  {"id":"22765","name":"geospiza_valid.csv","status":"","type":"CSV trait data","uploaded":"2010-04-24 11:04:58.159"}]	
	 */
		public void testArrayOfWithFileInfoSingleQuote() 
		{
			JsArray<JsFile> fileInfos = JsonUtil.asArrayOf("[{'name':'foo', 'label':'tree1', 'uploaded':'', 'type':'Foo', 'id':'650'},"+
															  "{'name':'foo', 'label':'tree2', 'uploaded':'', 'type':'Bar', 'id':'344'}]");
			assertNotNull(fileInfos);
			
			JsFile fi1 = fileInfos.get(0);
			assertTrue(fi1.getId().equals("650"));
			assertTrue(fi1.getName().equals("foo"));
			assertTrue(fi1.getLabel().equals("tree1"));
			
			JsFile fi2 = fileInfos.get(1);
			assertTrue(fi2.getId().equals("344"));
			assertTrue(fi2.getName().equals("foo"));
		}
		
		public void testArrayOfWithFileInfoDoubleQuote() {
			JsArray<JsFile> fileInfos = JsonUtil.asArrayOf("[{\"name\":\"foo\", \"label\":\"tree1\", \"uploaded\":\"\", \"type\":\"Foo\", \"id\":\"650\"},"+
			  												  "{\"name\":\"foo\", \"label\":\"tree2\", \"uploaded\":\"\", \"type\":\"Bar\", \"id\":\"344\"}]");
			assertNotNull(fileInfos);
			
			JsFile fi1 = fileInfos.get(0);
			assertTrue(fi1.getId().equals("650"));
			assertTrue(fi1.getName().equals("foo"));
			assertTrue(fi1.getLabel().equals("tree1"));
			
			JsFile fi2 = fileInfos.get(1);
			assertTrue(fi2.getId().equals("344"));
			assertTrue(fi2.getName().equals("foo"));		
		}	
}
