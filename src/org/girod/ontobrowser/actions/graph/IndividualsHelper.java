/*
Copyright (c) 2023 Hervé Girod
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
package org.girod.ontobrowser.actions.graph;

import java.util.Iterator;
import java.util.Map;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.girod.ontobrowser.model.DatatypePropertyValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.ObjectPropertyValue;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * A helper for boolean expressions.
 *
 * @version 0.8
 */
public class IndividualsHelper {
   private final OwlSchema graph;
   private final GraphExtractor extractor;

   public IndividualsHelper(GraphExtractor extractor, OwlSchema graph) {
      this.extractor = extractor;
      this.graph = graph;
   }

   public void addIndividualProperties(OwlIndividual owlIndividual) {
      Resource individual = owlIndividual.getIndividual();
      Map<ElementKey, OwlClass> parentClasses = owlIndividual.getParentClasses();
      Iterator<OwlClass> it = parentClasses.values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         Iterator<OwlProperty> it2 = theClass.getDomainOwlProperties().values().iterator();
         while (it2.hasNext()) {
            OwlProperty property = it2.next();
            if (property.isObjectProperty()) {
               OwlObjectProperty objectproperty = (OwlObjectProperty) property;
               ObjectProperty _jenaproperty = objectproperty.getProperty();
               Resource resource = individual.getPropertyResourceValue(_jenaproperty);
               if (resource != null) {
                  String namespace = resource.getNameSpace();
                  String name = resource.getLocalName();
                  ElementKey targetKey = ElementKey.create(namespace, name);
                  if (graph.hasIndividual(targetKey)) {
                     OwlIndividual target = graph.getIndividual(targetKey);
                     ObjectPropertyValue value = new ObjectPropertyValue(objectproperty, owlIndividual, target);
                     owlIndividual.addObjectPropertyValue(value);
                  }
               }
            } else if (property.isDatatypeProperty()) {
               OwlDatatypeProperty datatypeproperty = (OwlDatatypeProperty) property;
               DatatypeProperty _jenaproperty = datatypeproperty.getProperty();
               if (individual instanceof OntResource) {
                  RDFNode node = ((OntResource) individual).getPropertyValue(_jenaproperty);
                  if (node != null && node.isLiteral()) {
                     Literal literal = node.asLiteral();
                     String value = literal.getString();
                     String datatypeURI = literal.getDatatypeURI();
                     ElementKey datatypeKey = ElementKey.createFromURI(datatypeURI);
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
   }
}
