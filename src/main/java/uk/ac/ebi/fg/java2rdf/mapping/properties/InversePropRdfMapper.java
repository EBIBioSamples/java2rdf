package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;

/**
 * This is like {@link PropertyRdfMapper}, except the {@link #map(Object, Object, Map)} considers the relation 
 * from propValue to source (i.e., the inverse relation with respect to what {@link PropertyRdfMapper} normally does).
 *
 * <dl><dt>date</dt><dd>8 Aug 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class InversePropRdfMapper<T, PT> extends PropertyRdfMapper<T, PT>
{
	private PropertyRdfMapper<PT, T> inversePropMapper;
	
	public InversePropRdfMapper () {
		this ( null );
	}
	
	public InversePropRdfMapper ( PropertyRdfMapper<PT, T> inversePropMapper )
	{
		super ();
		this.setInversePropMapper ( inversePropMapper );
	}
	
	/**
	 * As explained above, it sends the pair (propValue, source) to {@link #getInversePropMapper()}, contrary to the order
	 * normally used by {@link PropertyRdfMapper}.
	 * 
	 * For instance, if you have an {@link InversePropRdfMapper InversePropRdfMapper&lt;CCard, Customer&gt;} with
	 * {@link #getInversePropMapper()} = OwlObjPropRdfMapper&lt;Customer, CCard&gt; ( "customer", "ex:has-cc" ), 
	 * this method will create statements of type (customer1, has-cc, cc1), by invoking the java getter CCard.getCustomer().
	 *   
	 */
	public boolean map ( T source, PT propValue, Map<String, Object> params ) 
	{
		if ( this.inversePropMapper == null ) return false;
		if ( propValue == null ) return false;
		
		return inversePropMapper.map ( propValue, source, params );
	}

	/**
	 * This is the base (direct) property that this mapper works with by considering its inverse. 
	 */
	public PropertyRdfMapper<PT, T> getInversePropMapper ()
	{
		return inversePropMapper;
	}

	public void setInversePropMapper ( PropertyRdfMapper<PT, T> inversePropMapper )
	{
		this.inversePropMapper = inversePropMapper;
	}

	/**
	 * This sets the factory of {@link #getInversePropMapper()} too.
	 */
	@Override
	public void setMapperFactory ( RdfMapperFactory mapperFactory )
	{
		super.setMapperFactory ( mapperFactory );
		if ( this.inversePropMapper != null ) inversePropMapper.setMapperFactory ( mapperFactory );
	}
}
