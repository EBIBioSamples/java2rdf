package uk.ac.ebi.fg.java2rdf.mapping;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.properties.BeanPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.PropertyRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfUriGenerator;
import uk.ac.ebi.fg.java2rdf.utils.OwlApiUtils;

/**
 * A bean mapper has the purpose of mapping a Java Bean into RDF. 
 *
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
@SuppressWarnings ( { "unchecked" } )
public class BeanRdfMapper<T> extends CompositeObjRdfMapper<T>
{
	private String targetRdfClassUri;
	private RdfUriGenerator<T> rdfUriGenerator;

	protected Logger log = LoggerFactory.getLogger ( this.getClass () );
	
	public BeanRdfMapper () {
		this ( null );
	}

	public BeanRdfMapper ( String rdfClassUri ) {
		this ( rdfClassUri, null, null );
	}
	

	public BeanRdfMapper ( String rdfClassUri, RdfUriGenerator<T> rdfUriGenerator ) {
		this ( rdfClassUri, rdfUriGenerator, null );
	}
	

	public BeanRdfMapper ( String rdfClassUri, RdfUriGenerator<T> rdfUriGenerator, ObjRdfMapper<T> ... propertyMappers )
	{
		super ( propertyMappers );
		this.setRdfClassUri ( rdfClassUri );
		this.setRdfUriGenerator ( rdfUriGenerator );
	}
	

	/**
	 * 
	 * Creates a set of subject-centric statements, using {@link #getPropertyMappers()}. Uses {@link #getRdfUriGenerator()}
	 * to get the URI of the 'source' parameter, i.e., the bean to be mapped. Uses {@link #getTargetRdfClassUri()} to 
	 * make a rdf:type statement about 'source'.
	 */
	@Override
	public boolean map ( T source, Map<String, Object> params )
	{
		try
		{
			if ( !super.map ( source, params )) return false;
			String uri = getRdfUriGenerator ().getUri ( source, params );
			if ( uri == null ) return false;
			
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			
			// Generates and rdf:type statement
			String targetRdfClassUri = getTargetRdfClassUri ();
			if ( targetRdfClassUri != null ) OwlApiUtils.assertIndividual ( mapFactory.getKnowledgeBase (), 
					getRdfUriGenerator ().getUri ( source, params ), targetRdfClassUri );
			// TODO: else WARN
			return true;
		} 
		catch ( Exception ex )
		{
			throw new RdfMappingException ( String.format ( 
				"Error while mapping %s[%s]='%s' to RDF: %s", 
				source.getClass ().getSimpleName (), 
				StringUtils.abbreviate ( source.toString (), 50 ), StringUtils.abbreviate ( source.toString (), 50 ),
				ex.getMessage ()
			), ex );
		}
	}

	
	/**
	 * Maps rdf:type for the class managed by this mapper ( usually it is something that corresponds to T )
	 */
	public String getTargetRdfClassUri () {
		return targetRdfClassUri;
	}

	public void setRdfClassUri ( String targetRdfClassUri ) {
		this.targetRdfClassUri = targetRdfClassUri;
	}
	
	/** The generator used in {@link #map(Object)} to make the URI of the source bean that is mapped to RDF. */
	public RdfUriGenerator<T> getRdfUriGenerator () {
		return rdfUriGenerator;
	}

	public void setRdfUriGenerator ( RdfUriGenerator<T> rdfUriGenerator ) {
		this.rdfUriGenerator = rdfUriGenerator;
	}
	
	public <PT> void addPropertyMapper ( String sourcePropertyName, PropertyRdfMapper<T, PT> propRdfMapper )
	{
		List<ObjRdfMapper<T>> mappers = this.getMappers ();
		if ( mappers == null ) this.setMappers ( mappers = new LinkedList<> () );			
		mappers.add ( new BeanPropRdfMapper<T, PT> ( sourcePropertyName, propRdfMapper ) );
	}
}
