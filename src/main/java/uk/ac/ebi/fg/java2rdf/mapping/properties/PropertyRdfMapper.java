package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapper;

/**
 * A generic mapper that maps a Java object pair, related by a binary relationship, i.e., a property, into an RDF 
 * statement. 
 * 
 * Note that this mapper doesn't necessarily maps to a single RDF statement, nor does it always with the same 
 * OWL/RDF property. There are cases more generic than that, if you need such specific use case, use 
 * {@link UriProvidedPropertyRdfMapper}.
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 * @param <T> the type of the Java object that plays the role of subject in the mapping worked out by this mapper.
 * @param <PT> the type of the Java object that plays the role of object in the mapping worked out by this mapper.
 */
public abstract class PropertyRdfMapper<T, PT> extends RdfMapper<T>
{
	protected Logger log = LoggerFactory.getLogger ( this.getClass () );

	public PropertyRdfMapper ()
	{
		super ();
	}

	/**
	 * (source, propValue) are intended to be an element of a binary relation R. This method defines how such object
	 * pair spawns an RDF statement (or more than one). The default version just checks that propValue != null, return
	 * false if it is.
	 * 
	 */
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		if ( propValue == null ) return false;
		return true;
	}

	/**
	 * Usual wrapper with params = null.
	 */
	public final boolean map ( T source, PT propValue ) {
		return map ( source, propValue, null );
	}
}
