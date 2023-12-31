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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.ontology.OntologyException;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.girod.ontobrowser.model.AnnotatedElement;
import org.girod.ontobrowser.model.AnnotationValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * This class get the annotations on elements in the graph.
 *
 * @version 0.8
 */
public class AnnotationsHelper {
   private final OwlSchema graph;
   private final OntModel model;
   private final SkippedAnnotations skipped = SkippedAnnotations.getInstance();

   public AnnotationsHelper(OwlSchema graph) {
      this.graph = graph;
      this.model = graph.getOntModel();
   }

   public void addSchemaAnnotations() {
      List<AnnotationProperty> annProperties = new ArrayList<>();
      // list annotations
      ExtendedIterator<AnnotationProperty> annotations = model.listAnnotationProperties();
      while (annotations.hasNext()) {
         AnnotationProperty annProperty = annotations.next();
         annProperties.add(annProperty);
      }

      Set<String> imported = model.listImportedOntologyURIs();
      ExtendedIterator<Ontology> it = model.listOntologies();
      while (it.hasNext()) {
         Ontology ontology = it.next();
         String uriAsString = ontology.getURI();
         if (uriAsString == null || imported.contains(uriAsString)) {
            continue;
         }
         this.setDefaultAnnotations(graph, ontology);
         Iterator<AnnotationProperty> it2 = annProperties.iterator();
         while (it2.hasNext()) {
            AnnotationProperty annProperty = it2.next();
            RDFNode theNode = ontology.getPropertyValue(annProperty);
            if (theNode == null) {
               continue;
            }
            ElementKey annkey = ElementKey.create(annProperty.getNameSpace(), annProperty.getLocalName());
            if (skipped.isSkipped(annkey)) {
               continue;
            }
            AnnotationValue value = null;
            if (theNode.isLiteral()) {
               String literal = theNode.asLiteral().getString();
               value = new AnnotationValue.LiteralAnnotationValue(literal);
            } else if (theNode.isURIResource()) {
               uriAsString = theNode.asResource().getURI();
               try {
                  URI uri = new URI(uriAsString);
                  value = new AnnotationValue.URIAnnotationValue(uri);
               } catch (URISyntaxException ex) {
               }
            } else if (theNode.isResource()) {
               Resource resource = theNode.asResource();
               String name = resource.getLocalName();
               String namespace = resource.getNameSpace();
               if (name != null && namespace != null) {
                  ElementKey key = ElementKey.create(namespace, name);
                  if (resource instanceof OntClass) {
                     if (graph.hasOwlClass(key)) {
                        value = new AnnotationValue.ElementAnnotationValue(graph.getOwlClass(key));
                     }
                  } else if (resource instanceof OntProperty) {
                     if (graph.hasOwlProperty(key)) {
                        value = new AnnotationValue.ElementAnnotationValue(graph.getOwlProperty(key));
                     }
                  } else if (resource instanceof Individual) {
                     if (graph.hasIndividual(key)) {
                        value = new AnnotationValue.ElementAnnotationValue(graph.getIndividual(key));
                     }
                  }
               }

            }
            if (value != null) {
               graph.addAnnotation(annkey, value);
            }
         }
      }
   }

   public void addDefaultAnnotations() {
      Iterator<OwlClass> it = graph.getOwlClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass owlClass = it.next();
         OntClass theClass = owlClass.getOntClass();
         if (theClass != null) {
            this.setDefaultAnnotations(owlClass, theClass);
         }
      }

      Iterator<OwlProperty> it2 = graph.getOwlProperties().values().iterator();
      while (it2.hasNext()) {
         OwlProperty owlProperty = it2.next();
         OntProperty theProperty = owlProperty.getProperty();
         if (theProperty != null) {
            this.setDefaultAnnotations(owlProperty, theProperty);
         }
      }

