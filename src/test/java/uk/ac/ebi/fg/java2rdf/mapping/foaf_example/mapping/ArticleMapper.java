package uk.ac.ebi.fg.java2rdf.mapping.foaf_example.mapping;

import static info.marcobrandizi.rdfutils.namespaces.NamespaceUtils.iri;

import java.util.Map;

import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Article;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Person;
import uk.ac.ebi.fg.java2rdf.mapping.properties.CollectionPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.CompositePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.LiteralPropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.properties.ResourcePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.rdfgen.RdfUriGenerator;


/**
 * The mapper for {@link Article}. java2rdf works with class mapper, where every mapper maps a JavaBean class and 
 * contains JavaBean property mappers.
 *
 * <dl><dt>date</dt><dd>20 Oct 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class ArticleMapper extends BeanRdfMapper<Article>
{
	{
		// How beans of Article type generates URI identifiers
		this.setRdfUriGenerator ( new RdfUriGenerator<Article> () {
			@Override
			public String getUri ( Article source, Map<String, Object> params ) {
				return iri ( "ex:article/" + source.getId () );
			}
		});
		
		// How they are mapped to a RDFS/OWL class
		this.setRdfClassUri ( iri ( "foaf", "Document" ) );
		
		// How to map primitive Java type to OWL datatype properties (or RDF-S) ( maps String getTitle () ) 
		this.addPropertyMapper ( "title", new LiteralPropRdfMapper<Article, String> ( iri ( "dcterms", "title" ) ) );

		// How to map resource links ( maps Person getEditor() )
		this.addPropertyMapper ( "editor", new ResourcePropRdfMapper<Article, Person> ( iri ( "dcterms", "publisher" )) );

		// How to map collection properties ( maps Set<Person> getAuthors () )
		this.addPropertyMapper ( "authors", new CollectionPropRdfMapper<Article, Person, String> ( 
			new ResourcePropRdfMapper<Article, Person> ( iri ( "dcterms", "creator" ) ) 
		));
		
		// This can be used to map the same bean properties onto multiple RDF properties (either datatype or object)
		// ( maps String getAbstractText() ) 
		this.addPropertyMapper ( "abstractText", new CompositePropRdfMapper<> (
			new LiteralPropRdfMapper<Article, String> ( iri ( "dcterms:abstract" ) ),
			new LiteralPropRdfMapper<Article, String> ( iri ( "rdfs:comment" ) ) 
		));
	}
}
