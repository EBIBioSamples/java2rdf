package uk.ac.ebi.fg.java2rdf.mapping.urigen;

import java.util.Map;

import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.OwlDatatypePropRdfMapper;

/**
 * Generates a literal value (a string at the moment) that corresponds to a JavaBean object in a RDF representation.
 * 
 * For example, you may want to map the instance b of Book, having p.title = 'I, Robot' to 'I, Robot' and use such value for a 
 * {@link OwlDatatypePropRdfMapper data type property mapper} about the OWL property 'dc:title', to be applied 
 * to a {@link BeanRdfMapper} for Person. See examples in the test package for details. Another slightly more complex 
 * example might be when you map Book to OWL:Citation, which of dc:label property is set with values like 
 * 'I, Robot, by Isac Asimov, Penguin Books, ISBN 123'. You will construct such a string inside a {@link RdfLiteralGenerator}.
 * 
 * @See OwlDatatypePropRdfMapper.
 * 
 * TODO: Review! We need to be more specific than string-only.
 *
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class RdfLiteralGenerator<T> extends RdfValueGenerator<T>
{
	/** It invokes {@link #getLiteral(Object)}, redefine that. */
	@Override
	public final String getValue ( T source, Map<String, Object> params )
	{
		return getLiteral ( source, params );
	}
	
	/**
	 * Here it is where the real job happens and then {@link #getValue(Object)} is implemented as a synonym of this.
	 */
	public String getLiteral ( T source, Map<String, Object> params ) {
		return source == null ? "" : source.toString ();
	}
	
	public String getLiteral ( T source ) {
		return getLiteral ( source, null );
	}
}
