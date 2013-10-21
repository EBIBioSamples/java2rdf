package uk.ac.ebi.fg.java2rdf.mapping.urigen;

import java.util.Map;

/**
 * <p>An RDF value generator takes a JavaBean object and generates a proper RDF identifier of such object, which has to 
 * be either a {@link RdfUriGenerator URI} or a {@link RdfLiteralGenerator literal value}.</p>
 * 
 * <p>Usually there is a RDF generator per Java class, but it might be more complex than that depends on what you do 
 * in {@link #getValue(Object)}.</p>
 *
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public abstract class RdfValueGenerator<T>
{
	/**
	 * At the moment the result is always a string, in future it might me something more complex like a string + a language. 
	 */
	public abstract String getValue ( T source, Map<String, Object> params );
	
	public final String getValue ( T source ) {
		return getValue ( source, null );
	} 
}
