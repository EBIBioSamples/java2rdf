package uk.ac.ebi.fg.java2rdf.mapping.foaf_example.mapping;

import static info.marcobrandizi.rdfutils.namespaces.NamespaceUtils.iri;
import static uk.ac.ebi.fg.java2rdf.utils.Java2RdfUtils.hashUriSignature;

import java.util.Map;

import uk.ac.ebi.fg.java2rdf.mapping.BeanRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.foaf_example.model.Person;
import uk.ac.ebi.fg.java2rdf.mapping.properties.OwlDatatypePropRdfMapper;
import uk.ac.ebi.fg.java2rdf.mapping.urigen.RdfUriGenerator;

/**
 * The mapper for {@link Person}. java2rdf works with class mapper, where every mapper maps a JavaBean class and 
 * contains JavaBean property mappers.
 *
 * <dl><dt>date</dt><dd>20 Oct 2014</dd></dl>
 * @author Marco Brandizi
 *
 */
public class PersonMapper extends BeanRdfMapper<Person>
{
	{
		// How beans of Person type generates URI identifiers
		this.setRdfUriGenerator ( new RdfUriGenerator<Person> () {
			@Override
			public String getUri ( Person source, Map<String, Object> params ) {
				return iri ( "ex:person/" + hashUriSignature ( source.getEmail () ) );
			}
		});
		
		// How they are mapped to a RDFS/OWL class
		this.setRdfClassUri ( iri ( "foaf", "Person" ) );
		
		// How to map primitive Java type to OWL datatype properties (or RDF-S).
		this.addPropertyMapper ( "name", new OwlDatatypePropRdfMapper<Person, String> ( iri ( "foaf:givenName" ) ) );
		this.addPropertyMapper ( "surname", new OwlDatatypePropRdfMapper<Person, String> ( iri ( "foaf:familyName" ) ) );
	}	
}
