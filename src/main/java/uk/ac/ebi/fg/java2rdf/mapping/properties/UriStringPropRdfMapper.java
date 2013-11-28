package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.utils.OwlApiUtils;

/**
 * Maps a Java String property that is assumed to contain an URI onto an RDF statement based on a property
 * like rdfs:seeAlso or even an object property. 
 *
 * <dl><dt>date</dt><dd>23 Oct 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class UriStringPropRdfMapper<T> extends UriProvidedPropertyRdfMapper<T, String>
{
	private boolean isObjectProperty = false; 
	
	public UriStringPropRdfMapper ()  {
		super ();
	}

	public UriStringPropRdfMapper ( String targetPropertyUri, boolean isObjectProperty ) {
		super ( targetPropertyUri );
		this.setObjectProperty ( isObjectProperty );
	}

	/**
	 * Defaults to false.
	 */
	public UriStringPropRdfMapper ( String targetPropertyUri ) {
		this ( targetPropertyUri, false );
	}

	
	/**
	 * <p>Generates the RDF triple 
	 * ({@link #getMapperFactory() getMapperFactory(source, params)}, {@link #getTargetPropertyUri()}, 
	 *   new {@link URI} ( propValue ) ).</p> 
	 *   
	 * <p>If propValue cannot be resolved to a proper URI, doesn't spawn anything and 
	 * logs an error (but doesn't throw any exception).</p>
	 * 
	 * <p>Will use {@link OwlApiUtils#assertAnnotationLink(org.semanticweb.owlapi.model.OWLOntology, String, String, String)}
	 * or {@link OwlApiUtils#assertLink(org.semanticweb.owlapi.model.OWLOntology, String, String, String)}, depending on
	 * {@link #isObjectProperty()}.</p>
	 * 
	 */
	@Override
	public boolean map ( T source, String propValue, Map<String, Object> params )
	{
		propValue = StringUtils.trimToNull ( propValue );
		if ( propValue == null ) return false;
		
		try
		{
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			
			// TODO: can we avoid to keep recomputing these
			//
			// This is necessary, cause source/pval may be swapped by InversePropRdfMapper
			String subjUri = mapFactory.getUri ( source, params );
			if ( subjUri == null ) return false;

			URI objUri = new URI ( propValue );
			
			if ( isObjectProperty )
				OwlApiUtils.assertLink ( this.getMapperFactory ().getKnowledgeBase (), 
					subjUri, this.getTargetPropertyUri (), objUri.toASCIIString () );
			else
				OwlApiUtils.assertAnnotationLink ( this.getMapperFactory ().getKnowledgeBase (), 
					subjUri, this.getTargetPropertyUri (), objUri.toASCIIString () );
			
			return true;
		} 
		catch ( URISyntaxException ex )
		{
			log.error ( "Ignoring bad URI while doing RDF mapping <{}[{}] '{}' [{}]", new String[] {
				source.getClass ().getSimpleName (), 
				StringUtils.abbreviate ( source.toString (), 50 ), 
				this.getTargetPropertyUri (),
				StringUtils.abbreviate ( propValue.toString (), 50 ) 
			});
			return false;
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
	 * If it's true, maps {@link #getTargetPropertyUri()} as an OWL object property, else it assumes it is an 
	 * annotation property (such as rdfs:seeAlso).
	 */
	public boolean isObjectProperty ()
	{
		return isObjectProperty;
	}

	public void setObjectProperty ( boolean isObjectProperty )
	{
		this.isObjectProperty = isObjectProperty;
	}
}
