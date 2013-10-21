package uk.ac.ebi.fg.java2rdf.mapping;

import java.util.Map;

/**
 * 
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public abstract class ObjRdfMapper<T> extends RdfMapper<T>
{
	/**
	 * Does the mapping. Takes the source parameter and produces statements into {@link #getKnowledgeBase()}. Does this by
	 * generating an RDF value for the source, through {@link #getRdfUriGenerator()}. The mapper might need other
	 * mappers, for objects attached to the source, which it gets via {@link #getMapperFactory()}.
	 * 
	 * Avoid to call this method directly, use {@link RdfMapperFactory#map(Object)} instead. This will trace the
	 * objects that are already mapped.
	 * 
	 * TODO: AOP to hijacks this call to a factory call. 
	 * 
	 * @return true if the entity was actually mapped, or false if not, either because it was ignored for some reason (e.g., 
	 * null URI or decision to exclude certain objects from export), or because it is a duplicate. 
	 */
	public abstract boolean map ( T source, Map<String, Object> params );
	
	public final boolean map ( T source )
	{
		return map ( source, null );
	}
}
