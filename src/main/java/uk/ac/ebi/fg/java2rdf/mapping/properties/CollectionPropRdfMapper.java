package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;

/**
 *  This is similar t {@link PropertyRdfMapper}, but it's aware that the Java object property to be mapped has multiple
 *  values, so {@link #map(Object, Collection, Map)} invokes the {@link #getPropertyMapper()} for each of such values.  
 *  
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class CollectionPropRdfMapper<T, PT> extends PropertyRdfMapper<T, Collection<PT>>
{
	private PropertyRdfMapper<T, PT> propertyMapper;
	
	public CollectionPropRdfMapper () {
		this ( null );
	}

	public CollectionPropRdfMapper ( PropertyRdfMapper<T, PT> propertyMapper ) 
	{
		this.setPropertyMapper ( propertyMapper );
	}

	/**
	 * Goes through all the values returned by propValues and does the mapping in a way similar to what happens in 
	 * {@link PropertyRdfMapper#map(Object, Object, Map)}, i.e., invoking {@link #getPropertyMapper()}
	 * for every value in the collection.
	 * 
	 * if propValues is null or empty returns false. Else, it returns true if there is at least one value in propValues
	 * for which {@link #getPropertyMapper()} returns true.
	 * 
	 */
	@Override
	public boolean map ( T source, Collection<PT> propValues, Map<String, Object> params )
	{
		try
		{
			if ( propValues == null || propValues.isEmpty () ) return false; 

			boolean result = false;
			for ( PT pvalue: propValues ) 
				result |= propertyMapper.map ( source, pvalue, params );
			
			return result;
		} 
		catch ( Exception ex )
		{
			throw new RdfMappingException ( String.format ( 
				"Error while doing the RDF mapping of <%s[%s] / [%s]: %s", 
					source.getClass ().getSimpleName (), 
					StringUtils.abbreviate ( source.toString (), 50 ), 
					StringUtils.abbreviate ( propValues.toString (), 50 ), 
					ex.getMessage ()
			), ex );
		}
	}

	/** The underlining property mapper used to map single values for the property {@link #getSourcePropertyName()} */
	public PropertyRdfMapper<T, PT> getPropertyMapper () {
		return propertyMapper;
	}

	
	public void setPropertyMapper ( PropertyRdfMapper<T, PT> propertyMapper ) 
	{
		this.propertyMapper = propertyMapper;
	}

	/**
	 * This sets the same factory for {@link #getPropertyMapper()} too, so you should call the latter before this.
	 */
	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( this.propertyMapper != null ) this.propertyMapper.setMapperFactory ( mapperFactory );
	}
}
