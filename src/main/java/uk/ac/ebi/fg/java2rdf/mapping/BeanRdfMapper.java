package uk.ac.ebi.fg.java2rdf.mapping;


import static info.marcobrandizi.rdfutils.namespaces.NamespaceUtils.iri;
import static uk.ac.ebi.fg.java2rdf.utils.Java2RdfUtils.RDF_GRAPH_UTILS;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.properties.BeanPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.PropertyRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfUriGenerator;

/**
 * Maps a JavaBean object and its getter-reachable properties onto a set of RDF statements.  
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
		this ( rdfClassUri, null, (ObjRdfMapper<T>[]) null );
	}
	

	public BeanRdfMapper ( String rdfClassUri, RdfUriGenerator<T> rdfUriGenerator ) {
		this ( rdfClassUri, rdfUriGenerator, (ObjRdfMapper<T>[]) null );
	}
	

	public BeanRdfMapper ( String rdfClassUri, RdfUriGenerator<T> rdfUriGenerator, ObjRdfMapper<T> ... propertyMappers )
	{
		super ( propertyMappers );
		this.setRdfClassUri ( rdfClassUri );
		this.setRdfUriGenerator ( rdfUriGenerator );
	}
	

	/**
	 * Creates a set of subject-centric statements, using {@link #getMappers()}, which usually contains mappers of 
	 * getXXX()-reachable properties. Uses {@link #getRdfUriGenerator()} to get the URI of the 'source' parameter, 
	 * i.e., the bean to be mapped. Uses {@link #getTargetRdfClassUri()} to make a rdf:type statement about 'source'.
	 * 
	 * If either the super implementation, the URI generator, or its getUri() method returns false, doesn't generate any
	 * mapping and returns false too. 
	 */
	@Override
	public boolean map ( T source, Map<String, Object> params )
	{
		try
		{
			if ( !super.map ( source, params ) ) return false;
			
			RdfUriGenerator<T> uriGen = getRdfUriGenerator ();
			Validate.notNull ( uriGen, "Internal error: cannot map [%s] to RDF without an URI generator", source.toString () );
			
			String uri = uriGen.getUri ( source, params );
			if ( uri == null ) return false;
			
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			
			// Generates and rdf:type statement
			String targetRdfClassUri = getTargetRdfClassUri ();
			if ( targetRdfClassUri != null ) RDF_GRAPH_UTILS.assertResource ( 
				mapFactory.getGraphModel (), uri, iri ( "rdf:type" ), targetRdfClassUri 
			);
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
	
	/** The generator used in {@link #map(Object, Map)} to make the URI of the source bean that is mapped to RDF. */
	public RdfUriGenerator<T> getRdfUriGenerator () {
		return rdfUriGenerator;
	}

	public void setRdfUriGenerator ( RdfUriGenerator<T> rdfUriGenerator ) {
		this.rdfUriGenerator = rdfUriGenerator;
	}
	
	/**
	 * Usually you will want that {@link #getMappers()} contains {@link BeanPropRdfMapper} only. This method wraps
	 * the propRdfMapper into a new {@link BeanPropRdfMapper}, having sourcePropertyName as the JavaBean property it works
	 * with.
	 */
	public <PT, RV> void addPropertyMapper ( String sourcePropertyName, PropertyRdfMapper<T, PT, RV> propRdfMapper )
	{
		if ( this.getMappers () == null ) this.setMappers ( new LinkedList<> () );
		this.addMapper ( new BeanPropRdfMapper<T, PT, RV> ( sourcePropertyName, propRdfMapper ) );
	}

	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( this.rdfUriGenerator == null ) return;
		
		this.rdfUriGenerator.setMapperFactory ( mapperFactory );
	}
		
}
