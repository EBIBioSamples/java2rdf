package uk.ac.ebi.fg.java2rdf.mapping.rdfgen;

import java.util.Map;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapper;


/**
 * TODO: comment me.
 * 
 * @author brandizi
 * <dl><dt>Date:</dt><dd>24 Nov 2017</dd></dl>
 *
 * @param <T>
 * @param <RV>
 */
public abstract class RdfValueGenerator<T,RV> extends RdfMapper<T>
{
	public abstract RV getValue ( T source, Map<String, Object> params );
}
