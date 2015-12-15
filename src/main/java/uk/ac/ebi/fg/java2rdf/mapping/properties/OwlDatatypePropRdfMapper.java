package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfLiteralGenerator;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfValueGenerator;
import uk.ac.ebi.fg.java2rdf.utils.OwlApiUtils;

/**
 * This maps a pair of Java objects by means of some OWL data type property. For instance, you may use this mapper
 * to map Book.getTitle() via dc:title. The RDF literal value for such a mapping (i.e., the book title) is created
 * by means of {@link #getRdfLiteralGenerator()}. 
 * 
 * @See {@link RdfLiteralGenerator}.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class OwlDatatypePropRdfMapper<T, PT> extends UriProvidedPropertyRdfMapper<T, PT>
{	
	public OwlDatatypePropRdfMapper ()  {
		this ( null, new RdfLiteralGenerator<PT> () );
	}

	public OwlDatatypePropRdfMapper ( String targetPropertyUri )
	{
		this ( targetPropertyUri, new RdfLiteralGenerator<PT> () );
	}

	public OwlDatatypePropRdfMapper ( String targetPropertyUri, RdfLiteralGenerator<PT> rdfLiteralGenerator ) 
	{
		super ( targetPropertyUri );
		this.setRdfLiteralGenerator ( rdfLiteralGenerator );
	}
	
	/**
	 * This maps (source, propValue) via {@link #getTargetPropertyUri()}. Uses {@link RdfMapperFactory#getUri(Object, Map)}
	 * to get the URI of source, and {@link #getRdfLiteralGenerator()} to get the literal value for propValue.
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

			RdfLiteralGenerator<PT> targetValGen = this.getRdfLiteralGenerator ();
			Validate.notNull ( mapFactory, "Internal error: cannot map a OWL data property without a literal generator" );
			
			String targetRdfVal = targetValGen.getValue ( propValue, params );
			if ( targetRdfVal == null ) return false;
			
			OwlApiUtils.assertData ( this.getMapperFactory ().getKnowledgeBase (), 
				subjUri, this.getTargetPropertyUri (), targetRdfVal );
			
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
	 * This generates the literal value (a plain string at the moment) to be used to map an object which is the value
	 * of a JavaBean property into a string representation of such value.
	 * 
	 * This is a convenience wrapper of {@link #getRdfValueGenerator()}
	 */
	public RdfLiteralGenerator<PT> getRdfLiteralGenerator () {
		return (RdfLiteralGenerator<PT>) this.getRdfValueGenerator ();
	}

	public void setRdfLiteralGenerator ( RdfLiteralGenerator<PT> rdfLiteralGenerator ) {
		this.setRdfValueGenerator ( rdfLiteralGenerator );
	}

	
	/** 
	 * Forces the type to be a {@link RdfLiteralGenerator}.
	 */
	@Override
	public void setRdfValueGenerator ( RdfValueGenerator<PT> rdfValueGenerator )
	{
		if ( ! ( rdfValueGenerator != null && rdfValueGenerator instanceof RdfLiteralGenerator ) ) 
			throw new IllegalArgumentException ( 
				"setRdfValueGenerator() must get a type of type RdfValueGenerator for " + this.getClass ().getSimpleName () + 
				", refusing the type " + rdfValueGenerator.getClass ().getName ()
		); 
		super.setRdfValueGenerator ( rdfValueGenerator );
	}
	
}
