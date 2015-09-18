package uk.ac.ebi.fg.java2rdf.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

/**
 * <p>A utility class that basically has the purpose of keeping a map from prefixes to namespaces and return the namespace
 * corresponding to a prefix. You can expand the set of predefined namespaces by using {@link #registerNs(String, String)}.</p>
 * 
 * <p>TODO: Indeed, we can do much better by loading namespace defs from an RDF file.</p> 
 *
 * <dl><dt>date</dt><dd>Apr 23, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class NamespaceUtils
{
	private NamespaceUtils () {}
	
	static 
	{
		NAMESPACES = new HashMap<String, String> ();
		
		registerNs ( "rdf",				Namespaces.RDF.toString () );
		registerNs ( "rdfs",			Namespaces.RDFS.toString () );
		registerNs ( "owl",				Namespaces.OWL.toString () );
		registerNs ( "xsd", 			Namespaces.XSD.toString () );
		registerNs ( "dc", 				Namespaces.DC.toString () );
		registerNs ( "dc-terms", 	Namespaces.DCTERMS.toString () );
	}
	
	/**
	 * See the source to see which defaults are available.
	 */
	private static final Map<String, String> NAMESPACES; 
	
	public static String ns ( String prefix ) {
		return NAMESPACES.get ( prefix );
	}
	
	public static String uri ( String prefix, String relativePath ) 
	{
		prefix = StringUtils.trimToNull ( prefix );
		Validate.notNull ( prefix, "Cannot resolve empty namespace prefix" );
		String namespace = NAMESPACES.get ( prefix );
		Validate.notNull ( namespace, "Namespace prefix '" + prefix + "' not found" );
		return namespace + relativePath;
	}
	
	public static String uri ( String prefixedUri )
	{
		String[] chunks = StringUtils.split ( prefixedUri, ':' );
		if ( chunks == null || chunks.length < 2 ) return prefixedUri;
		return uri ( chunks [ 0 ], chunks [ 1 ] );
	}
	
	/**
	 * Returns a unmodifiable view of the namespaces managed by this utility class, use {@link #registerNs(String, String)}
	 * to make changes.
	 * 
	 */
	public static Map<String, String> getNamespaces () {
		return Collections.unmodifiableMap ( NAMESPACES ); 
	}

	public static void registerNs ( String prefix, String uri ) {
		NAMESPACES.put ( prefix, uri );
	}

	/**
	 * Copies all the namespaces into the structure that OWLAPI uses to build its output 
	 */
	public static void copy2OwlApi ( PrefixOWLOntologyFormat owlApiPrefixes )
	{
		for ( Entry<String, String> nse: getNamespaces ().entrySet () )
			owlApiPrefixes.setPrefix ( nse.getKey (), nse.getValue () );
	}
	
	/**
	 * Builds a list of {@code PREFIX x <y>\n} from the current list of managed prefixes, 
	 * list that can be used as prolog for SPARQL queries. 
	 */
	public static String asSPARQLProlog ()
	{
		StringBuilder result = new StringBuilder ();
		for ( Entry<String, String> nse: getNamespaces ().entrySet () )
			result.append ( String.format ( "PREFIX %s: <%s>\n", nse.getKey (), nse.getValue () ) );
		return result.toString ();
	}
}
