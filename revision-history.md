# Revision History

*This file was last reviewed on 2023-01-41*

## 4.0.2-SNAPSHOT
* Current snapshot.

## 4.0.1
* Updating rdfutils dependency.

## 4.0
* Apache commons RDF removed. **Now jena2rdf depends on Jena again**, sorry, commons RDF seems dead and I 
  don't have time anymore to maintain my own patches, which are never merged into the main codebase.

## 3.0
* **FROM NOW ON, JDK < 11 IS NO LONGER SUPPORTED**. java2rdf will possibly work with 1.8 for a
  while (until we start introducing incompatible changes), but that's not officially 
  supported.
* Continuous integration migrated to github Actions.

## 2.1.4
* Release to link jutils/rdfutils to latest stable releases.

## 2.1.3
* Needed to amend dependency error.

## 2.1
* Migration to Jena 3.9.0.
* Bugfix in `RdfTrueGenerator`.

## 2.0.1
* Needed to amend dependency error.
  
## 2.0
* Migration to rdfutils package, and OWLApi/Jena pluggability.

## 1.0
* First actual release. This was made before substantial changes and with the purpose to keep some 
  track of the version that was used in projects like EBI RDF platform (used to be 1.0-SNAPSHOT). It was 
  changed straight after, so never actually used.
  