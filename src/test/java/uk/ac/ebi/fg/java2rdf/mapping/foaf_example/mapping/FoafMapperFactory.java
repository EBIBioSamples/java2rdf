package uk.ac.ebi.fg.java2rdf.mapping.foaf_example.mapping;

import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ebi.fg.java2rdf.mapping.RdfMapperFactory;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.FoafMappingTest;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Article;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Person;
import uk.ac.ebi.fg.java2rdf.utils.NamespaceUtils;

/**
 * This is where you put all the mappers you need together and where you invoke the mapping operations against your
 * source JavaBean objects.
 *
 * <dl><dt>date</dt><dd>20 Oct 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class FoafMapperFactory extends RdfMapperFactory
{
	static 
	{
		// You'll typically do this on a static block, to prepare your own namespaces. The class already manages common 
		// ones. We plan to support the equivalent utility in OWLAPI or Jena in future.
		//
		NamespaceUtils.registerNs ( "ex", 		FoafMappingTest.EXNS );
		NamespaceUtils.registerNs ( "foaf",		"http://xmlns.com/foaf/0.1/" );
	}
	
	public FoafMapperFactory ( OWLOntology knowledgeBase )
	{
		super ( knowledgeBase );
		this.setMapper ( Article.class, new ArticleMapper () );
		this.setMapper ( Person.class, new PersonMapper () );
	}
}
