package org.iplantc.de.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.iplantc.treedata.model.Thing;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class WrapSpecies extends AbstractTransformer
{

	@SuppressWarnings("unchecked")
	@Override
	protected Object doTransform(Object arg0, String arg1) throws TransformerException
	{
		Collection<Thing> things = (Collection<Thing>)arg0;
		Set<Species> species = new HashSet<Species>();
		for(Thing thing : things)
		{
			species.add(new Species(thing.getId().toString(), thing.getName()));
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("species", species);
		return map;
	}

	public class Species
	{
		private String id;
		private String name;

		public Species()
		{

		}

		public Species(String id, String name)
		{
			this.id = id;
			this.name = name;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		public String getId()
		{
			return id;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

	}
}
