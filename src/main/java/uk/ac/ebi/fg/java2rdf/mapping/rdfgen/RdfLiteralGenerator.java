package uk.ac.ebi.fg.java2rdf.mapping.rdfgen;

import static uk.ac.ebi.fg.java2rdf.utils.Java2RdfUtils.RDF_GRAPH_UTILS;

import java.util.Map;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import info.marcobrandizi.rdfutils.GraphUtils;
import info.marcobrandizi.rdfutils.jena.JenaGraphUtils;
import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.LiteralPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.utils.Java2RdfUtils;

/**
 * Generates a literal value (a string at the moment) that corresponds to a JavaBean object in a RDF representation.
 * 
 * For example, you may want to map the instance b of Book, having p.title = 'I, Robot' to 'I, Robot' and use such value for a 
 * {@link LiteralPropRdfMapper data type property mapper} about the OWL property 'dc:title', to be applied 
 * to a {@link BeanRdfMapper} for Person. See examples in the test package for details. Another slightly more complex 
 * example might be when you map Book to OWL:Citation, which of dc:label property is set with values like 
 * 'I, Robot, by Isac Asimov, Penguin Books, ISBN 123'. You will construct such a string inside a {@link RdfLiteralGenerator}.
 * 
 * @See LiteralPropRdfMapper.
 *
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class RdfLiteralGenerator<T> extends RdfValueGenerator<T, Literal>
{
	private boolean isEmptyStringNull = true;
		
	public RdfLiteralGenerator () {
	}

	public RdfLiteralGenerator ( boolean isEmptyStringNull ) {
		this.isEmptyStringNull = isEmptyStringNull;
	}

	/** It invokes {@link #getLiteral(Object)}, redefine that. */
	@Override
	public final Literal getValue ( T source, Map<String, Object> params )
	{
		return getLiteral ( source, params );
	}
	
	/**
	 * Here it is where the real job happens and then {@link #getValue(Object)} is implemented as a synonym of this.
	 * 
	 * The default implementation converts to a generic text literal if T is of string type (without datatype 
	 * and without language), it uses {@link GraphUtils#value2TypedLiteral(Graph, Object)}
	 * for other types (i.e., converts common Java types to XSD correspondent types).
	 *  
	 */
	public Literal getLiteral ( T source, Map<String, Object> params ) 
	{
		if ( source == null ) return null;
		
		Model g = this.getMapperFactory ().getGraphModel ();
		
		if ( ! ( source instanceof String ) )
			return RDF_GRAPH_UTILS.value2TypedLiteral ( g, source ).orElse ( null );
		
		String srcStr = (String) source;
		if ( this.isEmptyStringNull && srcStr.isEmpty () ) srcStr = null;
		return RDF_GRAPH_UTILS.value2Literal ( g, srcStr ).orElse ( null );
	}
	
	public Literal getLiteral ( T source ) {
		return getLiteral ( source, null );
	}

	public boolean isEmptyStringNull ()
	{
		return isEmptyStringNull;
	}

	public void setEmptyStringNull ( boolean isEmptyStringNull )
	{
		this.isEmptyStringNull = isEmptyStringNull;
	}
	
}
