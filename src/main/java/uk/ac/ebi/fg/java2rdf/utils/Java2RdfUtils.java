package uk.ac.ebi.fg.java2rdf.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;

/**
 * Some stuff useful for the RDF mapping job performed by the Java2RDF pacakge.
 *
 * <dl><dt>date</dt><dd>May 6, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class Java2RdfUtils
{
	private static Logger log = LoggerFactory.getLogger ( Java2RdfUtils.class );
	
	private Java2RdfUtils () {}

	/**
	 * Takes a string that is supposed to represent the identifier of a resource and turns it into an opaque compact and 
	 * URI-compatible representation. At the moment it hashes the parameter (via MD5) and converts the hash into lower-case
	 * hexadecimal. 
	 * 
	 */
	public static String hashUriSignature ( String sig ) 
	{
		if ( sig == null ) throw new IllegalArgumentException ( "Cannot hash a null URI" );
		
		MessageDigest messageDigest = null;		
		try {
			messageDigest = MessageDigest.getInstance ( "MD5" );
		} 
		catch ( NoSuchAlgorithmException ex ) {
			throw new RdfMappingException ( "Internal error, cannot get the MD5 digester from the JVM", ex );
		}
	
		String hash = DatatypeConverter.printHexBinary ( messageDigest.digest ( sig.getBytes () ) );
		hash = hash.toLowerCase ();
		
		log.trace ( "Returning hash '{}' from input '{}'", hash, sig );
		
		return hash;
	}
	
	/** 
	 * Invokes {@link URLEncoder#encode(String, String)} with UTF-8 encoding and wraps the generated exception with 
	 * an {@link IllegalArgumentException}.
	 * 
	 * @return null if the parameter is null, or the URL-encoded string.
	 */
	public static String urlEncode ( String queryStringUrl )
	{
		try {
			if ( queryStringUrl == null ) return null;
			return URLEncoder.encode ( queryStringUrl, "UTF-8" );
		}
		catch ( UnsupportedEncodingException ex ) {
			throw new IllegalArgumentException ( "That's strange, UTF-8 encoding seems wrong for encoding '" + queryStringUrl + "'" );
		}
	}
}
