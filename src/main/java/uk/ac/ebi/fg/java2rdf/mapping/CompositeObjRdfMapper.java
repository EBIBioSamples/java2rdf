package uk.ac.ebi.fg.java2rdf.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * TODO: Comment me!
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

	public CompositeObjRdfMapper ( ObjRdfMapper<T>... mappers ) 
	{
		if ( mappers != null ) this.setMappers ( Arrays.asList ( mappers ) );
	}
	
	@Override
	public boolean map ( T source, Map<String, Object> params )
	{
		if ( mappers == null || mappers.isEmpty () ) return false;
		
		boolean result = false;
		for ( ObjRdfMapper<T> mapper: mappers )
			result |= mapper.map ( source, params );
		
		return result;
	}

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
