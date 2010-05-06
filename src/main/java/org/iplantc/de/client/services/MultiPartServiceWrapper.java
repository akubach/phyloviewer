package org.iplantc.de.client.services;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a wrapper for services dealing with multi-part data.
 */
public class MultiPartServiceWrapper extends BaseServiceCallWrapper
{
	private static final long serialVersionUID = 1L;
	private List<HTTPPart> parts = new ArrayList<HTTPPart>();

	public MultiPartServiceWrapper()
	{
		super();
	}

	public MultiPartServiceWrapper(String address)
	{
		super(address);
	}

	public MultiPartServiceWrapper(Type type, String address)
	{
		super(type, address);
	}

	public int getNumParts()
	{
		return parts.size();
	}

	public List<HTTPPart> getParts()
	{
		return parts;
	}

	public void addPart(String body, String disposition)
	{
		parts.add(new HTTPPart(body, disposition));
	}
}
