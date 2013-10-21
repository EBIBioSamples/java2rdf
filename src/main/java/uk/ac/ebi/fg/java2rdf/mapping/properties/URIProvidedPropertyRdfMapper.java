package uk.ac.ebi.fg.java2rdf.mapping.properties;


/**
 * While {@link PropertyRdfMapper} is generic and can map onto an arbitrary number of statements and using multiple
 * OWL/RDF property URIs, this subclass is intended for the use case where a pair of Java objects is always mapped 
 * by means of some property URI.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 */
public abstract class URIProvidedPropertyRdfMapper<T, PT> extends PropertyRdfMapper<T, PT>
{
	private String targetPropertyUri;
	
	public URIProvidedPropertyRdfMapper ()
	{
		this ( null );
	}

	public URIProvidedPropertyRdfMapper ( String targetPropertyUri )
	{
		super ();
		this.setTargetPropertyUri ( targetPropertyUri );
	}
	

	/**
	 * The property URI that this mapper use for its mapping work.
	 * @return
	 */
	public String getTargetPropertyUri () {
		return targetPropertyUri;
	}

	public void setTargetPropertyUri ( String targetPropertyUri ) {
		this.targetPropertyUri = targetPropertyUri;
	}
}
