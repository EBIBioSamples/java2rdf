package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import info.marcobrandizi.rdfutils.commonsrdf.CommonsRDFUtils;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfLiteralGenerator;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfUriGenerator;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfValueGenerator;

/**
 * This maps a pair of Java objects to a statement between resources.
 * 
 * For instance, you may use this mapper
 * to map Book.getAuthor() via ex:has-author. Both the subject and object URI for the mapped RDF statement
 * (i.e., the book  and author's URIs) are taken from {@link RdfMapperFactory#getUri(Object, Map)}.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class ResourcePropRdfMapper<T, PT> extends UriProvidedPropertyRdfMapper<T, PT, String>
{
	public ResourcePropRdfMapper ()  {
		super ();
	}

	public ResourcePropRdfMapper ( String targetPropertyUri ) {
		super ( targetPropertyUri );
	}
	
	
	/**
	 * Generates the RDF triple 
	 * ({@link #getMapperFactory() getMapperFactory(source, params)}, {@link #getTargetPropertyUri()}, 
	 *   {@link #getMapperFactory() getMapperFactory(propValue, params)} ).
	 */
	@Override
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		if ( !super.map ( source, propValue, params ) ) return false;
		
		try
		{
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			Validate.notNull ( mapFactory, "Internal error: %s must be linked to a mapper factory", this.getClass ().getSimpleName () );
			
			// TODO: can we avoid to keep recomputing these
			//
			
			// This is necessary, cause source/pval may be swapped by InversePropRdfMapper
			String subjUri = mapFactory.getUri ( source, params );
			if ( subjUri == null ) return false;

			// Gets the URI for the property target. This either uses the associated property generator, or it asks the 
			// factory to use the URI generator that is associated to the Java type the property target is instance of
			RdfUriGenerator<PT> valUriGenerator = this.getUriGenerator ();
			String objUri = valUriGenerator != null 
				? valUriGenerator.getUri ( propValue, params ) 
				: mapFactory.getUri ( propValue, params );
			
			if ( objUri == null ) return false;
			
			CommonsRDFUtils.COMMUTILS.assertResource ( 
				mapFactory.getGraphModel (), 
				subjUri,
				this.getTargetPropertyUri (), 
				objUri
			);

			// Don't use targetMapper directly, we need to trace this visit.
			return mapFactory.map ( propValue, params );
		} 
		catch ( Exception ex )
		{
			throw new RdfMappingException ( String.format ( 
				"Error while doing the RDF mapping <%s[%s] '%s' [%s]: %s", 
					source.getClass ().getSimpleName (), 
					StringUtils.abbreviate ( source.toString (), 50 ), 
					this.getTargetPropertyUri (),
					StringUtils.abbreviate ( propValue.toString (), 50 ), 
					ex.getMessage ()
			), ex );
		}
	}
	
	/**
	 * This generates the URIs for the property values that this property mapper targets.  
	 * 
	 * This is a convenience wrapper of {@link #getRdfValueGenerator()}
	 */
	public RdfUriGenerator<PT> getUriGenerator () {
		return (RdfUriGenerator<PT>) this.getRdfValueGenerator ();
	}

	public void setUriGenerator ( RdfUriGenerator<PT> uriGenerator ) {
		this.setRdfValueGenerator ( uriGenerator );
	}

	
	/** 
	 * Forces the type to be a {@link RdfUriGenerator}.
	 */
	@Override
	public void setRdfValueGenerator ( RdfValueGenerator<PT, String> rdfValueGenerator )
	{
		if ( ! ( rdfValueGenerator != null && rdfValueGenerator instanceof RdfUriGenerator ) )
		{
			String msgTail = Optional.ofNullable ( rdfValueGenerator )
				.map ( g -> ", refusing the generator of type " + g .getClass ().getName () )
				.orElse ( "" );
			
			throw new IllegalArgumentException ( 
				"setRdfValueGenerator() must get a type of type RdfUriGenerator for " + this.getClass ().getSimpleName () 
				+ msgTail
			);
		}
		super.setRdfValueGenerator ( rdfValueGenerator );
	}
	
}
