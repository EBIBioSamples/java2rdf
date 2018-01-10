package uk.ac.ebi.fg.java2rdf.mapping.foaf_example;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import info.marcobrandizi.rdfutils.commonsrdf.CommonsRDFUtils;
import info.marcobrandizi.rdfutils.jena.SparqlBasedTester;
import info.marcobrandizi.rdfutils.namespaces.NamespaceUtils;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.mapping.FoafMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Article;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Person;

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
	
	static {
		NamespaceUtils.registerNs ( "ex", EXNS );
		NamespaceUtils.registerNs ( "exart", EXNS + "article/" );
	}
	
	@Test
	public void testMapping () throws Exception
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
		
		// You must provide a graph to the mapper factory. Graph is a wrapper from the Apache commons-rdf
		// API, which is then implemented with a specific RDF framework. Here Jena is used for the latter, but
		// only for the tests, javq2rdf is pure commons-rdf and doesn't depend on a specific framework.
		// You'll java2rdf client will likely be, in the way shown here.
		
		// We're sure it will be the Jena flavour, because we're using this dependency here.
		JenaRDF rdf = (JenaRDF) CommonsRDFUtils.COMMUTILS.getRDF ();
		
		// JenaRDF can generate a graph wrapping a new model via createGraph(). This other approach allows for 
		// better control on the way the model is created and set up (at the expense of framework independence)
		Model model = ModelFactory.createDefaultModel ();
		model.setNsPrefixes ( NamespaceUtils.getNamespaces () );

		Graph graph = rdf.asGraph ( model );
		

		// Our factory
		FoafMapperFactory mf = new FoafMapperFactory ( graph );
		
		// Here we go, starting from the top, a set of graph roots (or even all objects you have)
		mf.map ( article );
		
		// Again, this Jena-specific, since commons-rdf doesn't abstract things like I/O or SPARQL.
		model.write ( new FileWriter ( "target/foaf_example.ttl" ), "TURTLE", EXNS );
		
		// Some tests
		SparqlBasedTester tester = new SparqlBasedTester ( model, NamespaceUtils.asSPARQLProlog () );
		
		tester.ask ( "No John Smith!", 
			"ASK { ?person        a                foaf:Person ;\n" + 
			"        foaf:familyName  'Smith' ;\n" + 
			"        foaf:givenName   'John'.\n" +
			"}" 
		);
		
		tester.ask ( "No Ora Lassila!",
		  "ASK {   ?person    a                foaf:Person ;\n" + 
		  "        foaf:familyName  'Lassila' ;\n" + 
		  "        foaf:givenName   'Ora'.\n" +
		  "}"
		);
		
		tester.ask ( "No sem-web-document!",
		  "ASK { exart:123  a           foaf:Document ;\n" + 
		  "        rdfs:comment       'A new form of Web content that is meaningful to computers will unleash a revolution of new possibilities' ;\n" + 
		  "        dcterms:abstract   'A new form of Web content that is meaningful to computers will unleash a revolution of new possibilities' ;\n" + 
		  "        dcterms:title      'The semantic web' .\n" +
		  "}"
		);

		tester.ask ( "No links to authors!",
			"ASK { exart:123\n" +
		  "        dcterms:creator\n" + 
			"          [ foaf:familyName  'Lassila'; foaf:givenName   'Ora' ],\n" +
			"          [ foaf:familyName  'Hendler'; foaf:givenName   'James' ],\n" +
			"          [ foaf:familyName  'Berners Lee'; foaf:givenName   'Tim' ].\n" +
		  "}"
    );

		tester.ask ( "No link to editor!",
				"ASK { exart:123\n" +
			  "        dcterms:publisher\n" + 
				"          [ foaf:familyName  'Smith'; foaf:givenName   'John' ].\n" +
			  "}"
		);

	}
	
}
