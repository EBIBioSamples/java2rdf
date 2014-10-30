#java2rdf

A simple library to maps Java objects and Java beans onto RDF/OWL. Contrary to other similar tools, java2rdf is based on declaring mappings between JavaBeans and RDF/OWL entities in dedicated mapping Java classes, so not in configuration files (you don't have to learn yet another XML schema), not via Java annotations (you don't always have access to, or want to spoil the source model).  

We have a [5-min presentation about java2rdf](http://www.slideshare.net/mbrandizi/java2rdf). That shows code exerpts from [this example](https://github.com/EBIBioSamples/java2rdf/tree/master/src/test/java/uk/ac/ebi/fg/java2rdf/mapping/foaf_example), we also have  [another example](https://github.com/EBIBioSamples/java2rdf/blob/master/src/test/java/uk/ac/ebi/fg/java2rdf/mapping/MappersTest.java), showing a slightly different, 'quick-n-dirty' way to define object mappings (we recommend the former approach in real applications).

[Here](http://www.marcobrandizi.info/mysite/node/153) you can read something more about java2rdf and the BioSD Linked Data Project, for which it was built.
