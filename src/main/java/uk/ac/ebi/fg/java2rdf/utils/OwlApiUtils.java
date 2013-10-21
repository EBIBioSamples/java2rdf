package uk.ac.ebi.fg.java2rdf.utils;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMappingException;

/**
 * TODO: Comment me!
 *
 * <dl><dt>date</dt><dd>Mar 23, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class OwlApiUtils
{
	private static Logger log = LoggerFactory.getLogger ( OwlApiUtils.class );
	
	private static final boolean DEBUG_FLAG = false;
	
	
	public static synchronized void assertData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue )
	{
		assertData ( model, subjectUri, propertyUri, propertyValue, null );
	}
	
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
	
	public static synchronized void assertAnnotationData ( OWLOntology model, String subjectUri, String propertyUri, String propertyValue ) 
	{
		assertAnnotationData ( model, subjectUri, propertyUri, propertyValue, null );
	}

	
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
