package uk.ac.ebi.fg.java2rdf.mapping.rdfgen;

import java.util.Map;

import org.apache.commons.rdf.api.Literal;

/**
 * TODO: comment me!
 *
 * @author brandizi
 * <dl><dt>Date:</dt><dd>29 Nov 2017</dd></dl>
 *
 */
public class RdfTrueGenerator extends RdfLiteralGenerator<Boolean>
{
	public RdfTrueGenerator ()
	{
		super ( true );
	}

	public Literal getLiteral ( Boolean source, Map<String, Object> params )
	{
		if ( source == null || !source ) return null;
		return super.getLiteral ( source );
	}
}
