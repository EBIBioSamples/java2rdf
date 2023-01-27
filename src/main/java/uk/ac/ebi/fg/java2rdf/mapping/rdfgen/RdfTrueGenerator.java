package uk.ac.ebi.fg.java2rdf.mapping.rdfgen;

import java.util.Map;

import org.apache.jena.rdf.model.Literal;

/**
 * A {@link RdfLiteralGenerator literal generator} for booleans that returns null for the false value.
 * This can be useful when you want to report true values only and you consider false as default.
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
		if ( !Boolean.TRUE.equals ( source ) ) return null;
		return super.getLiteral ( source, params );
	}
}
