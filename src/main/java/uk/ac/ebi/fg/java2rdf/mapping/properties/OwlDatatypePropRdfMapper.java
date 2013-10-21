package uk.ac.ebi.fg.java2rdf.mapping.properties;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfLiteralGenerator;
import uk.ac.ebi.fg.java2rdf.utils.OwlApiUtils;

/**
 * This maps a pair of Java objects by means of some OWL data type property. For instance, you may use this mapper
 * to map Book.getTitle() via dc:title. The RDF literal value for such a mapping (i.e., the book title) is created
 * by means of {@link #getRdfLiteralGenerator()}. 
 * 
 * @See {@link RdfLiteralGenerator}.
 * 
 * <dl><dt>date</dt><dd>Mar 24, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class OwlDatatypePropRdfMapper<T, PT> extends URIProvidedPropertyRdfMapper<T, PT>
{
	private RdfLiteralGenerator<PT> rdfLiteralGenerator;
	
	public OwlDatatypePropRdfMapper ()  {
		this ( null, new RdfLiteralGenerator<PT> () );
	}

	public OwlDatatypePropRdfMapper ( String targetPropertyUri )
	{
		this ( targetPropertyUri, new RdfLiteralGenerator<PT> () );
	}

	public OwlDatatypePropRdfMapper ( String targetPropertyUri, RdfLiteralGenerator<PT> rdfLiteralGenerator ) 
	{
		super ( targetPropertyUri );
		this.setRdfLiteralGenerator ( rdfLiteralGenerator );
	}
	
	/**
	 * This maps (source, propValue) via {@link #getTargetPropertyUri()}. Uses {@link RdfMapperFactory#getUri(Object, Map)}
	 * to get the URI of source, and {@link #getRdfLiteralGenerator()} to get the literal value for propValue.
	 */
	@Override
	public boolean map ( T source, PT propValue, Map<String, Object> params )
	{
		try
		{
			if ( propValue == null ) return false;
			
			RdfMapperFactory mapFactory = this.getMapperFactory ();
			RdfLiteralGenerator<PT> targetValGen = this.getRdfLiteralGenerator ();
			String targetRdfVal = targetValGen.getValue ( propValue, params );
			if ( targetRdfVal == null ) return false;
			
			OwlApiUtils.assertData ( this.getMapperFactory ().getKnowledgeBase (), 
				mapFactory.getUri ( source, params ), this.getTargetPropertyUri (), targetRdfVal );
			return true;
		} 
		catch ( Exception ex )
		{
			throw new RdfMappingException ( String.format ( 
				"Error while doing the RDF mapping <%s[%s] '%s' [%s]>: %s", 
					source.getClass ().getSimpleName (), 
					StringUtils.abbreviate ( source.toString (), 50 ), 
					this.getTargetPropertyUri (),
					StringUtils.abbreviate ( propValue.toString (), 50 ), 
					ex.getMessage ()
			), ex );
		}
	}

	/**
	 * This generates the literal value (a plain string at the moment) to be used to map an object which is the value
	 * of a JavaBean property into a string representation of such value. 
	 */
	public RdfLiteralGenerator<PT> getRdfLiteralGenerator () {
		return rdfLiteralGenerator;
	}

	public void setRdfLiteralGenerator ( RdfLiteralGenerator<PT> rdfLiteralGenerator ) {
		this.rdfLiteralGenerator = rdfLiteralGenerator;
	}
}
