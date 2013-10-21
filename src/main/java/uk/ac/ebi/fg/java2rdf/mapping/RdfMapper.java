package uk.ac.ebi.fg.java2rdf.mapping;


/**
 * An RDF mapper allows you to map a Java entity, such as a Java Bean or one of its properties, to RDF statements.
 * 
 * This root class is the bare minimum that is expected from each mapper, essentially, a mapper keeps a pointer to the {@link RdfMapperFactory}
 * that uses it.
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
