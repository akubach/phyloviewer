package org.iplantc.de.server;

/**
 * A ParamPart represents one parameter (name/value pair) in an HTTP multipart body.
 * @author Donald A. Barre
 */
public class ParamPart extends Part {

	public ParamPart(String name, String contents) {
		super(name, contents.trim());
	}
}
