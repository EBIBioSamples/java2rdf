package uk.ac.ebi.fg.java2rdf.mapping.foaf_example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.mapping.FoafMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Article;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Person;
import uk.ac.ebi.fg.java2rdf.utils.NamespaceUtils;

/**
 * A simple JUnit test, which shows how to use java2rdf, once you've defined the mappers.
 *
 * <dl><dt>date</dt><dd>20 Oct 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class FoafMappingTest
{
	public static final String EXNS = "http://www.example.com/ex/";
	
	@Test
	public void testMapping () throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException
	{
		Article article = new Article ( 
			123, 
			"The semantic web", 
			"A new form of Web content that is meaningful to computers will unleash a revolution of new possibilities" 
		);

		Person tbl = new Person ( "tbl@w3cc.com", "Tim", "Berners Lee" );
		Person hendler = new Person ( "heendlr@cs.rpi.edu", "James", "Hendler" );
		Person lassila = new Person ( "oral@w3.com", "Ora", "Lassila" );
		
		Person editor = new Person ( "ed@scienceamerican.com", "John", "Smith" );

		Set<Person> authors = new HashSet<> ();
		authors.add ( tbl ); authors.add ( hendler ); authors.add ( lassila );
		
		article.setAuthors ( authors );
		article.setEditor ( editor );
		
		// This is typical OWLAPI code
		OWLOntologyManager owlMgr = OWLManager.createOWLOntologyManager ();
		OWLOntology kb = owlMgr.createOntology ( IRI.create ( EXNS + "ontology" ) );

		// Our factory
		FoafMapperFactory mf = new FoafMapperFactory ( kb );
		
		// Here we go, starting from the top, a set of graph roots (or even all objects you have)
		mf.map ( article );
		
		// Again, this OWLAPI
		PrefixOWLOntologyFormat fmt = new TurtleOntologyFormat ();
		NamespaceUtils.copy2OwlApi ( fmt ); // this pours our namespaces in OWLAPI
		owlMgr.saveOntology ( mf.getKnowledgeBase (), fmt, new FileOutputStream ( "target/foaf_example.ttl" ) );
	}
	
}
