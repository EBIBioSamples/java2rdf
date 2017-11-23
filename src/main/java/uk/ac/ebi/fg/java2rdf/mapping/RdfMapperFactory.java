package uk.ac.ebi.fg.java2rdf.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.rdf.api.Graph;

import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfUriGenerator;

/**
 * <p>This is to be used to configure the mappers needed for mapping a specific Java object model to RDF. You're expected to
 * define your own extension of this class, where you use {@link #setMapper(Class, ObjRdfMapper)} or {@link #setMappers(Map)}
 * to associate JavaBean classes to RDF mappers, which translate every JavaBean instance of classes in a set of RDF statements.</p>
 * 
 * <p>After that, you should start the mapping job by invoking {@link #map(Object, Map)} or {@link #map(Object)} with some 
 * root classes in the object model, the mappers will propagate the mapping to other reachable objects automatically.</p>
 * 
 * <p>The map() methods in the factory also takes care of already-visited objects and 
 * avoid multiple unneeded visits to the same objects, as well loops. Given that, you should always start a mapping 
 * from the {@link #map(Object) map() method} in this factory and avoid to invoke
 * {@link BeanRdfMapper#map(Object)}, since the factory only can be aware of already-visited beans.</p> 
 *
 * <dl><dt>date</dt><dd>Mar 23, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class RdfMapperFactory
{
	private Graph graphModel;
	private Map<Class, ObjRdfMapper> mappers;
	private Set visitedObjects = Collections.synchronizedSet ( new HashSet<> () );
	
	public RdfMapperFactory () {
	}
	
	public RdfMapperFactory ( Graph graphModel ) {
		this.graphModel = graphModel;
	}
	

	/**
	 * The default implementation provides a mapper by looking at the class of the source object, via {@link #getMappers()}. 
	 */
	public <T> ObjRdfMapper<T> getMapper ( T source ) 
	{
		Validate.notNull ( source, "Cannot map a null source object" );
		return (ObjRdfMapper<T>) this.getMapper ( source.getClass () );
	}

	/** 
	 * Tells which mapper is used for instances of this class. This has to be defined in advance, via 
	 * {@link #setMapper(Class, ObjRdfMapper)} or {@link #setMappers(Map)}. 
	 * 
	 */
	public <T> ObjRdfMapper<T> getMapper ( Class<T> clazz ) 
	{
		Validate.notNull ( clazz, "Internal error: cannot map a null class" );
		Validate.notNull ( mappers, "Internal error: Please initialise the Java2RDF framework with a set of mappers, "
			+ "before requesting a mapper for '%s'", clazz.getSimpleName () );
		return mappers.get ( clazz );
	}

	/**
	 * This is used by {@link #map(Object, Map)} to map a JavaBean to RDF statements. This method invokes 
	 * {@link ObjRdfMapper#setMapperFactory(RdfMapperFactory)} automatically.
	 * 
	 */
	public <T> ObjRdfMapper setMapper ( Class<T> clazz,  ObjRdfMapper<T> mapper ) 
	{
		Validate.notNull ( clazz, "Internal error: I cannot map a null class to RDF" );
		Validate.notNull ( mapper, "Internal error: I cannot map '%s' to RDF using a null mapper", clazz.getSimpleName () );
		
		if ( mappers == null ) mappers = new HashMap<> ();
		mapper.setMapperFactory ( this );
		return mappers.put ( clazz, mapper );
	}
	
	/**
	 * See {@link #setMapper(Class, ObjRdfMapper)}.
	 */
	public Map<Class, ObjRdfMapper> getMappers () {
		return mappers;
	}

	/**
	 * See {@link #setMapper(Class, ObjRdfMapper)}.
	 */
	public void setMappers ( Map<Class, ObjRdfMapper> mappers ) {
		this.mappers = mappers;
	}
	
	/** This is where the mapping output goes */
	public Graph getGraphModel () {
		return graphModel;
	}

	/**
	 * In case it is really new, {@link #reset()} is invoked. 
	 */
	public void setGraphModel ( Graph graphModel ) 
	{
		if ( this.graphModel == graphModel ) return;
		this.graphModel = graphModel;
		this.reset ();
	}

	/** 
	 * <p>This invokes {@link #getMapper(Object)} and then its {@link ObjRdfMapper#map(Object, Map) map method}. It does this
	 * only if the object has not been already mapped. Always call this, which trace already-mapped beans. 
	 * Never call {@link BeanRdfMapper#map(Object, Map)} directly.</p>
	 *  
	 * <p>TODO: AOP around the source mapper.</p>
	 */
	public <T> boolean map ( T source, Map<String, Object> params )
	{
		if ( source == null ) return false;
		if ( this.visitedObjects.contains ( source ) ) return false;
			
		ObjRdfMapper<T> mapper = getMapper ( source );
		Validate.notNull ( mapper, "Cannot find a mapper for '%s'", source.getClass ().getSimpleName () );
		
		this.visitedObjects.add ( source );
		return mapper.map ( source, params ); 
	}
	
	public final <T> boolean map ( T source ) {
		return this.map ( source, null );
	}

	/**
	 * See {@link #getRdfUriGenerator(Object)} and {@link #getMapper(Class)}.
	 */
	public <T> RdfUriGenerator<T> getRdfUriGenerator ( Class<T> clazz ) 
	{
		Validate.notNull ( clazz, "Internal error: I cannot have a URI generator for a null class" );
		ObjRdfMapper<T> mapper = getMapper ( clazz );
		if ( ! ( mapper instanceof BeanRdfMapper) ) throw new RdfMappingException ( 
			"Internal error: the mapper '" + mapper.getClass ().getSimpleName () + "' is not a BeanRdfMapper and I cannot get "
			+ "a URI generator from it" 
		);
		return ((BeanRdfMapper) mapper).getRdfUriGenerator ();
	} 
	
	/**
	 * A convenience wrapper of {@link #getMapper(Object)}.{@link #getRdfUriGenerator(Object)}.
	 */
	public <T> RdfUriGenerator<T> getRdfUriGenerator ( T source ) 
	{
		Validate.notNull ( source, "Internal error: I cannot have any URI generator for a null source object" );
		return (RdfUriGenerator<T>) this.getRdfUriGenerator ( source.getClass() );
	}
	


	/**
	 * A convenience wrapper of {@link #getRdfUriGenerator(Object)}.{@link RdfUriGenerator#getUri(Object)}.
	 */
	public <T> String getUri ( T source, Map<String, Object> params ) 
	{
		Validate.notNull ( source, "Internal error: cannot map a null source object to RDF" );
		
		RdfUriGenerator<T> uriGen = this.getRdfUriGenerator ( source );
		Validate.notNull ( uriGen,
			"Internal error: cannot map [%s] with a null URI generator", StringUtils.abbreviate ( source.toString (), 30 ) );
		
		return uriGen.getUri ( source, params );
	}
	
	/**
	 * Wrapper with params = null.
	 */
	public final <T> String getUri ( T source ) {
		return this.getUri ( source, null );
	} 


	/**
	 * Marks all the beans as new, so they'll be re-visited by {@link #map(Object, Map)} once it has been done once.
	 */
	public void reset () {
		this.visitedObjects.clear ();
	}

}

