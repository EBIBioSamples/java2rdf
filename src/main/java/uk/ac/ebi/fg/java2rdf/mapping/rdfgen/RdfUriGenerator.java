package uk.ac.ebi.fg.java2rdf.mapping.rdfgen;

import java.util.Map;


/**
 * This maps a JavaBean object into a URI identifier. For example you may want to map an instance of b of Book having 
 * b.isbn = '123' to 'http://rdf.example.com/123'. This should be done by {@link #getUri(Object)}.
 *
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public abstract class RdfUriGenerator<T> extends RdfValueGenerator<T, String>
{
	/** It invokes {@link #getUri(Object)}, redefine that. */
	@Override
	public final String getValue ( T source, Map<String, Object> params )
	{
		return getUri ( source, params );
	}

	/**
	 * Here it is where the real job happens and then {@link #getValue(Object)} is implemented as a synonym of this.
	 */
	public abstract String getUri ( T source, Map<String, Object> params );

	public final String getUri ( T source  ) {
		return getUri ( source, null );
	}

}
