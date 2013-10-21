package uk.ac.ebi.fg.java2rdf.mapping;


/**
 * TODO: COMMENT ME AGAIN!
 * 
 * This is the bare minimum that is expected from each mapper, essentially, a mapper keeps a pointer to the {@link RdfMapperFactory}
 * that uses it and does its job when the method {@link #map(Object)} is invoked. 
 *
 * <dl><dt>date</dt><dd>Mar 23, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public abstract class RdfMapper<T>
{
	private RdfMapperFactory mapperFactory;
	
	public RdfMapperFactory getMapperFactory () {
		return mapperFactory;
	}

	public void setMapperFactory ( RdfMapperFactory mapperFactory ) {
		this.mapperFactory = mapperFactory;
	}
	
}
