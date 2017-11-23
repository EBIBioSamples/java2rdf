package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import info.marcobrandizi.rdfutils.commonsrdf.CommonsRDFUtils;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfValueGenerator;

/**
 * This maps a pair of Java objects by means of some OWL object type property. For instance, you may use this mapper
 * to map Book.getAuthor() via ex:has-author. Both the subject and object URI for the mapped RDF statement
 * (i.e., the book  and author's URIs) are taken from {@link RdfMapperFactory#getUri(Object, Map)}.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class OwlObjPropRdfMapper<T, PT> extends UriProvidedPropertyRdfMapper<T, PT>
{
	public OwlObjPropRdfMapper ()  {
		super ();
	}

	public OwlObjPropRdfMapper ( String targetPropertyUri ) {
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
			RdfValueGenerator<PT> objValGenerator = this.getRdfValueGenerator ();
			String objUri = objValGenerator != null 
				? objValGenerator.getValue ( propValue, params ) 
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
}
