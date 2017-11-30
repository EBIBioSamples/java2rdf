package uk.ac.ebi.fg.java2rdf.mapping.properties;

import static info.marcobrandizi.rdfutils.commonsrdf.CommonsRDFUtils.COMMUTILS;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.rdf.api.Literal;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfLiteralGenerator;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfValueGenerator;

/**
 * This maps a pair of Java objects by means of RDF literal values for the target of some property.
 *  
 * For instance, you may use this mapper to map Book.getTitle() via dc:title. The RDF literal value for 
 * such a mapping (i.e., the book title) is created by means of {@link #getLiteralGenerator()}. 
 * 
 * @See {@link RdfLiteralGenerator}.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class LiteralPropRdfMapper<T, PT> extends UriProvidedPropertyRdfMapper<T, PT, Literal>
{	 
	public LiteralPropRdfMapper ()  {
		this ( null, new RdfLiteralGenerator<PT> () );
	}

	public LiteralPropRdfMapper ( String targetPropertyUri )
	{
		this ( targetPropertyUri, new RdfLiteralGenerator<PT> () );
	}

	public LiteralPropRdfMapper ( String targetPropertyUri, RdfLiteralGenerator<PT> rdfLiteralGenerator ) 
	{
		super ( targetPropertyUri );
		this.setLiteralGenerator ( rdfLiteralGenerator );
	}
	
	/**
	 * This maps (source, propValue) via {@link #getTargetPropertyUri()}. Uses {@link RdfMapperFactory#getUri(Object, Map)}
	 * to get the URI of source, and {@link #getLiteralGenerator()} to get the literal value for propValue.
	 */
	@Override
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		try
		{
			if ( !super.map ( source, propValue, params ) ) return false;
			
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			Validate.notNull ( mapFactory, "Internal error: %s must be linked to a mapper factory", this.getClass ().getSimpleName () );

			String subjUri = mapFactory.getUri ( source, params );
			if ( subjUri == null ) return false;

			RdfLiteralGenerator<PT> targetValGen = this.getLiteralGenerator ();
			Validate.notNull ( 
				mapFactory, 
				"Internal error: the " + this.getClass ().getSimpleName () + " mapper requires a literal generator" 
			);
			
			Literal targetRdfVal = targetValGen.getValue ( propValue, params );
			if ( targetRdfVal == null ) return false;
			
			COMMUTILS.assertLiteral ( 
				this.getMapperFactory ().getGraphModel (), 
				subjUri, 
				this.getTargetPropertyUri (),
				targetRdfVal
			);
			
			return true;
		} 
		catch ( Exception ex )
		{
			throw new RdfMappingException ( String.format ( 
				"Error while doing the RDF mapping <%s[%s] '%s' [%s]>: %s", 
					source.getClass ().getSimpleName (), 
					StringUtils.abbreviate ( source.toString (), 50 ), 
					this.getTargetPropertyUri (),
					StringUtils.abbreviate ( propValue.toString (), 50 ), 
					ex.getMessage ()
			), ex );
		}
	}

	/**
	 * This generates the literal value to be used to map the value of an object property to RDF.
	 * By default, it uses {@link RdfLiteralGenerator}, which maps most common Java types to XSD types.
	 * 
	 * This is a convenience wrapper of {@link #getRdfValueGenerator()}
	 */
	public RdfLiteralGenerator<PT> getLiteralGenerator () {
		return (RdfLiteralGenerator<PT>) this.getRdfValueGenerator ();
	}

	public void setLiteralGenerator ( RdfLiteralGenerator<PT> rdfLiteralGenerator ) {
		this.setRdfValueGenerator ( rdfLiteralGenerator );
	}

	
	/** 
	 * Forces the type to be a {@link RdfLiteralGenerator}.
	 */
	@Override
	public void setRdfValueGenerator ( RdfValueGenerator<PT, Literal> rdfValueGenerator )
	{
		if ( ! ( rdfValueGenerator != null && rdfValueGenerator instanceof RdfLiteralGenerator ) )
		{
			String msgTail = Optional.ofNullable ( rdfValueGenerator )
				.map ( g -> ", refusing the generator of type " + g.getClass ().getName () )
				.orElse ( "" );
			
			throw new IllegalArgumentException ( 
				"setRdfValueGenerator() must get a type of type RdfLiteralGenerator for " + this.getClass ().getSimpleName () 
				+ msgTail
			);
		}
		super.setRdfValueGenerator ( rdfValueGenerator );
	}
	
}
