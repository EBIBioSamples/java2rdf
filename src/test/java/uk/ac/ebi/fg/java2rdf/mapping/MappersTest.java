package uk.ac.ebi.fg.java2rdf.mapping;

import static uk.ac.ebi.fg.java2rdf.utils.NamespaceUtils.uri;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.ebi.fg.java2rdf.mapping.properties.CollectionPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.CompositePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.InversePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.OwlDatatypePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.OwlObjPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfUriGenerator;

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
			this.addPropertyMapper ( "name", new OwlDatatypePropRdfMapper<F, String> ( FOONS + "name" ) );
			
			// This can be used to map the same bean properties onto multiple RDF properties (either datatype or object)
			this.addPropertyMapper ( "description", new CompositePropRdfMapper<> (
				new OwlDatatypePropRdfMapper<F, String> ( FOONS + "description" ),
				new OwlDatatypePropRdfMapper<F, String> ( uri ( "rdfs:comment" ) ) 
			));
		}
	}

	public static final String FOONS = "http://www.example.com/foo#";
	
	private OWLOntology onto;
	private OWLOntologyManager owlMgr;
	
	private Foo foo;
	
	@Before
	public void doBasicMapping () throws OWLOntologyCreationException
	{
		foo = new Foo ();
		foo.setName ( "A Test Object" );
		foo.setDescription ( "A test description" );
		
		owlMgr = OWLManager.createOWLOntologyManager ();
		onto = owlMgr.createOntology ( IRI.create ( FOONS + "ontology" ) );
	}

	
	@Test
	public void testBasics () throws OWLOntologyCreationException, OWLOntologyStorageException 
	{
		/** 
		 * Anonymous classses is another, less common approach to define the mapping between beans and RDF/OWL. Compare this
		 * to the FooMapper approach above. Of course you can combine the two. 
		 */
		RdfMapperFactory mapFactory = new RdfMapperFactory ( onto ) {{
			this.setMapper ( Foo.class, new BeanRdfMapper<Foo> ( FOONS + "FooChild" ) {{
				this.setRdfUriGenerator ( new RdfUriGenerator<Foo>() {
					@Override
					public String getUri ( Foo source, Map<String, Object> params ) {
						return FOONS + source.getName ().toLowerCase ().replace ( ' ', '_' );
					}
				});
				this.addPropertyMapper ( "name", new OwlDatatypePropRdfMapper<Foo, String> ( FOONS + "name" ) );
				this.addPropertyMapper ( "description", new CompositePropRdfMapper<> (
					new OwlDatatypePropRdfMapper<Foo, String> ( FOONS + "description" ),
					new OwlDatatypePropRdfMapper<Foo, String> ( uri ( "rdfs", "comment" ) ) 
				));
			}});
		}};
		
		mapFactory.getMapper ( foo ).map ( foo );
		outputKB ();
	}
	
	/** Tests the mapping of a single-value JavaBean property to an OWL object property */ 
	@Test
	public void testOneOneRelation () throws OWLOntologyCreationException, OWLOntologyStorageException 
	{
		RdfMapperFactory mapFactory = new RdfMapperFactory ( onto ) {{
			this.setKnowledgeBase ( onto );
			this.setMapper ( Foo.class, new FooMapper<Foo> () );
			this.setMapper ( FooChild.class, new FooMapper<FooChild> () {{
				this.setRdfClassUri ( FOONS + "FooChild" );
				this.addPropertyMapper ( "parent", new OwlObjPropRdfMapper<FooChild, Foo> ( FOONS + "has-parent" ));
			}});
		}};
		
		FooChild child = new FooChild ();
		child.setName ( "A test Child" );
		child.setDescription ( "A test description for a test child" );
		child.setParent ( foo );

		mapFactory.map ( child );
		outputKB ();
	}
	
	/** 
	 * <p>Tests the mapping from a multi-value JavaBean property (i.e., one that returns a {@link Collection}) to 
	 * multiple RDF/OWL statements, each having the same bean's URI and property.</p>
	 * 
	 * <p>This uses {@link CollectionPropRdfMapper}, which is equipped with a {@link OwlObjPropRdfMapper single-value property mapper}.   
	 */
	@Test
	public void testOneToManyRelation () throws OWLOntologyCreationException, OWLOntologyStorageException 
	{
		RdfMapperFactory mapFactory = new RdfMapperFactory ( onto ) {{
			this.setKnowledgeBase ( onto );
			this.setMapper ( Foo.class, new FooMapper<Foo> () {{
				this.addPropertyMapper ( "children",
					new CollectionPropRdfMapper<Foo, FooChild> ( new OwlObjPropRdfMapper<Foo, FooChild> ( FOONS + "has-child") )); 
			}});
			this.setMapper ( FooChild.class, new FooMapper<FooChild> () {{
				this.setRdfClassUri ( FOONS + "FooChild" );
				this.addPropertyMapper ( "parent", new OwlObjPropRdfMapper<FooChild, Foo> ( FOONS + "has-parent" ));
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
		
		outputKB ();
	}
	
	/**
	 * A complete mapping example, written to show main mapping declarations in one place.
	 * 
	 * The output from this example is:
	 * <pre>
###  http://www.example.com/foo#a_test_child_1
foo:a_test_child_1 rdf:type foo:FooChild ,
                            owl:NamedIndividual ;

                   foo:name "A test Child 1"^^xsd:string ;
                   foo:description "A test description for a test child 1"^^xsd:string ;
                   rdfs:comment "A test description for a test child 1"^^xsd:string ;
                   foo:has-parent foo:a_test_object ;
                   foo:is-parent-of foo:a_test_object .

###  http://www.example.com/foo#a_test_child_2
foo:a_test_child_2 rdf:type foo:FooChild ,
                            owl:NamedIndividual ;
                   foo:name "A test Child 2"^^xsd:string ;
                   rdfs:comment "A test description for a test child 2"^^xsd:string ;
                   foo:description "A test description for a test child 2"^^xsd:string ;
                   foo:has-parent foo:a_test_object ;
                   foo:is-parent-of foo:a_test_object .

###  http://www.example.com/foo#a_test_object
foo:a_test_object rdf:type foo:Foo ,
                           owl:NamedIndividual ;
                  foo:name "A Test Object"^^xsd:string ;
                  rdfs:comment "A test description"^^xsd:string ;
                  foo:description "A test description"^^xsd:string ;
                  foo:has-child foo:a_test_child_1 ,
                                foo:a_test_child_2 .
	 * </pre>
	 */
	@Test
	public void testCompleteExample () throws OWLOntologyCreationException, OWLOntologyStorageException 
	{
		RdfMapperFactory mapFactory = new RdfMapperFactory ( onto ) {{
			this.setKnowledgeBase ( onto );
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
				this.addPropertyMapper ( "name", new OwlDatatypePropRdfMapper<Foo, String> ( FOONS + "name" ) );
				
				// This can be used to map the same bean properties onto multiple RDF properties (either datatype or object)
				this.addPropertyMapper ( "description", new CompositePropRdfMapper<Foo, String> (
					new OwlDatatypePropRdfMapper<Foo, String> ( FOONS + "description" ),
					new OwlDatatypePropRdfMapper<Foo, String> ( uri ( "rdfs", "comment" ) ) 
				));

				// One-to-many relationship
				this.addPropertyMapper ( "children",
					new CollectionPropRdfMapper<Foo, FooChild> ( new OwlObjPropRdfMapper<Foo, FooChild> ( FOONS + "has-child") ));
				
				// Maps from the related objects, instead of the current subject
				this.addPropertyMapper ( "children",
					new CollectionPropRdfMapper<Foo, FooChild> (
						new InversePropRdfMapper<Foo, FooChild> ( 
							// This is mapped a second time below, with another RDF property
							new OwlObjPropRdfMapper<FooChild, Foo> ( FOONS + "is-parent-of" )
				)));
				
			}}); // Foo mapper

			// One-One or Many-to-One relationship
			this.setMapper ( FooChild.class, new FooMapper<FooChild> () {{
				this.setRdfClassUri ( FOONS + "FooChild" );
				this.addPropertyMapper ( "parent", new OwlObjPropRdfMapper<FooChild, Foo> ( FOONS + "has-parent" ));
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
		
		outputKB ();
	}
	
	
	private void outputKB () throws OWLOntologyStorageException
	{
		PrefixOWLOntologyFormat fmt = new TurtleOntologyFormat ();
		fmt.setPrefix ( "foo", FOONS );
		owlMgr.saveOntology ( onto, fmt, System.out );
	}
}
