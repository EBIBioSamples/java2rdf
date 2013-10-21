package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 * @param <T>
 * @param <PT>
 */
public abstract class PropertyRdfMapper<T, PT> extends RdfMapper<T>
{
	protected Logger log = LoggerFactory.getLogger ( this.getClass () );

	public PropertyRdfMapper ()
	{
		super ();
	}

	/**
	 * <p>Implements a specific way to map the property value of this source.
	 * You should take care of the case propValue == null, while {@link #map(Object)} takes care of the null source.</p>
	 * 
	 * <p>This usually creates a statement having the source as subject (typically uses {@link RdfMapperFactory#getRdfUriGenerator(Object)}
	 * for its URI), {@link #getTargetPropertyUri()} as RDF/OWL property and a value or another URI as object (again, it
	 * usually uses the factory for that).</p>
	 * 
	 * <p>As usually, it returns true when a real addition occurs.</p>
	 */
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		if ( propValue == null ) return false;
		return true;
	}

	public final boolean map ( T source, PT propValue ) {
		return map ( source, propValue, null );
	}
}
