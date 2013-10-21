package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.utils.OwlApiUtils;

/**
 * This maps the value of a JavaBean property ot an OWL object-proeperty. It uses {@link #getMapperFactory()} and 
 * its method {@link RdfMapperFactory#getRdfUriGenerator(Object)} to get URIs for the source and target beans
 * to be mapped. For example, to map an instance of b of a Java class Book, having b.author = a, with a as an instance of 
 * the Java class Author, to a statement like: http://rdf.example.com/isbn/123 ex:has-author http://example.com/author/asimov, 
 * you'll define an oobject property mapper having {@link #getSourcePropertyName()} = ex:has-author, while the 
 * subject's URI will be provided by the {@link BeanRdfMapper} for Book 
 * (via the method {@link BeanRdfMapper#getRdfUriGenerator()}) and the object's URI will be given by the {@link BeanRdfMapper} 
 * for Author (again, via {@link BeanRdfMapper#getRdfUriGenerator()}. The bean mappers and their URI generators will be 
 * invoked by the {@link #getMapperFactory() mapper factory associated to this property mapper}.  
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class OwlObjPropRdfMapper<T, PT> extends URIProvidedPropertyRdfMapper<T, PT>
{
	public OwlObjPropRdfMapper ()  {
		super ();
	}

	public OwlObjPropRdfMapper ( String targetPropertyUri ) {
		super ( targetPropertyUri );
	}
	
	
	/**
	 * Generates a triple where the property {@link #getSourcePropertyName()} is asserted for the source, using
	 * {@link #getTargetPropertyUri()}. Uses {@link RdfMapperFactory#getUri(Object)} for both the source and the target URI. 
	 */
	@Override
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		if ( propValue == null ) return false;
		try
		{
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			
			// TODO: can we avoid to keep recomputing these
			//
			
			// This is necessary, cause source/pval may be swapped by InversePropRdfMapper
			String subjUri = mapFactory.getUri ( source, params );
			if ( subjUri == null ) return false;

			String objUri = mapFactory.getUri ( propValue, params );
			if ( objUri == null ) return false;
			
			OwlApiUtils.assertLink ( this.getMapperFactory ().getKnowledgeBase (), 
				subjUri, this.getTargetPropertyUri (), objUri );

			// Don't use targetMapper, we need to trace this visit.
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
