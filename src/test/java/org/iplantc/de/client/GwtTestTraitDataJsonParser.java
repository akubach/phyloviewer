package org.iplantc.de.client;

import org.iplantc.de.client.utils.TraitDataJsonParser;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestTraitDataJsonParser extends GWTTestCase {
	
	private TraitDataJsonParser parser;

	@Override
	public String getModuleName() {
		return "org.iplantc.de.discoveryenvironment";
	}
	
	@Override
	public void gwtTearDown() {
		parser = null;
	}
	
	public void testParserNull() {
		String json = null;
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNull(parser.getData());
		assertNull(parser.getHeader());
		assertNull(parser.getRoot());
	}
	
	
	public void testParserEmpty() {
		String json = "";
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNull(parser.getData());
		assertNull(parser.getHeader());
		assertNull(parser.getRoot());
	}
	
	
	public void testParserHeaderOnly() {
		String json = "{\"headers\":[{\"id\":\"0\",\"label\":\"species\"},{\"id\":\"1234\",\"label\":\"flowerSize\"},{\"id\":\"2345\",\"label\":\"color\"}]}";
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNotNull(parser.getHeader());
		assertNull(parser.getData());
		assertNotNull(parser.getRoot());
	}
	
	public void testParserEmptyHeaderOnly() {
		String json = "{\"headers\":[]}";
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNotNull(parser.getHeader());
		assertNull(parser.getData());
		assertNotNull(parser.getRoot());
	}
	
	public void testParserEmptyHeaderEmptyData() {
		String json = "{\"headers\":[]," +
	      "\"data\":[" +
	      			"]}";
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNotNull(parser.getHeader());
		assertNotNull(parser.getData());
		assertNotNull(parser.getRoot());
		
	}
	public void testParserHeaderEmptyData() {
		String json = "{\"headers\":[{\"id\":\"0\",\"label\":\"species\"},{\"id\":\"1234\",\"label\":\"flowerSize\"},{\"id\":\"2345\",\"label\":\"color\"}]," +
	      "\"data\":[" +
	      			"]}";
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNotNull(parser.getHeader());
		assertNotNull(parser.getData());
		assertNotNull(parser.getRoot());
		
	}
	
	
	public void testParserJson() {
		String json = "{\"headers\":[{\"id\":\"0\",\"label\":\"species\"},{\"id\":\"1234\",\"label\":\"flowerSize\"},{\"id\":\"2345\",\"label\":\"color\"}],"
			+ "\"data\":[{\"id\":\"1340\",\"values\":[\"foo\",\"4.3\",\"0\"]},"
			+ "          {\"id\":\"1339\",\"values\":[\"bar\",\"1.3\",\"1\"]}" + "]}";
		
		parser = new TraitDataJsonParser(json);
		parser.parseRoot();
		assertNotNull(parser.getHeader());
		assertNotNull(parser.getData());
		assertNotNull(parser.getRoot());
	}
	
	

}
