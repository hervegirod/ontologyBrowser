/*
Copyright (c) 2023, 2024 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/ontologyBrowser
 */
package org.girod.ontobrowser.parsers.graph;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.girod.ontobrowser.model.DatatypePropertyValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.ObjectPropertyValue;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * A helper for individuals.
 *
 * @version 0.13
 */
public class IndividualsHelper {
   private final OwlSchema graph;

   public IndividualsHelper(OwlSchema graph) {
      this.graph = graph;
   }

   public void addIndividualProperties(OwlIndividual owlIndividual) {
      Resource individual = owlIndividual.getIndividual();

      StmtIterator smt = individual.listProperties();
      while (smt.hasNext()) {
         Statement statement = smt.nextStatement();
         Property property = statement.getPredicate();
         String namespace = graph.getNamespace(property);
         String name = property.getLocalName();
         ElementKey propertyKey = ElementKey.create(namespace, name);
         RDFNode node = statement.getObject();
         if (node != null && graph.hasOwlProperty(propertyKey)) {
            OwlProperty owlProperty = graph.getOwlProperty(propertyKey);
            if (owlProperty.isObjectProperty() && node.isResource()) {
               Resource resource = node.asResource();
               OwlObjectProperty objectproperty = (OwlObjectProperty) owlProperty;
               namespace = graph.getNamespace(resource);
               name = resource.getLocalName();
               ElementKey targetKey = ElementKey.create(namespace, name);
               if (graph.hasIndividual(targetKey)) {
                  OwlIndividual target = graph.getIndividual(targetKey);
                  ObjectPropertyValue value = new ObjectPropertyValue(objectproperty, owlIndividual, target);
                  owlIndividual.addObjectPropertyValue(value);
               }
            } else if (owlProperty.isDatatypeProperty() && node.isLiteral()) {
               OwlDatatypeProperty datatypeproperty = (OwlDatatypeProperty) owlProperty;
               Literal literal = node.asLiteral();
               String value = literal.getString();
               String datatypeURI = literal.getDatatypeURI();
               ElementKey datatypeKey = ElementKey.createFromURI(datatypeURI);
               if (! graph.hasDatatype(datatypeKey)) {
                  OwlDatatype datatype = new OwlDatatype(datatypeKey);
                  graph.addDatatype(datatype);
               }
               if (datatypeKey != null && graph.hasDatatype(datatypeKey)) {
                  OwlDatatype datatype = graph.getDatatype(datatypeKey);
                  DatatypePropertyValue propValue = new DatatypePropertyValue(datatypeproperty, owlIndividual, datatype, value);
                  owlIndividual.addDatatypePropertyValue(propValue);
               }
            }
         }
      }
   }
}
