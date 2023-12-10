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
package org.girod.ontobrowser.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.BooleanClassDescription;
import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlAutoIndividual;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDeclaredIndividual;
import org.girod.ontobrowser.model.OwlEquivalentExpression;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * A helper for boolean expressions.
 *
 * @since 0.7
 */
public class ExpressionsHelper {
   private final OwlSchema graph;
   private final GraphExtractor extractor;
   private final AnnotationsHelper annotationsHelper;

   public ExpressionsHelper(GraphExtractor extractor, OwlSchema graph) {
      this.extractor = extractor;
      this.graph = graph;
      this.annotationsHelper = extractor.getAnnotationsHelper();
   }

   public void addEquivalentProperties(Map<ElementKey, Set<ElementKey>> equivalentProperties, OntProperty thisProperty, ElementKey key) {
      Set<ElementKey> set;
      if (equivalentProperties.containsKey(key)) {
         set = equivalentProperties.get(key);
      } else {
         set = new HashSet<>();
         equivalentProperties.put(key, set);
      }
      ExtendedIterator<? extends OntProperty> it = thisProperty.listEquivalentProperties();
      while (it.hasNext()) {
         OntProperty theProperty = it.next();
         if (theProperty.getLocalName() == null) {
            extractor.addError("property.noname");
            continue;
         }
         ElementKey otherKey = new ElementKey(theProperty.getNameSpace(), theProperty.getLocalName());
         set.add(otherKey);
      }
   }

   public void addEquivalentClasses(Map<ElementKey, Set<ElementKey>> equivalentClasses, OntClass thisClass, OwlClass owlClass) {
      ElementKey key = owlClass.getKey();
      Set<ElementKey> set;
      if (equivalentClasses.containsKey(key)) {
         set = equivalentClasses.get(key);
      } else {
         set = new HashSet<>();
         equivalentClasses.put(key, set);
      }
      ExtendedIterator<OntClass> it = thisClass.listEquivalentClasses();
      while (it.hasNext()) {
         OntClass theClass = it.next();
         if (theClass.getLocalName() == null) {
            if (theClass.isUnionClass()) {
               UnionClass unionClass = theClass.asUnionClass();
               computeBooleanExpression(owlClass, unionClass);
            } else if (theClass.isIntersectionClass()) {
               IntersectionClass intersectionClass = theClass.asIntersectionClass();
               computeBooleanExpression(owlClass, intersectionClass);
            } else if (theClass.isEnumeratedClass()) {
               EnumeratedClass enumClass = theClass.asEnumeratedClass();
               computeEnumeratedClass(owlClass, enumClass);
            } else {
               extractor.addInfo("equivalentclass.nothandled", key);
            }
         } else {
            ElementKey otherKey = new ElementKey(theClass.getNameSpace(), theClass.getLocalName());
            set.add(otherKey);
         }
      }
   }

   private void computeEnumeratedClass(OwlClass owlClass, EnumeratedClass enumClass) {
      OwlEquivalentExpression equivalentExpression = new OwlEquivalentExpression(owlClass, enumClass);
      RDFList list = enumClass.getOneOf();
      ExtendedIterator<RDFNode> it = list.iterator();
      while (it.hasNext()) {
         RDFNode node = it.next();
         if (node.isResource()) {
            Resource resource = node.asResource();
            String namespace = resource.getNameSpace();
            String name = resource.getLocalName();
            if (resource instanceof OntResource) {
               OntResource ontResource = (OntResource) resource;
               ElementKey key = ElementKey.create(namespace, name);
               if (ontResource.isIndividual()) {
                  if (graph.hasIndividual(key)) {
                     OwlIndividual individual = graph.getIndividual(key);
                     equivalentExpression.addElement(individual);
                  } else {
                     Individual individual = ontResource.asIndividual();
                     OwlIndividual owlIndividual = new OwlDeclaredIndividual(individual);
                     graph.addIndividual(owlIndividual);
                  }
               }
            } else {
               ElementKey key = ElementKey.create(namespace, name);
               if (graph.hasIndividual(key)) {
                  OwlIndividual individual = graph.getIndividual(key);
                  equivalentExpression.addElement(individual);
               } else {
                  OwlAutoIndividual owlIndividual = new OwlAutoIndividual(resource);
                  graph.addIndividual(owlIndividual);
                     StmtIterator iterSmt = resource.listProperties();
                     while (iterSmt.hasNext()) {
                        Statement statement = iterSmt.next();
                        RDFNode node2 = statement.getObject();
                        Property predicate = statement.getPredicate();
                        ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
                        annotationsHelper.addAnnotationValue(node2, owlIndividual, theKey);
                     }
               }
            }
         }
      }
   }

