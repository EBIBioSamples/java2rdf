package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.rdf.api.Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.ObjRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfValueGenerator;

/**
 * A generic mapper that maps a pair of Java objects, which are related by a binary relationship, into an RDF 
 * statement and using an RDF predicate (i.e., property). 
 * 
 * Note that this mapper doesn't necessarily map to a single RDF statement, nor does it always use the same 
 * OWL/RDF property, since there are use cases more generic than that. If you need such specific use case, use 
 * {@link UriProvidedPropertyRdfMapper}.
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 * @param <T> the type of the Java object that plays the role of subject in the mapping worked out by this mapper.
 * @param <PT> the type of the Java object that plays the role of object in the mapping worked out by this mapper.
 * @param <RV> the type of RDF entity that this mapper generates for values of PT, eg, a {@link Literal} or
 * a String for URIs. 
 * 
 */
public abstract class PropertyRdfMapper<T, PT, RV> extends RdfMapper<T>
{
	private RdfValueGenerator<PT, RV> rdfValueGenerator = null; 
	
	protected Logger log = LoggerFactory.getLogger ( this.getClass () );

	public PropertyRdfMapper ()
	{
		super ();
	}

	/**
	 * (source, propValue) are intended to be an element of a binary relation R. This method defines how such object
	 * pair spawns an RDF statement (or more than one). The default version here just checks that propValue != null, return
	 * false if it is. The final RDF target value gathered from propValue should be generated taking into account 
	 * {@link #getRdfValueGenerator()}.
	 * 
	 * Note that this implementation raises an exception if source is null. 
	 * 
	 * @see ObjRdfMapper#map(Object, Map) for further information about params and return value.
	 * 
	 */
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		Validate.notNull ( source, "Internal error: cannot map a null source object to RDF" );
		if ( propValue == null ) return false;
		return true;
	}

	/**
	 * Usual wrapper with params = null.
	 */
	public final boolean map ( T source, PT propValue ) {
		return map ( source, propValue, null );
	}

	/**
	 * When it's not null, this should be used to generate the object value (i.e., a literal, or a URI) in the target RDF 
	 * statement that maps a Java object value, provided by the JavaBean property that this mapper deals with.
	 *  
	 * If this is null, {@link #map(Object, Object, Map)} should default to something else (e.g., ask the 
	 * {@link RdfMapperFactory} to give a {@link RdfValueGenerator} for the property target). 
	 */
	public RdfValueGenerator<PT,RV> getRdfValueGenerator ()
	{
		return rdfValueGenerator;
	}

	public void setRdfValueGenerator ( RdfValueGenerator<PT,RV> rdfValueGenerator )
	{
		this.rdfValueGenerator = rdfValueGenerator;
	}

	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( this.rdfValueGenerator != null ) 		this.rdfValueGenerator.setMapperFactory ( this.getMapperFactory () );
	}
}
