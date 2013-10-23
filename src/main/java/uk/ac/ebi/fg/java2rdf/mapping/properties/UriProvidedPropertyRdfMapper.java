package uk.ac.ebi.fg.java2rdf.mapping.properties;


/**
 * While {@link PropertyRdfMapper} is generic and can map onto an arbitrary number of statements and using multiple
 * OWL/RDF property URIs, this subclass is intended for the use case where a pair of Java objects is always mapped 
 * by means of some RDF/OWL property, for which a property URI is available.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 */
public abstract class UriProvidedPropertyRdfMapper<T, PT> extends PropertyRdfMapper<T, PT>
{
	private String targetPropertyUri;
	
	public UriProvidedPropertyRdfMapper ()
	{
		this ( null );
	}

	public UriProvidedPropertyRdfMapper ( String targetPropertyUri )
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
