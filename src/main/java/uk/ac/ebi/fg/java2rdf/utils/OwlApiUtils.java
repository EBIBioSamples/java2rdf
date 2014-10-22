package uk.ac.ebi.fg.java2rdf.utils;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;

/**
 * Some utilities that avoids to use OWL API directly, thus making things simpler and less interdependent.
 * 
 * All methods are synchronised, since we expect that the Java2RDF library will be used in a multi-threaded fashion. 
 * 
 * TODO: a good idea for the future is to make a completely generic API and plug-ins to common frameworks, such as
 * Jena, Sesame, OWL-API etc.
 *
 * <dl><dt>date</dt><dd>6 Feb 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class OwlApiUtils
{
	private static Logger log = LoggerFactory.getLogger ( OwlApiUtils.class );
	
	private static final boolean DEBUG_FLAG = false;
	
	/**
	 * Wrapper with dataTypeUri = null.
	 */
	public static synchronized void assertData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue )
	{
		assertData ( model, subjectUri, propertyUri, propertyValue, null );
	}
	
	/**
	 * Asserts a statement by using {@link OWLDataPropertyAssertionAxiom}. If dataTypeUri = null, uses a default type
	 * literal. 
	 */
	public static synchronized void assertData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue, String dataTypeUri )
	{
		checkNonNullTriple ( "assertData", subjectUri, propertyUri, propertyValue, dataTypeUri );
		if ( DEBUG_FLAG ) return;
		
		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
		
    OWLLiteral literal;
    if ( dataTypeUri == null )
    	literal = owlFactory.getOWLLiteral ( propertyValue );
    else
    {
    	OWLDatatype dataType = owlFactory.getOWLDatatype ( IRI.create ( dataTypeUri ) );
    	literal = owlFactory.getOWLLiteral ( propertyValue, dataType );
    }

		owlMgr.addAxiom ( model, owlFactory.getOWLDataPropertyAssertionAxiom ( 
			owlFactory.getOWLDataProperty ( IRI.create( propertyUri )), 
			owlFactory.getOWLNamedIndividual ( IRI.create ( subjectUri )),
			literal
		));		
	}

	/**
	 * Assert a statement using {@link OWLObjectPropertyAssertionAxiom}. 
	 */
	public static synchronized void assertLink ( OWLOntology model, String subjectUri, String propertyUri, String objectUri )
	{
		checkNonNullTriple ( "assertLink", subjectUri, propertyUri, objectUri, null );
		if ( DEBUG_FLAG ) return;

		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();

		owlMgr.addAxiom ( model, owlFactory.getOWLObjectPropertyAssertionAxiom ( 
			owlFactory.getOWLObjectProperty ( IRI.create( propertyUri )), 
			owlFactory.getOWLNamedIndividual ( IRI.create ( subjectUri )),  
			owlFactory.getOWLNamedIndividual ( IRI.create ( objectUri ))  
		));
	}
	
	/**
	 * Assert that a subject URI is an OWL indiviudal, instance of a class URI. Uses {@link OWLClassAssertionAxiom}. 
	 */
	public static synchronized void assertIndividual ( OWLOntology model, String individualUri, String classUri ) 
	{
		if ( StringUtils.trimToNull ( individualUri ) == null || StringUtils.trimToNull ( classUri ) == null ) 
			throw new RdfMappingException ( String.format ( 
				"assertIndividual ( '%s', '%s' ): cannot accept null paramters",
				individualUri, classUri
		)); 

		log.trace ( "doing assertClass ( '{}', '{}' )", individualUri, classUri ); 
		if ( DEBUG_FLAG ) return;

		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
				
		owlMgr.addAxiom ( model, owlFactory.getOWLClassAssertionAxiom (
			owlFactory.getOWLClass ( IRI.create ( classUri )), 
			owlFactory.getOWLNamedIndividual( IRI.create ( individualUri ))
		));		
	}

	/**
	 * Asserts that a given classUri is subClass of another superClassUri. Uses {@link OWLSubClassOfAxiom}.
	 */
	public static synchronized void assertClass ( OWLOntology model, String classUri, String superClassUri ) 
	{
		if ( StringUtils.trimToNull ( classUri ) == null || StringUtils.trimToNull ( superClassUri ) == null ) 
			throw new RdfMappingException ( String.format ( 
				"assertClass ( '%s', '%s' ): cannot accept null paramters",
				classUri, superClassUri
		)); 
		
		log.trace ( "doing assertClass ( '{}', '{}' )", classUri, superClassUri );
		if ( DEBUG_FLAG ) return;
		
		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
		
		owlMgr.addAxiom ( model, owlFactory.getOWLSubClassOfAxiom (
			owlFactory.getOWLClass ( IRI.create ( classUri )), 
			owlFactory.getOWLClass( IRI.create ( superClassUri ))
		));		
	}
	
	/**
	 * Asserts a statement assuming propertyUri is an annotation property. Uses {@link OWLAnnotationAssertionAxiom}.
	 */
	public static synchronized void assertAnnotationLink ( OWLOntology model, String subjectUri, String propertyUri, String objectUri )
	{
		checkNonNullTriple ( "assertAnnotationLink", subjectUri, propertyUri, objectUri, null );
		if ( DEBUG_FLAG ) return;
		
		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
		
		owlMgr.addAxiom ( model, owlFactory.getOWLAnnotationAssertionAxiom ( 
			owlFactory.getOWLAnnotationProperty ( IRI.create ( propertyUri ) ),
			IRI.create ( subjectUri ),
			IRI.create ( objectUri )
		));
	}
	
	/**
	 * A wrapper with dataTypeUri = null.
	 */
	public static synchronized void assertAnnotationData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue ) 
	{
		assertAnnotationData ( model, subjectUri, propertyUri, propertyValue, null );
	}

	/**
	 * Asserts a statement assuming we have a data annotation property. Uses {@link OWLAnnotationAssertionAxiom}.
	 * if dataTypeUri is null, uses a default literal type.
	 */
	public static synchronized void assertAnnotationData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue, String dataTypeUri )
	{
		checkNonNullTriple ( "assertAnnotationData", subjectUri, propertyUri, propertyValue, dataTypeUri );
		if ( DEBUG_FLAG ) return;
		
		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
		
    OWLLiteral literal;
    if ( dataTypeUri == null )
    	literal = owlFactory.getOWLLiteral ( propertyValue );
    else
    {
    	OWLDatatype dataType = owlFactory.getOWLDatatype ( IRI.create ( dataTypeUri ) );
    	literal = owlFactory.getOWLLiteral ( propertyValue, dataType );
    }
		
		owlMgr.addAxiom ( model, owlFactory.getOWLAnnotationAssertionAxiom ( 
			owlFactory.getOWLAnnotationProperty ( IRI.create ( propertyUri ) ),
			IRI.create ( subjectUri ),
			literal
		));
	}
	
	
	
	/**
	 * Wrapper with dataTypeUri = null.
	 */
	public static synchronized boolean testDataAssertionData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue )
	{
		return testDataAssertionData ( model, subjectUri, propertyUri, propertyValue, null );
	}
	
	/**
	 * Checks if this statement about a data property exists. If dataTypeUri = null, uses a default type
	 * literal. 
	 */
	public static synchronized boolean testDataAssertionData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue, String dataTypeUri )
	{
		checkNonNullTriple ( "testDataAssertionData", subjectUri, propertyUri, propertyValue, dataTypeUri );
		
		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
		
    OWLLiteral literal;
    if ( dataTypeUri == null )
    	literal = owlFactory.getOWLLiteral ( propertyValue );
    else
    {
    	OWLDatatype dataType = owlFactory.getOWLDatatype ( IRI.create ( dataTypeUri ) );
    	literal = owlFactory.getOWLLiteral ( propertyValue, dataType );
    }

		return model.containsAxiomIgnoreAnnotations ( owlFactory.getOWLDataPropertyAssertionAxiom ( 
			owlFactory.getOWLDataProperty ( IRI.create( propertyUri )), 
			owlFactory.getOWLNamedIndividual ( IRI.create ( subjectUri )),
			literal
		));		
	}

	/**
	 * Checks if this statement about an object property exists.
	 */
	public static synchronized boolean testLinkAssertion ( OWLOntology model, String subjectUri, String propertyUri, String objectUri )
	{
		checkNonNullTriple ( "testLinkAssertion", subjectUri, propertyUri, objectUri, null );

		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();

		return model.containsAxiom ( owlFactory.getOWLObjectPropertyAssertionAxiom ( 
			owlFactory.getOWLObjectProperty ( IRI.create( propertyUri )), 
			owlFactory.getOWLNamedIndividual ( IRI.create ( subjectUri )),  
			owlFactory.getOWLNamedIndividual ( IRI.create ( objectUri ))  
		));
	}
	
	/**
	 * Assert that a subject URI is an OWL indiviudal, instance of a class URI. Uses {@link OWLClassAssertionAxiom}. 
	 */
	public static synchronized boolean testIndividual ( OWLOntology model, String individualUri, String classUri ) 
	{
		if ( StringUtils.trimToNull ( individualUri ) == null || StringUtils.trimToNull ( classUri ) == null ) 
			throw new RdfMappingException ( String.format ( 
				"assertIndividual ( '%s', '%s' ): cannot accept null paramters",
				individualUri, classUri
		)); 

		OWLOntologyManager owlMgr = model.getOWLOntologyManager ();
		OWLDataFactory owlFactory = owlMgr.getOWLDataFactory ();
				
		return model.containsAxiom ( owlFactory.getOWLClassAssertionAxiom (
			owlFactory.getOWLClass ( IRI.create ( classUri )), 
			owlFactory.getOWLNamedIndividual( IRI.create ( individualUri ))
		));		
	}	
	
	
	
	/**
	 * Used above to check that we have non-null parameters. dataTypeUri is not checked, only used to report error messages.
	 * methodName is only used for logging.
	 * 
	 */
	private static void checkNonNullTriple ( String methodName, String subjectUri, String propertyUri, String objectValueOrUri, String dataTypeUri )
	{
		if ( StringUtils.trimToNull ( subjectUri ) == null 
				|| StringUtils.trimToNull ( propertyUri ) == null || StringUtils.trimToNull ( objectValueOrUri ) == null 
		)
		{
			if ( dataTypeUri == null )
				throw new RdfMappingException ( String.format (
					"Cannot assert an RDF triple with null elements: %s ( '%s', '%s', '%s' )", 
					methodName, subjectUri, propertyUri, StringUtils.abbreviate ( objectValueOrUri, 255 ) ));
			else
				throw new RdfMappingException ( String.format (
					"Cannot assert an RDF triple with null elements: %s ( '%s', '%s', '%s', '%s' )", 
					methodName, subjectUri, propertyUri, StringUtils.abbreviate ( objectValueOrUri, 255 ), dataTypeUri ));
		}
		
		log.trace ( "doing {} ( '{}', '{}', '{}' )", new String [] { 
			methodName, subjectUri, propertyUri, StringUtils.abbreviate ( objectValueOrUri, 50 ) } 
		);
	}
	
}
