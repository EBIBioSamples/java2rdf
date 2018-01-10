package uk.ac.ebi.fg.java2rdf.mapping;

import static info.marcobrandizi.rdfutils.commonsrdf.CommonsRDFUtils.COMMUTILS;
import static info.marcobrandizi.rdfutils.namespaces.NamespaceUtils.iri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

import info.marcobrandizi.rdfutils.commonsrdf.CommonsRDFUtils;
import info.marcobrandizi.rdfutils.jena.SparqlBasedTester;
import info.marcobrandizi.rdfutils.namespaces.NamespaceUtils;
import uk.ac.ebi.fg.java2rdf.mapping.properties.CollectionPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.CompositePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.InversePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.LiteralPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.ResourcePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfUriGenerator;

/**
 * Tests and examples of basic usage. 
 * TODO: there are not so many JUnit assertions at the moment...
 *
 * <dl><dt>date</dt><dd>Apr 17, 2013</dd></dl>
 * @author Marco Brandizi
 *
 */
public class MappersTest
{
	public static class Foo 
	{
		private String name, description;
		private Set<FooChild> children = new HashSet<> ();
		private double price = -1d;

		public String getName () {
			return name;
		}

		public void setName ( String name ) {
			this.name = name;
		}

		public String getDescription () {
			return description;
		}

		public void setDescription ( String description ) {
			this.description = description;
		}

		public Set<FooChild> getChildren () {
			return children;
		}

		public void setChildren ( Set<FooChild> children ) {
			this.children = children;
		}

		public double getPrice () {
			return price;
		}

		public void setPrice ( double price ) {
			this.price = price;
		}
		
	}
	
	public static class FooChild extends Foo
	{
		private Foo parent;

		public Foo getParent () {
			return parent;
		}

		public void setParent ( Foo parent ) {
			this.parent = parent;
		}
	}
	
	/** 
	 * One way to define an RDF mapper is to extend the basic mapper, this will be more common in real situations. 
	 * You typically will want to prepare it all in the class initialiser or a constructor. 
	 *  
	 */
	public static class FooMapper<F extends Foo> extends BeanRdfMapper<F>
	{
		{
			// How beans of F type generates URI identifiers
			this.setRdfUriGenerator ( new RdfUriGenerator<F> () {
				@Override
				public String getUri ( F source, Map<String, Object> params ) {
					return FOONS + source.getName ().toLowerCase ().replace ( ' ', '_' );
				}
			});
			
			// How they are mapped to a RDFS/OWL class
			this.setRdfClassUri ( FOONS + "Foo" );
			
			// How the properties for beans of type F are mapped to RDF/OWL.
			this.addPropertyMapper ( "name", new LiteralPropRdfMapper<F, String> ( FOONS + "name" ) );
			
			// This can be used to map the same bean properties onto multiple RDF properties (either datatype or object)
			this.addPropertyMapper ( "description", new CompositePropRdfMapper<> (
				new LiteralPropRdfMapper<F, String> ( FOONS + "description" ),
				new LiteralPropRdfMapper<F, String> ( iri ( "rdfs:comment" ) ) 
			));
		}
	}

	public static final String FOONS = "http://www.example.com/foo#";
	
	private Graph graph;
	
	private Foo foo;
	
