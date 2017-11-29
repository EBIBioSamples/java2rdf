package uk.ac.ebi.fg.java2rdf.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A composite Java object mapper. 
 * 
 * Follows the composite pattern to maps a Java object onto multiple RDF statements and using multiple predefined 
 * {@link #getMappers() mappers}.
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 * @param <T>
 */
public class CompositeObjRdfMapper<T> extends ObjRdfMapper<T>
{
	private List<ObjRdfMapper<T>> mappers = null;
	
	public CompositeObjRdfMapper ()
	{
	}

	@SafeVarargs // TODO: value-by value param check
	public CompositeObjRdfMapper ( ObjRdfMapper<T>... mappers ) 
	{
		if ( mappers != null ) this.setMappers ( Arrays.asList ( mappers ) );
	}
	
	/**
	 * Forward the mapping to each {@link #getMappers() composite mapper}.
	 * @return true if at least one of the mapper component returns true.
	 */
	@Override
	public boolean map ( T source, Map<String, Object> params )
	{
		if ( source == null ) return false;
		if ( mappers == null || mappers.isEmpty () ) return true; 
		
		boolean result = false;
		for ( ObjRdfMapper<T> mapper: mappers ) 
			result |= mapper.map ( source, params );
		
		return result;
	}

	/**
	 * The composing mappers that this composite uses to realise the composite mapping. 
	 */
	public List<ObjRdfMapper<T>> getMappers ()
	{
		return this.mappers;
	}

	public void setMappers ( List<ObjRdfMapper<T>> mappers )
	{
		this.mappers = mappers;
	}

	public boolean addMapper ( ObjRdfMapper<T> mapper ) {
		return this.mappers.add ( mapper );
	}
	
	/**
	 * Automatically sets the factory of all the {@link #getPropertyMappers()} set so far.
	 */
	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( mappers == null ) return;
		
		for ( ObjRdfMapper<T> mapper: this.mappers )
			mapper.setMapperFactory ( mapperFactory );
	}
	
}
