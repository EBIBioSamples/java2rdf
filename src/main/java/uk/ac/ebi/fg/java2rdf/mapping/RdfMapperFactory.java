package uk.ac.ebi.fg.java2rdf.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ebi.fg.java2rdf.mapping.properties.URIProvidedPropertyRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfUriGenerator;

/**
 * TODO: COMMENT ME AGAIN!
 *
 * <p>This takes care of a collection of Bean-to-RDF mappers to be used together and it should be the entry point of 
 * a the job of converting an instance of an object model to RDF, i.e., you should define your own extension of 
 * this factory, with your specific mappers defined in it and then invoke {@link #map(Object)} for the 
 * root-level objects of your model. These in turn should call the {@link URIProvidedPropertyRdfMapper property mappers} stored
 * inside the {@link BeanRdfMapper bean mappers} and also the same {@link #map(Object)} method from the factory and
 * {@link #getRdfUriGenerator(Object)}, to obtain property/value statements about the JavaBean visited via such calls.</p>
 * 
 * <p>While you do this, the {@link #map(Object)} method in this factory takes care of already-visited objects and 
 * avoid multiple unneeded visits to the same objects, as well loops. Given that, you should always start a mapping 
 * from the {@link #map(Object) map() method} in this factory and avoid to invoke
 * {@link BeanRdfMapper#map(Object)}, since the factory only can be aware of already-visited beans.   
 *
 * <dl><dt>date</dt><dd>Mar 23, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class RdfMapperFactory
{
	private OWLOntology knowledgeBase;
	private Map<Class, ObjRdfMapper> mappers;
	private Set visitedObjects = Collections.synchronizedSet ( new HashSet<> () );
	
	public RdfMapperFactory () {
	}
	
	public RdfMapperFactory ( OWLOntology knowledgeBase ) {
		this.knowledgeBase = knowledgeBase;
	}
	

	/**
	 * The default implementation provides a mapper by looking at the class of the source object, via {@link #getMappers()}. 
	 */
	public <T> ObjRdfMapper<T> getMapper ( T source ) {
		return source == null ? null : (ObjRdfMapper<T>) this.getMapper ( source.getClass () );
	}

	public <T> ObjRdfMapper<T> getMapper ( Class<T> clazz ) 
	{
		if ( clazz == null ) return null;
		return mappers == null ? null : mappers.get ( clazz );
	}

	/**
	 * Maps a Java class to a {@link BeanRdfMapper}. This will be used by {@link #getMapper(Object)} and, in turn, 
	 * by {@link #map(Object)}. This method does 
	 * {@link BeanRdfMapper#setMapperFactory(RdfMapperFactory) mapper.setMapperFactory ( this )} automatically.
	 * 
	 */
	public <T> ObjRdfMapper setMapper ( Class<T> clazz,  ObjRdfMapper<T> mapper ) 
	{
		Validate.notNull ( clazz, "Internal error: I cannot map a null class to RDF" );
		Validate.notNull ( mapper, "Internal error: I cannot map '" + clazz.getSimpleName () + "' to RDF using a null mapper" );
		
		if ( mappers == null ) mappers = new HashMap<> ();
		mapper.setMapperFactory ( this );
		return mappers.put ( clazz, mapper );
	}
	
	public Map<Class, ObjRdfMapper> getMappers () {
		return mappers;
	}

	public void setMappers ( Map<Class, ObjRdfMapper> mappers ) {
		this.mappers = mappers;
	}
	
	
	public OWLOntology getKnowledgeBase () {
		return knowledgeBase;
	}

	/**
	 * In case it is really new, {@link #reset()} is invoked. 
	 */
	public void setKnowledgeBase ( OWLOntology knowledgeBase ) 
	{
		if ( this.knowledgeBase == knowledgeBase ) return;
		this.knowledgeBase = knowledgeBase;
		this.reset ();
	}

	/** 
	 * <p>This invokes {@link #getMapper(Object)} and then its {@link BeanRdfMapper#map(Object) map method}. It does this
	 * only if the object has not been already mapped. Always call this, which trace already-mapped beans. 
	 * Never call {@link BeanRdfMapper#map(Object)} directly.</p>
	 *  
	 * <p>TODO: AOP around the source mapper.</p>
	 */
	public <T> boolean map ( T source, Map<String, Object> params )
	{
		if ( mappers == null ) return false;
		if ( source == null ) return false;
		if ( this.visitedObjects.contains ( source ) ) return false;
			
		ObjRdfMapper<T> mapper = getMapper ( source );
		if ( mapper == null ) throw new RuntimeException ( 
			"Cannot find a mapper for " + source.getClass ().getSimpleName () );
		
		this.visitedObjects.add ( source );
		return mapper.map ( source, params ); 
	}
	
	public final <T> boolean map ( T source ) {
		return this.map ( source, null );
	}

	
	public <T> RdfUriGenerator<T> getRdfUriGenerator ( Class<T> clazz ) 
	{
		if ( clazz == null ) return null;
		ObjRdfMapper<T> mapper = getMapper ( clazz );
		if ( ! ( mapper instanceof BeanRdfMapper) ) return null;
		return ((BeanRdfMapper) mapper).getRdfUriGenerator ();
	} 
	
	/**
	 * A convenience wrapper of {@link #getMapper(Object)}.{@link #getRdfUriGenerator(Object)}.
	 */
	public <T> RdfUriGenerator<T> getRdfUriGenerator ( T source ) 
	{
		if ( source == null ) return null; 
		return (RdfUriGenerator<T>) this.getRdfUriGenerator ( source.getClass() );
	}
	


	/**
	 * A convenience wrapper of {@link #getRdfUriGenerator(Object)}.{@link RdfUriGenerator#getUri(Object)}.
	 * @param source
	 * @return
	 */
	public <T> String getUri ( T source, Map<String, Object> params ) 
	{
		if ( source == null ) return null; 
		
		RdfUriGenerator<T> uriGen = this.getRdfUriGenerator ( source );
		if ( uriGen == null ) return null;
		
		return uriGen.getUri ( source, params );
	}
	
	public final <T> String getUri ( T source ) {
		return this.getUri ( source, null );
	} 


	/**
	 * Marks all the beans as new, so they'll be re-visited by {@link #map(Object)}, like the first time.
	 */
	public void reset () {
		this.visitedObjects.clear ();
	}

}