	@Before
	public void initTestObjects ()
	{
		foo = new Foo ();
		foo.setName ( "A Test Object" );
		foo.setDescription ( "A test description" );
		
		// We're sure it will be the Jena flavour, because we're using this dependency here.
		JenaRDF rdf = (JenaRDF) CommonsRDFUtils.COMMUTILS.getRDF ();
		
		// JenaRDF can generate a graph wrapping a new model via createGraph(). This other approach allows for 
		// better control on the way the model is created and set up (at the expense of framework independence,
		// we're doing this in tests and nowhere else in java2rdf).
		graph = rdf.asGraph ( ModelFactory.createDefaultModel () );
		
		NamespaceUtils.registerNs ( "foo", FOONS );
	}

	
	@Test
	public void testBasics () 
	{
		/** 
		 * Anonymous classses is another, less common approach to define the mapping between beans and RDF/OWL. Compare this
		 * to the FooMapper approach above. Of course you can combine the two. 
		 */
		RdfMapperFactory mapFactory = new RdfMapperFactory ( graph ) {{
			this.setMapper ( Foo.class, new BeanRdfMapper<Foo> ( FOONS + "FooChild" ) {{
				this.setRdfUriGenerator ( new RdfUriGenerator<Foo>() {
					@Override
					public String getUri ( Foo source, Map<String, Object> params ) {
						return FOONS + source.getName ().toLowerCase ().replace ( ' ', '_' );
					}
				});
				this.addPropertyMapper ( "name", new LiteralPropRdfMapper<Foo, String> ( FOONS + "name" ) );
				this.addPropertyMapper ( "description", new CompositePropRdfMapper<> (
					new LiteralPropRdfMapper<Foo, String> ( FOONS + "description" ),
					new LiteralPropRdfMapper<Foo, String> ( iri ( "rdfs:comment" ) ) 
				));
				
				// The default literal mapper converts most common Java types to corresponding XSD
				this.addPropertyMapper ( "price", new LiteralPropRdfMapper<Foo, String> ( FOONS + "hasPrice" ) );				
			}});
		}};
		
		foo.setPrice ( 2.5 );
		
		mapFactory.getMapper ( foo ).map ( foo );
		outputRdf ();
		
		assertEquals ( 
			"Wrong name mapping!", 
			foo.getName (),
			COMMUTILS.getObject ( graph, mapFactory.getUri ( foo ), iri ( "foo:name" ) )
			.flatMap ( COMMUTILS::literal2Value )
			.orElse ( null )
	  );
		
		assertEquals ( 
			"Wrong description mapping!", 
			foo.getDescription (),
			COMMUTILS.getObject ( graph, mapFactory.getUri ( foo ), iri ( "rdfs:comment" ), true )
			.flatMap ( COMMUTILS::literal2Value )
			.orElse ( null )
	  );	
		
		assertEquals ( 
			"Wrong description mapping!", 
			foo.getDescription (),
			COMMUTILS.getObject ( graph, mapFactory.getUri ( foo ), iri ( "foo:description" ), true )
			.flatMap ( COMMUTILS::literal2Value )
			.orElse ( null )
	  );		
		

		RDFTerm pricen = COMMUTILS.getObject ( graph, mapFactory.getUri ( foo ), iri ( "foo:hasPrice" ), true ).orElse ( null );
		assertNotNull ( "Price not found!", pricen );
		assertTrue ( "hasPrice node not literal!", pricen instanceof Literal );
		
		Literal pricel = (Literal) pricen;
		assertEquals ( "Price wrong!", String.valueOf ( foo.getPrice () ), pricel.getLexicalForm () );
		assertEquals ( "Price XSD type wrong!", iri ( "xsd:double" ), pricel.getDatatype ().getIRIString () );
	}
	
	/** Tests the mapping of a single-value JavaBean property to an OWL object property */ 
	@Test
	public void testOneOneRelation ()
	{
		RdfMapperFactory mapFactory = new RdfMapperFactory ( graph ) {{
			this.setGraphModel ( graph );
			this.setMapper ( Foo.class, new FooMapper<Foo> () );
			this.setMapper ( FooChild.class, new FooMapper<FooChild> () {{
				this.setRdfClassUri ( FOONS + "FooChild" );
				this.addPropertyMapper ( "parent", new ResourcePropRdfMapper<FooChild, Foo> ( FOONS + "has-parent" ));
			}});
		}};
		
		FooChild child = new FooChild ();
		child.setName ( "A test Child" );
		child.setDescription ( "A test description for a test child" );
		child.setParent ( foo );
		
		mapFactory.map ( child );
		outputRdf ();

		RDFTerm obj = COMMUTILS.getObject ( graph, mapFactory.getUri ( child ), iri ( "foo:has-parent" ), true ).orElse ( null );
		assertNotNull ( "no has-parent returned!", obj );
		assertTrue ( "Wrong node type returned!", obj instanceof IRI );
		assertEquals ( "Wrong uri returned for has-parent!", mapFactory.getUri ( foo ), ( (IRI) obj ).getIRIString () );
	}
	
