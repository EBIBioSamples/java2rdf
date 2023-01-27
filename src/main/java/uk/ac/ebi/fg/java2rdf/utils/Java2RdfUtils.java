package uk.ac.ebi.fg.java2rdf.utils;

import java.util.Map;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.marcobrandizi.rdfutils.GraphUtils;
import info.marcobrandizi.rdfutils.jena.JenaGraphUtils;

/**
 * Some stuff useful for the RDF mapping job performed by the Java2RDF pacakge.
 *
 * <dl><dt>date</dt><dd>May 6, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Java2RdfUtils
{
	/**
	 * We're always using this {@link GraphUtils} implementation, based on Jena.
	 * We factorise it here, in order to at least make the consumers independent on the 
	 * specific implementation. In future, we might want to get this with a more sophisticated
	 * mechanism (eg, factory, SPI).
	 */
	public static final GraphUtils<Model, RDFNode, Resource, Property, Literal> RDF_GRAPH_UTILS
	= JenaGraphUtils.JENAUTILS;
	
	
	private static Logger log = LoggerFactory.getLogger ( Java2RdfUtils.class );
	
	private Java2RdfUtils () {}

	@SuppressWarnings ( "unchecked" )
	public static <V> V getParam ( Map<String, Object> params, String key, V defaultValue )
	{
		if ( params == null ) return defaultValue;
		return (V) params.getOrDefault ( key, defaultValue );
	}
	
	public static <V> V getParam ( Map<String, Object> params, String key )
	{
		return getParam ( params, key, null );
	}

}