   private void computeBooleanExpression(OwlClass owlClass, BooleanClassDescription expression) {
      OwlEquivalentExpression equivalentExpression = new OwlEquivalentExpression(owlClass, expression);
      // see https://www.iandickinson.me.uk/blog/2005-04-08/jena-tip-navigating-union-or.html
      for (Iterator i = expression.listOperands(); i.hasNext();) {
         OntClass op = (OntClass) i.next();
         if (op.isEnumeratedClass()) {
            EnumeratedClass en = op.asEnumeratedClass();
            ExtendedIterator<? extends OntResource> it2 = en.listOneOf();
            while (it2.hasNext()) {
               OntResource resource = it2.next();
               String namespace = resource.getNameSpace();
               String name = resource.getLocalName();
               if (namespace != null && name != null) {
                  ElementKey key = ElementKey.create(namespace, name);
                  if (resource.isIndividual()) {
                     if (graph.hasIndividual(key)) {
                        OwlIndividual individual = graph.getIndividual(key);
                        equivalentExpression.addElement(individual);
                     } else {
                        Individual individual = resource.asIndividual();
                        OwlIndividual owlIndividual = new OwlDeclaredIndividual(individual);
                        graph.addIndividual(owlIndividual);
                     }
                  } else if (resource.isClass()) {
                     if (graph.hasOwlClass(key)) {
                        OwlClass theClass = graph.getOwlClass(key);
                        equivalentExpression.addElement(theClass);
                     }
                  } else if (resource.isProperty()) {
                     if (graph.hasOwlProperty(key)) {
                        OwlProperty theProperty = graph.getOwlProperty(key);
                        equivalentExpression.addElement(theProperty);
                     }
                  } else {
                     OwlAutoIndividual owlIndividual = new OwlAutoIndividual(resource);
                     graph.addIndividual(owlIndividual);
                     equivalentExpression.addElement(owlIndividual);
                     StmtIterator iterSmt = resource.listProperties();
                     while (iterSmt.hasNext()) {
                        Statement statement = iterSmt.next();
                        RDFNode node = statement.getObject();
                        Property predicate = statement.getPredicate();
                        ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
                        annotationsHelper.addAnnotationValue(node, owlIndividual, theKey);
                     }
                  }
               }
            }
         }
      }
      owlClass.addEquivalentExpression(equivalentExpression);
   }

   public void fillEquivalentClasses(Map<ElementKey, Set<ElementKey>> equivalentKeys) {
      Iterator<Map.Entry<ElementKey, Set<ElementKey>>> it = equivalentKeys.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<ElementKey, Set<ElementKey>> entry = it.next();
         Set<ElementKey> set = entry.getValue();
         if (!set.isEmpty()) {
            OwlClass thisClass = graph.getOwlClass(entry.getKey());
            Iterator<ElementKey> it2 = set.iterator();
            while (it2.hasNext()) {
               ElementKey theKey = it2.next();
               if (graph.hasOwlClass(theKey)) {
                  OwlClass theOtherClass = graph.getOwlClass(theKey);
                  thisClass.addEquivalentClass(theOtherClass);
               }
            }
         }
      }
   }

   public void fillEquivalentProperties(Map<ElementKey, Set<ElementKey>> equivalentKeys) {
      Iterator<Map.Entry<ElementKey, Set<ElementKey>>> it = equivalentKeys.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<ElementKey, Set<ElementKey>> entry = it.next();
         Set<ElementKey> set = entry.getValue();
         if (!set.isEmpty()) {
            OwlProperty thisProperty = graph.getOwlProperty(entry.getKey());
            Iterator<ElementKey> it2 = set.iterator();
            while (it2.hasNext()) {
               ElementKey theKey = it2.next();
               if (graph.hasOwlProperty(theKey)) {
                  OwlProperty theOtherProperty = graph.getOwlProperty(theKey);
                  thisProperty.addEquivalentProperty(theOtherProperty);
               }
            }
         }
      }
   }
}
