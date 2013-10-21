package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.fg.java2rdf.mapping.ObjRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;

/**
 * Maps a property in a JavaBean (i.e., getXXX) to an RDF statement, see details below. 
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class BeanPropRdfMapper<T, PT> extends ObjRdfMapper<T>
{
	private String sourcePropertyName = null;
	private PropertyRdfMapper<T, PT> propertyMapper = null;
	
	
	public BeanPropRdfMapper () {
		this ( null, null );
	}

	public BeanPropRdfMapper ( String sourcePropertyName, PropertyRdfMapper<T, PT> propertyMapper )
	{
		super ();
		this.setSourcePropertyName ( sourcePropertyName );
		this.setPropertyMapper ( propertyMapper );
	}


	/**
	 * Gets the bean property indicated by {@link #getSourcePropertyName()} and maps it to an RDF statement about the bean
	 * and its property value, by means of {@link #getPropertyMapper()}.
	 * 
	 * If there is no property mapper defined or the upper implementation returns false, then this method returns false too
	 * and doesn't do any mapping.
	 * 
	 */
	@Override
	@SuppressWarnings ( "unchecked" )
	public final boolean map ( T source, Map<String, Object> params ) throws RdfMappingException
	{
		if ( propertyMapper == null ) return false;

		PT pval = null;
		try
		{
			pval = (PT) PropertyUtils.getSimpleProperty ( source, sourcePropertyName );
			return propertyMapper.map ( source, pval, params );
		} 
		catch ( Exception ex )
		{
			throw new RdfMappingException ( String.format ( 
				"Internal error while mapping %s[%s].'%s'='%s' to RDF: %s", 
				source.getClass ().getSimpleName (), 
				StringUtils.abbreviate ( source.toString (), 50 ), sourcePropertyName, 
				pval == null ? null : StringUtils.abbreviate ( pval.toString(), 50 ),
				ex.getMessage ()
			), ex);
		}
	}
	
	/**
	 * The Java bean property that this mappes maps to RDF. E.g., if this returns 'Name', {@link #map(Object, Map)} will
	 * invoke getName() to get an object value to be sent to the {@link #getPropertyMapper() property mapper}.   
	 */
	public String getSourcePropertyName ()
	{
		return sourcePropertyName;
	}

	public void setSourcePropertyName ( String sourcePropertyName )
	{
		this.sourcePropertyName = sourcePropertyName;
	}

	/**
	 * This is invoked by {@link #map(Object, Map)} to map the source parameter and the property targeted by {@link #getSourcePropertyName()}
	 * to an RDF statement.
	 * 
	 */
	public PropertyRdfMapper<T, PT> getPropertyMapper ()
	{
		return propertyMapper;
	}

	public void setPropertyMapper ( PropertyRdfMapper<T, PT> propertyMapper )
	{
		this.propertyMapper = propertyMapper;
	}

	/**
	 * This sets the factory of {@link #getPropertyMapper()} too.
	 */
	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( this.propertyMapper != null ) this.propertyMapper.setMapperFactory ( mapperFactory );
	}

	
}
