package uk.ac.ebi.fg.java2rdf.mapping.rdfgen;

import java.util.Map;

import org.apache.commons.rdf.api.Literal;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapper;


/**
 * Subclasses of this are used to generate an RDF-related value RV from a Java one T. 
 * For instance, a {@link Literal} or a String that contains a URI.
 * 
 * @author brandizi
 * <dl><dt>Date:</dt><dd>24 Nov 2017</dd></dl>
 *
 */
public abstract class RdfValueGenerator<T,RV> extends RdfMapper<T>
{
	public abstract RV getValue ( T source, Map<String, Object> params );
}
