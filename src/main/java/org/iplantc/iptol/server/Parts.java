package org.iplantc.iptol.server;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class Parts {

	private Set<Part> parts = new HashSet<Part>();

	public Parts() {

	}

	public void add(Part part) {
		parts.add(part);
	}

	public Part getPart(String name) {
		for (Part part : parts) {
			if (StringUtils.equals(name, part.getName())) {
				return part;
			}
		}
		return null;
	}

	public FilePart getFilePart(String name) {
		for (Part part : parts) {
			if (part instanceof FilePart && StringUtils.equals(name, part.getName())) {
				return (FilePart) part;
			}
		}
		return null;
	}

	public ParamPart getParamPart(String name) {
		for (Part part : parts) {
			if (part instanceof ParamPart && StringUtils.equals(name, part.getName())) {
				return (ParamPart) part;
			}
		}
		return null;
	}
}
