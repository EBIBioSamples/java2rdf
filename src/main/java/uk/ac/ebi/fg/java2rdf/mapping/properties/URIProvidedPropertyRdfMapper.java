package uk.ac.ebi.fg.java2rdf.mapping.properties;


import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;


/**
 * TODO: COMMENT ME AGAIN!!!
 * 
 * Property mappers are used to associate a JavaBean property to an RDFS/OWL property. Property mappers are usually
 * invoked by {@link BeanRdfMapper#map(Object)}.
 * 
 * @param <T> the type of source mapped bean which of property is mapped to RDF.
 * @param <PT> the type of source property that is mapped to RDF.
 *
 * TODO: do we need a field like this.specificRDFValueMapper? 
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
	

	
	public String getTargetPropertyUri () {
		return targetPropertyUri;
	}

	public void setTargetPropertyUri ( String targetPropertyUri ) {
		this.targetPropertyUri = targetPropertyUri;
	}
}
