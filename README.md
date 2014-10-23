#java2rdf

A simple library to maps Java objects and Java beans onto RDF/OWL. Contrary to other similar tools, java2rdf is based on declaring mappings between JavaBeans and RDF/OWL entities in dedicated mapping Java classes, so not in configuration files (you don't have to learn yet another XML schema), not via Java annotations (you don't always have access to, or want to spoil the source model).  

[This](https://github.com/EBIBioSamples/java2rdf/tree/master/src/test/java/uk/ac/ebi/fg/java2rdf/mapping/foaf_example) and [this](https://github.com/EBIBioSamples/java2rdf/blob/master/src/test/java/uk/ac/ebi/fg/java2rdf/mapping/MappersTest.java) are hopefully clear examples of how it works.

[Here](http://www.marcobrandizi.info/mysite/node/153) you can read something more about java2rdf and the BioSD Linked Data Project, for which it was built.

