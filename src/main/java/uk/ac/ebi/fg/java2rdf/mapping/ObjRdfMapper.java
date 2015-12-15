package uk.ac.ebi.fg.java2rdf.mapping;

import java.util.Map;

/**
 * An object mapper has the method {@link #map(Object)} and it's able to map a Java object to a set of RDF statements. 
 * How this is done is left to concrete subclasses (have a look at them).
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public abstract class ObjRdfMapper<T> extends RdfMapper<T>
{
	/**
	 * Does the mapping. This is expected to generate RDF triples into {@link #getKnowledgeBase()}. 
	 * 
	 * Avoid to call this method directly, use {@link RdfMapperFactory#map(Object, Map)} instead. This will trace the
	 * objects that are already mapped.
	 * 
	 * The params can contain additional parameters needed to do the mappings.
	 * 
	 * TODO: AOP to hijacks this call to a factory call. 
	 * 
	 * @return true if the entity was actually mapped, or false if not, either because it was ignored for some reason (e.g., 
	 * null URI or decision to exclude certain objects from export), or because it is a duplicate. <b>PLEASE NOTE</b>:
	 * you should always return from this method according to this contract. 
	 */
	public abstract boolean map ( T source, Map<String, Object> params );
	
	/**
	 * Wraps {@link #map(Object, Map)} with params = null 
	 */
	public final boolean map ( T source )
	{
		return map ( source, null );
	}
}
