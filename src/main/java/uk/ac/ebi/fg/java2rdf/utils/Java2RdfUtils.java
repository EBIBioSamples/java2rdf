package uk.ac.ebi.fg.java2rdf.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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

	@SuppressWarnings ( "unchecked" )
	public static <V> V getParam ( Map<String, Object> params, String key, V defaultValue )
	{
		if ( params == null ) return defaultValue;
		return (V) params.getOrDefault ( key, defaultValue );
	}
	
	public static <V> V getParam ( Map<String, Object> params, String key )
	{
		return getParam ( params, key, null );
	}

}