	/** 
	 * <p>Tests the mapping from a multi-value JavaBean property (i.e., one that returns a {@link Collection}) to 
	 * multiple RDF/OWL statements, each having the same bean's URI and property.</p>
	 * 
	 * <p>This uses {@link CollectionPropRdfMapper}, which is equipped with a {@link ResourcePropRdfMapper single-value property mapper}.   
	 */
	@Test
	public void testOneToManyRelation () 
	{
		RdfMapperFactory mapFactory = new RdfMapperFactory ( graph ) {{
			this.setGraphModel ( graph );
			this.setMapper ( Foo.class, new FooMapper<Foo> () {{
				this.addPropertyMapper ( "children",
					new CollectionPropRdfMapper<Foo, FooChild, String> ( new ResourcePropRdfMapper<Foo, FooChild> ( FOONS + "has-child") )); 
			}});
			this.setMapper ( FooChild.class, new FooMapper<FooChild> () {{
				this.setRdfClassUri ( FOONS + "FooChild" );
				this.addPropertyMapper ( "parent", new ResourcePropRdfMapper<FooChild, Foo> ( FOONS + "has-parent" ));
			}});
		}};
		
		Set<FooChild> children = foo.getChildren ();

		FooChild child1 = new FooChild ();
		child1.setName ( "A test Child 1" );
		child1.setDescription ( "A test description for a test child 1" );
		children.add ( child1 );

		FooChild child2 = new FooChild ();
		child2.setName ( "A test Child 2" );
		child2.setDescription ( "A test description for a test child 2" );
		children.add ( child2 );

		// Loop is avoided here by keeping track of already-mapped objects in the map factory. 
		child1.setParent ( foo );
		child2.setParent ( foo );
		
		mapFactory.map ( foo );
		outputRdf ();
		
		for ( FooChild child: new FooChild[] { child1, child2 } )
		{
			// Verify has-parent
			{
				RDFTerm obj = COMMUTILS.getObject ( graph, mapFactory.getUri ( child ), iri ( "foo:has-parent" ), true ).orElse ( null );
				assertNotNull ( "no has-parent returned for " + child.getName () + "!", obj );
				assertTrue ( "Wrong node type returned!", obj instanceof IRI );
				assertEquals ( "Wrong uri returned for has-parent!", mapFactory.getUri ( foo ), ( (IRI) obj ).getIRIString () );
			}
			
			// Verify has-child
			{
				Triple checkTriple = graph.stream ( 
					COMMUTILS.uri2Resource ( graph, mapFactory.getUri ( foo ) ), 
					COMMUTILS.uri2Property ( graph, iri ( "foo:has-child" ) ), 
					COMMUTILS.uri2Resource ( graph, mapFactory.getUri ( child ) )
				)
				.findFirst ()
				.orElse ( null );
				
				assertNotNull ( "No has-child for " + child.getName () + "!", checkTriple );
			}
		}

	
	}
	