      Iterator<OwlIndividual> it3 = graph.getIndividuals().values().iterator();
      while (it3.hasNext()) {
         OwlIndividual owlIndividual = it3.next();
         Resource resource = owlIndividual.getIndividual();
         if (resource instanceof OntResource) {
            this.setDefaultAnnotations(owlIndividual, (OntResource) resource);
         } else {
            //this.setDefaultAnnotations(owlIndividual, resource);
         }
      }
   }

   private void setDefaultAnnotations(AnnotatedElement element, OntResource resource) {
      String comment = getComments(resource);
      element.setComments(comment);
      setIsDefinedBy(element, resource);
      setVersionInfo(element, resource);
      setLabel(element, resource);
      setSeeAlso(element, resource);
   }

   private String getComments(OntResource resource) {
      StringBuilder buf = new StringBuilder();
      boolean isEmpty = true;
      ExtendedIterator<RDFNode> it = resource.listComments(null);
      while (it.hasNext()) {
         RDFNode node = it.next();
         String s = node.asLiteral().getString();
         buf.append(s);
         if (it.hasNext()) {
            buf.append("\n");
         }
         isEmpty = false;
      }
      if (isEmpty) {
         return null;
      } else {
         return buf.toString();
      }
   }

   private void setLabel(AnnotatedElement element, OntResource resource) {
      try {
         String labelRs = resource.getLabel(null);
         element.setLabel(labelRs);
      } catch (OntologyException e) {
      }
   }

   private void setIsDefinedBy(AnnotatedElement element, OntResource resource) {
      Resource isDefinedBy = resource.getIsDefinedBy();
      if (isDefinedBy != null) {
         if (isDefinedBy.isLiteral()) {
            String _definedBy = isDefinedBy.asLiteral().getString();
            element.setIsDefinedBy(new AnnotationValue.LiteralAnnotationValue(_definedBy));
         } else if (isDefinedBy.isURIResource()) {
            String uriAsString = isDefinedBy.getURI();
            URI uri;
            try {
               uri = new URI(uriAsString);
               if (!graph.hasElement(uri)) {
                  element.setIsDefinedBy(new AnnotationValue.URIAnnotationValue(uri));
               } else {
                  element.setIsDefinedBy(new AnnotationValue.ElementAnnotationValue(graph.getElement(uri)));
               }
            } catch (URISyntaxException ex) {
            }
         }
      }
   }

   private void setSeeAlso(AnnotatedElement element, OntResource resource) {
      Resource seeAlso;
      try {
         seeAlso = resource.getSeeAlso();
      } catch (OntologyException e) {
         if (resource.isURIResource()) {
            String uriAsString = resource.getURI();
            URI uri;
            try {
               uri = new URI(uriAsString);
               if (!graph.hasElement(uri)) {
                  element.setSeeAlso(new AnnotationValue.URIAnnotationValue(uri));
               } else {
                  element.setSeeAlso(new AnnotationValue.ElementAnnotationValue(graph.getElement(uri)));
               }
            } catch (URISyntaxException ex) {
            }
         }
         seeAlso = null;
      }
      if (seeAlso != null) {
         if (seeAlso.isLiteral()) {
            String _seeAlso = seeAlso.asLiteral().getString();
            element.setSeeAlso(new AnnotationValue.LiteralAnnotationValue(_seeAlso));
         } else if (seeAlso.isURIResource()) {
            String uriAsString = seeAlso.getURI();
            URI uri;
            try {
               uri = new URI(uriAsString);
               if (!graph.hasElement(uri)) {
                  element.setSeeAlso(new AnnotationValue.URIAnnotationValue(uri));
               } else {
                  element.setSeeAlso(new AnnotationValue.ElementAnnotationValue(graph.getElement(uri)));
               }
            } catch (URISyntaxException ex) {
            }
         } else if (seeAlso.isResource()) {
            String name = seeAlso.getLocalName();
            String namespace = seeAlso.getNameSpace();
            if (name != null && namespace != null) {
               ElementKey key = ElementKey.create(namespace, name);
               AnnotationValue value = null;
               if (resource instanceof OntClass) {
                  if (graph.hasOwlClass(key)) {
                     value = new AnnotationValue.ElementAnnotationValue(graph.getOwlClass(key));
                  }
               } else if (resource instanceof OntProperty) {
                  if (graph.hasOwlProperty(key)) {
                     value = new AnnotationValue.ElementAnnotationValue(graph.getOwlProperty(key));
                  }
               } else if (resource instanceof Individual) {
                  if (graph.hasIndividual(key)) {
                     value = new AnnotationValue.ElementAnnotationValue(graph.getIndividual(key));
                  }
               }
               if (value != null) {
                  element.setSeeAlso(value);
               }
            }
         }
      }
   }

   private void setVersionInfo(AnnotatedElement element, OntResource resource) {
      String versionInfo = resource.getVersionInfo();
      if (versionInfo != null) {
         element.setVersionInfo(versionInfo);
      }
   }

   public AnnotationValue addAnnotationValue(OntClass theClass, OwlClass owlClass, ElementKey annotationKey, Property property) {
      if (skipped.isSkipped(annotationKey)) {
         return null;
      }
      RDFNode theNode = theClass.getPropertyValue(property);
      if (theNode == null) {
         return null;
      }
      AnnotationValue value = null;
      if (theNode.isLiteral()) {
         String literal = theNode.asLiteral().getString();
         value = new AnnotationValue.LiteralAnnotationValue(literal);
      } else if (theNode.isURIResource()) {
         String uriAsString = theNode.asResource().getURI();
         try {
            URI uri = new URI(uriAsString);
            value = new AnnotationValue.URIAnnotationValue(uri);
         } catch (URISyntaxException ex) {
         }
      } else if (theNode.isResource()) {
         Resource resource = theNode.asResource();
         String name = resource.getLocalName();
         String namespace = resource.getNameSpace();
         if (name != null && namespace != null) {
            ElementKey key = ElementKey.create(namespace, name);
            if (resource instanceof OntClass) {
               if (graph.hasOwlClass(key)) {
                  value = new AnnotationValue.ElementAnnotationValue(graph.getOwlClass(key));
               }
            } else if (resource instanceof OntProperty) {
               if (graph.hasOwlProperty(key)) {
                  value = new AnnotationValue.ElementAnnotationValue(graph.getOwlProperty(key));
               }
            } else if (resource instanceof Individual) {
               if (graph.hasIndividual(key)) {
                  value = new AnnotationValue.ElementAnnotationValue(graph.getIndividual(key));
               }
            }
         }

      }
      if (value != null) {
         owlClass.addAnnotation(annotationKey, value);
      }
      return value;
   }

   public AnnotationValue addAnnotationValue(RDFNode theNode, OwlAnnotation annotation, ElementKey annotationKey) {
      if (skipped.isSkipped(annotationKey)) {
         return null;
      }
      AnnotationValue value = null;
      if (theNode.isLiteral()) {
         String literal = theNode.asLiteral().getString();
         value = new AnnotationValue.LiteralAnnotationValue(literal);
      } else if (theNode.isURIResource()) {
         String uriAsString = theNode.asResource().getURI();
         try {
            URI uri = new URI(uriAsString);
            value = new AnnotationValue.URIAnnotationValue(uri);
         } catch (URISyntaxException ex) {
         }
      }
      if (value != null) {
         annotation.addAnnotation(annotationKey, value);
      }
      return value;
   }

   public AnnotationValue addAnnotationValue(RDFNode theNode, OwlObjectProperty owlProperty, ElementKey annotationKey) {
      if (skipped.isSkipped(annotationKey)) {
         return null;
      }
      AnnotationValue value = null;
      if (theNode.isLiteral()) {
         String literal = theNode.asLiteral().getString();
         value = new AnnotationValue.LiteralAnnotationValue(literal);
      } else if (theNode.isURIResource()) {
         String uriAsString = theNode.asResource().getURI();
         try {
            URI uri = new URI(uriAsString);
            value = new AnnotationValue.URIAnnotationValue(uri);
         } catch (URISyntaxException ex) {
         }
      }
      if (value != null) {
         owlProperty.addAnnotation(annotationKey, value);
      }
      return value;
   }

   public AnnotationValue addAnnotationValue(RDFNode theNode, OwlDatatypeProperty owlProperty, ElementKey annotationKey) {
      if (skipped.isSkipped(annotationKey)) {
         return null;
      }
      AnnotationValue value = null;
      if (theNode.isLiteral()) {
         String literal = theNode.asLiteral().getString();
         value = new AnnotationValue.LiteralAnnotationValue(literal);
      } else if (theNode.isURIResource()) {
         String uriAsString = theNode.asResource().getURI();
         try {
            URI uri = new URI(uriAsString);
            value = new AnnotationValue.URIAnnotationValue(uri);
         } catch (URISyntaxException ex) {
         }
      }
      if (value != null) {
         owlProperty.addAnnotation(annotationKey, value);
      }
      return value;
   }

   public AnnotationValue addAnnotationValue(RDFNode theNode, OwlIndividual owlIndividual, ElementKey annotationKey) {
      if (skipped.isSkipped(annotationKey)) {
         return null;
      }
      AnnotationValue value = null;
      if (theNode.isLiteral()) {
         String literal = theNode.asLiteral().getString();
         value = new AnnotationValue.LiteralAnnotationValue(literal);
      } else if (theNode.isURIResource()) {
         String uriAsString = theNode.asResource().getURI();
         try {
            URI uri = new URI(uriAsString);
            value = new AnnotationValue.URIAnnotationValue(uri);
         } catch (URISyntaxException ex) {
         }
      }
      if (value != null) {
         owlIndividual.addAnnotation(annotationKey, value);
      }
      return value;
   }
}
