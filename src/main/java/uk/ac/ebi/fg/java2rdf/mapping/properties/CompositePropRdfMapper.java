package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class CompositePropRdfMapper<T, PT> extends PropertyRdfMapper<T, PT>
{
	private List<PropertyRdfMapper<T, PT>> propertyMappers;
	
	public CompositePropRdfMapper ()
	{
		this ( null );
	}

	public CompositePropRdfMapper ( PropertyRdfMapper<T, PT> ... propertyMappers )
	{
		if ( propertyMappers == null ) return;
		this.setPropertyMappers ( Arrays.asList ( propertyMappers ) );
	}

	public CompositePropRdfMapper ( String targetPropertyUri, URIProvidedPropertyRdfMapper<T, PT> ... propertyMappers ) 
	{
		this ( propertyMappers );
		if ( propertyMappers == null ) return;
		for ( URIProvidedPropertyRdfMapper<T, PT> pmapper: propertyMappers )
			pmapper.setTargetPropertyUri ( targetPropertyUri );
	}

	
	@Override
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		if ( propertyMappers == null || propertyMappers.isEmpty () ) return false;
		if ( !super.map ( source, propValue, params ) ) return false;
		
		boolean result = false;
		for ( PropertyRdfMapper<T, PT> mapper: this.propertyMappers )
			result |= mapper.map ( source, propValue, params );

		return result;
	}

	public List<PropertyRdfMapper<T, PT>> getPropertyMappers ()
	{
		return propertyMappers;
	}

	public void setPropertyMappers ( List<PropertyRdfMapper<T, PT>> propertyMappers )
	{
		this.propertyMappers = propertyMappers;
	}

	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( this.propertyMappers == null ) return;
		
		for ( PropertyRdfMapper<T, PT> pmapper: this.propertyMappers )
			pmapper.setMapperFactory ( mapperFactory );
	}
	
}