	/**
	 * A complete mapping example, written to show main mapping declarations in one place.
	 * 
	 * The output from this example is like:
	 * <pre>{@code
			@base          <http://www.example.com/foo#> .
			@prefix owl:   <http://www.w3.org/2002/07/owl#> .
			@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
			@prefix foo:   <http://www.example.com/foo#> .
			@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .
			@prefix dcterms: <http://purl.org/dc/terms/> .
			@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
			@prefix foaf:  <http://xmlns.com/foaf/0.1/> .
			@prefix dc:    <http://purl.org/dc/elements/1.1/> .
			
			foo:a_test_child_1  a     foo:FooChild ;
			        rdfs:comment      "A test description for a test child 1" ;
			        foo:description   "A test description for a test child 1" ;
			        foo:has-parent    foo:a_test_object ;
			        foo:is-parent-of  foo:a_test_object ;
			        foo:name          "A test Child 1" .
			
			foo:a_test_object  a     foo:Foo ;
			        rdfs:comment     "A test description" ;
			        foo:description  "A test description" ;
			        foo:has-child    foo:a_test_child_1 , foo:a_test_child_2 ;
			        foo:name         "A Test Object" .
			
			foo:a_test_child_2  a     foo:FooChild ;
			        rdfs:comment      "A test description for a test child 2" ;
			        foo:description   "A test description for a test child 2" ;
			        foo:has-parent    foo:a_test_object ;
			        foo:is-parent-of  foo:a_test_object ;
			        foo:name          "A test Child 2" .
        
	 * }</pre>
	 */
	@Test
	public void testCompleteExample ()
	{
		RdfMapperFactory mapFactory = new RdfMapperFactory ( graph ) {{
			this.setGraphModel ( graph );
			this.setMapper ( Foo.class, new BeanRdfMapper<Foo> () {{
				// How beans of Foo type generates URI identifiers
				this.setRdfUriGenerator ( new RdfUriGenerator<Foo> () {
					@Override
					public String getUri ( Foo source, Map<String, Object> params ) {
						return FOONS + source.getName ().toLowerCase ().replace ( ' ', '_' );
					}
				});
			
				// How they are mapped to a RDFS/OWL class
				this.setRdfClassUri ( FOONS + "Foo" );
				
				// How the properties for beans of type F are mapped to RDF/OWL.
				this.addPropertyMapper ( "name", new LiteralPropRdfMapper<Foo, String> ( FOONS + "name" ) );
				
				// This can be used to map the same bean properties onto multiple RDF properties (either datatype or object)
				this.addPropertyMapper ( "description", new CompositePropRdfMapper<Foo, String> (
					new LiteralPropRdfMapper<Foo, String> ( FOONS + "description" ),
					new LiteralPropRdfMapper<Foo, String> ( iri ( "rdfs:comment" ) ) 
				));

				// One-to-many relationship
				this.addPropertyMapper ( "children",
					new CollectionPropRdfMapper<Foo, FooChild, String> ( new ResourcePropRdfMapper<Foo, FooChild> ( FOONS + "has-child") ));
				
				// Maps from the related objects, instead of the current subject
				this.addPropertyMapper ( "children",
					new CollectionPropRdfMapper<Foo, FooChild, String> (
						new InversePropRdfMapper<Foo, FooChild, String> ( 
							// This is mapped a second time below, with another RDF property
							new ResourcePropRdfMapper<FooChild, Foo> ( FOONS + "is-parent-of" )
				)));
				
			}}); // Foo mapper

			// One-One or Many-to-One relationship
			this.setMapper ( FooChild.class, new FooMapper<FooChild> () {{
				this.setRdfClassUri ( FOONS + "FooChild" );
				this.addPropertyMapper ( "parent", new ResourcePropRdfMapper<FooChild, Foo> ( FOONS + "has-parent" ));
			}});
		}};
		
		Set<FooChild> children = foo.getChildren ();

		FooChild child1 = new FooChild ();
		child1.setName ( "A test Child 1" );
		child1.setDescription ( "A test description for a test child 1" );
		children.add ( child1 );

		FooChild child2 = new FooChild ();
		child2.setName ( "A test Child 2" );
		child2.setDescription ( "A test description for a test child 2" );
		children.add ( child2 );

		// Loop is avoided here by keeping track of already-mapped objects in the map factory. 
		child1.setParent ( foo );
		child2.setParent ( foo );
		
		mapFactory.map ( foo );
		outputRdf ();
		
		// Again, this is Jena-specific, java2rdf doesn't depend on it except in tests.
		SparqlBasedTester tester = new SparqlBasedTester ( 
			( (JenaGraph) graph ).asJenaModel (), NamespaceUtils.asSPARQLProlog () 
		);
		
		tester.ask ( "Noo child 1 instantiation!", "ASK { foo:a_test_child_1 rdf:type foo:FooChild }" );
		tester.ask ( "Noo child 2 instantiation!", "ASK { foo:a_test_child_2 rdf:type foo:FooChild }" );
		tester.ask ( "Noo child 1 descr!", "ASK { foo:a_test_child_1 foo:description 'A test description for a test child 1' }" );
		tester.ask ( "Noo child 1 comment!", "ASK { foo:a_test_child_1 rdfs:comment 'A test description for a test child 1' }" );
		tester.ask ( "Noo child 2 name!", "ASK { foo:a_test_child_2 foo:name 'A test Child 2' }" );
		tester.ask ( "Noo child 1 is-parent-of!", "ASK { foo:a_test_child_1 foo:is-parent-of foo:a_test_object }" );
		tester.ask ( "Noo child 2 is-parent-of!", "ASK { foo:a_test_child_2 foo:is-parent-of foo:a_test_object }" );
		
		tester.ask ( "Noo test-obj instantiation!", "ASK { foo:a_test_object rdf:type foo:Foo }" );
		tester.ask ( "Noo test-obj instantiation!", "ASK { foo:a_test_object foo:name 'A Test Object' }" );
		tester.ask ( "Noo test-obj has-child 1!", "ASK { foo:a_test_object foo:has-child foo:a_test_child_1 }" );
		tester.ask ( "Noo test-obj has-child 1!", "ASK { foo:a_test_object foo:has-child foo:a_test_child_2 }" );
	}
	
	
	private void outputRdf ()
	{
		// This brings us from the generic commons API back to the specific framework. We use Jena here in tests
		// and nowhere else. Your application will likely be more framework-specific. 
		// Note that Commons-RDF doesn't abstract features like RDF serialisation. 
		//
		Model m = ( (JenaGraph) graph ).asJenaModel ();
		m.setNsPrefixes ( NamespaceUtils.getNamespaces () );
		m.write ( System.out, "TURTLE", FOONS );
	}
}
