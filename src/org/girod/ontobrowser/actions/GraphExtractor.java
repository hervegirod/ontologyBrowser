/*
Copyright (c) 2021, 2022, 2023 Hervé Girod
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
package org.girod.ontobrowser.actions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.OntologyException;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.AnnotationValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.PropertyClassRef;
import org.girod.ontobrowser.model.restriction.OwlAllValuesFromRestriction;
import org.girod.ontobrowser.model.restriction.OwlCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlHasValueRestriction;
import org.girod.ontobrowser.model.restriction.OwlMaxCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMaxQualifiedCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMinCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMinQualifiedCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlQualifiedCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.model.restriction.OwlSomeValuesFromRestriction;
import org.girod.ontobrowser.model.restriction.UnrestrictedOwlRestriction;
import org.girod.ontobrowser.utils.DatatypeUtils;

/**
 * This class allows to extract the graph from an Owl model.
 *
 * @version 0.6
 */
public class GraphExtractor {
   private final File file;
   private final OntModel model;
   private OwlSchema graph = null;
   private ElementKey thingKey = null;
   private final boolean addThingClass;
   private final boolean showPackages;

   /**
    * Constructor.
    *
    * @param file the file
    * @param model the Owl model
    */
   public GraphExtractor(File file, OntModel model) {
      this(file, model, true, false);
   }

   public GraphExtractor(File file, OntModel model, boolean addThingClass, boolean showPackages) {
      this.file = file;
      this.model = model;
      this.showPackages = showPackages;
      if (showPackages) {
         this.addThingClass = false;
      } else {
         this.addThingClass = addThingClass;
      }
   }

   private OwlRestriction getRestrictionFrom(Restriction restriction) {
      OwlRestriction owlRestriction = null;
      Resource resource;
      if (restriction.isAllValuesFromRestriction()) {
         AllValuesFromRestriction restriction1 = restriction.asAllValuesFromRestriction();
         owlRestriction = new OwlAllValuesFromRestriction(restriction1);
      } else if (restriction.isHasValueRestriction()) {
         HasValueRestriction restriction1 = restriction.asHasValueRestriction();
         owlRestriction = new OwlHasValueRestriction(restriction1);
      } else if (restriction.isSomeValuesFromRestriction()) {
         SomeValuesFromRestriction restriction1 = restriction.asSomeValuesFromRestriction();
         owlRestriction = new OwlSomeValuesFromRestriction(restriction1);
      } else if (restriction.isMaxCardinalityRestriction()) {
         MaxCardinalityRestriction restriction1 = restriction.asMaxCardinalityRestriction();
         owlRestriction = new OwlMaxCardinalityRestriction(restriction1);
      } else if (restriction.isMinCardinalityRestriction()) {
         MinCardinalityRestriction restriction1 = restriction.asMinCardinalityRestriction();
         owlRestriction = new OwlMinCardinalityRestriction(restriction1);
      } else if (restriction.isCardinalityRestriction()) {
         CardinalityRestriction restriction1 = restriction.asCardinalityRestriction();
         owlRestriction = new OwlCardinalityRestriction(restriction1);
      } else {
         // see https://stackoverflow.com/questions/20562107/how-to-add-qualified-cardinality-in-jena
         // see http://mail-archives.apache.org/mod_mbox/jena-users/201303.mbox/%3CCA+Q4Jn=bDM2wiPSh4DHj58hzxR_oWx2jJoQxnroFScac=E7t3Q@mail.gmail.com%3E
         RDFNode node = restriction.getPropertyValue(OWL2.onClass);
         resource = node.asResource();
         if (restriction.hasProperty(OWL2.maxQualifiedCardinality)) {
            owlRestriction = new OwlMaxQualifiedCardinalityRestriction(restriction, resource);
         } else if (restriction.hasProperty(OWL2.minQualifiedCardinality)) {
            owlRestriction = new OwlMinQualifiedCardinalityRestriction(restriction, resource);
         } else if (restriction.hasProperty(OWL2.qualifiedCardinality)) {
            owlRestriction = new OwlQualifiedCardinalityRestriction(restriction, resource);
         }

      }
      return owlRestriction;
   }

   private void setIsDefinedBy(NamedOwlElement element, OntResource resource) {
      Resource isDefinedBy = resource.getIsDefinedBy();
      if (isDefinedBy != null) {
         if (isDefinedBy.isLiteral()) {
            String _definedBy = isDefinedBy.asLiteral().getString();
            element.setIsDefinedBy(new AnnotationValue.LiteralAnnotationValue(_definedBy));
         } else if (isDefinedBy.isURIResource()) {
            String uriAsString = isDefinedBy.getURI();
            try {
               URI uri = new URI(uriAsString);
               element.setIsDefinedBy(new AnnotationValue.URIAnnotationValue(uri));
            } catch (URISyntaxException ex) {
            }
         }
      }
   }

   private void setSeeAlso(NamedOwlElement element, OntResource resource) {
      Resource seeAlso;
      try {
         seeAlso = resource.getSeeAlso();
      } catch (OntologyException e) {
         seeAlso = null;
      }
      if (seeAlso != null) {
         if (seeAlso.isLiteral()) {
            String _seeAlso = seeAlso.asLiteral().getString();
            element.setSeeAlso(new AnnotationValue.LiteralAnnotationValue(_seeAlso));
         } else if (seeAlso.isURIResource()) {
            String uriAsString = seeAlso.getURI();
            try {
               URI uri = new URI(uriAsString);
               element.setIsDefinedBy(new AnnotationValue.URIAnnotationValue(uri));
            } catch (URISyntaxException ex) {
            }
         }
      }
   }

   private AnnotationValue addAnnotationValue(OntClass theClass, OwlClass owlClass, ElementKey annotationKey, OntProperty property) {
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
      }
      if (value != null) {
         owlClass.addAnnotation(annotationKey, value);
      }
      return value;
   }
   
   private AnnotationValue addAnnotationValue(RDFNode theNode, OwlObjectProperty owlProperty, ElementKey annotationKey) {
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
   
   private AnnotationValue addAnnotationValue(RDFNode theNode, OwlDatatypeProperty owlProperty, ElementKey annotationKey) {
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
   
   private AnnotationValue addAnnotationValue(RDFNode theNode, OwlIndividual owlIndividual, ElementKey annotationKey) {
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

   private void setVersionInfo(NamedOwlElement element, OntResource resource) {
      String versionInfo = resource.getVersionInfo();
      if (versionInfo != null) {
         element.setVersionInfo(versionInfo);
      }
   }

   private void setDefaultAnnotations(NamedOwlElement element, OntResource resource) {
      String comment = getComments(resource);
      element.setComments(comment);
      setIsDefinedBy(element, resource);
      setVersionInfo(element, resource);
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

   private void addToPropertyToClassMap(Map<ElementKey, Set<ElementKey>> classToProperties, ElementKey propertyKey, ElementKey classKey) {
      Set<ElementKey> set;
      if (classToProperties.containsKey(classKey)) {
         set = classToProperties.get(classKey);
      } else {
         set = new HashSet<>();
         classToProperties.put(classKey, set);
      }
      set.add(propertyKey);
   }

   private OwlRestriction getOwlRestriction(OntClass clazz) {
      String localName = clazz.getLocalName();
      OwlRestriction owlRestriction;
      if (localName == null) {
         if (clazz.isRestriction()) {
            Restriction restriction = clazz.asRestriction();
            owlRestriction = getRestrictionFrom(restriction);
         } else {
            // the class may not be a restriction, in that case omit it
            return null;
         }
      } else {
         owlRestriction = new UnrestrictedOwlRestriction(clazz);
      }
      return owlRestriction;
   }

   /**
    * Return the list of Owl classes which have an existing individual. There may be more than one of course. The getOntClass() method from
    * Jena is not what we look for here because we will only have the first one in that case.
    *
    * @param individual the Individual
    * @return the list of Owl classes which have the specified individual
    */
   private List<OntClass> getOwlClasses(Individual individual) {
      List<OntClass> list = new ArrayList<>();
      ExtendedIterator<Resource> it = individual.listRDFTypes(true);
      while (it.hasNext()) {
         Resource res = it.next();
         try {
            OntClass theClass = res.as(OntClass.class);
            list.add(theClass);
         } catch (ConversionException e) {
            // this can happen if the Resource is not a Class
         }
      }
      return list;
   }

   /**
    * Return the graph.
    *
    * @return the graph
    */
   public OwlSchema getGraph() throws OntologyException {
      graph = new OwlSchema(model);
      OntClass thingClass = model.getOntClass("http://www.w3.org/2002/07/owl#Thing");
      OwlClass owlThingClass = graph.getThingClass();
      thingKey = owlThingClass.getKey();
      List<OwlRestriction> restrictions = new ArrayList<>();

      // see https://stackoverflow.com/questions/17296209/jena-ontology-api-how-to-retrieve-axiom-that-attach-annotation-to-a-class-proper
      // Find the axioms in the model.  For each axiom, iterate through the
      // its properties, looking for those that are *not* used for encoding the
      // annotated axiom.  Those that are left are the annotations.
      Map<ElementKey, Set<ElementKey>> rangeClassToProperties = new HashMap<>();
      Map<ElementKey, Set<ElementKey>> domainClassToProperties = new HashMap<>();

      // list annotations
      ExtendedIterator<AnnotationProperty> annotations = model.listAnnotationProperties();
      while (annotations.hasNext()) {
         AnnotationProperty annProperty = annotations.next();
         OwlAnnotation owlAnnotation = new OwlAnnotation(annProperty);
         graph.addAnnotation(owlAnnotation);
      }

      // list properties
      Set<ElementKey> annotationkeys = new HashSet<>();
      Map<ElementKey, Set<ElementKey>> equivalentProperties = new HashMap<>();
      ExtendedIterator properties = model.listAllOntProperties();
      while (properties.hasNext()) {
         OntProperty thisProperty = (OntProperty) properties.next();
         String nameSpace = thisProperty.getNameSpace();
         OwlProperty owlProp = null;
         if (thisProperty.isObjectProperty()) {
            ObjectProperty objProperty = (ObjectProperty) thisProperty;
            OwlObjectProperty owlProperty = new OwlObjectProperty(objProperty, nameSpace, thisProperty.getLocalName());
            setDefaultAnnotations(owlProperty, thisProperty);
            addEquivalentProperties(equivalentProperties, objProperty, owlProperty.getKey());
            owlProp = owlProperty;
            graph.addOwlProperty(owlProperty);
            ExtendedIterator declDomain = objProperty.listDomain();
            if (!declDomain.hasNext()) {
               addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), thingKey);
            } else {
               while (declDomain.hasNext()) {
                  OntClass thisClass = (OntClass) declDomain.next();
                  if (thisClass.isUnionClass()) {
                     UnionClass unionClass = thisClass.asUnionClass();
                     ExtendedIterator<? extends OntClass> it = unionClass.listOperands();
                     while (it.hasNext()) {
                        OntClass theClass = it.next();
                        OwlRestriction restriction = getOwlRestriction(theClass);
                        if (restriction != null) {
                           ElementKey domainKey = restriction.getKey();
                           owlProperty.addDomain(restriction);
                           restrictions.add(restriction);
                           addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), domainKey);
                        }
                     }
                  } else {
                     OwlRestriction restriction = getOwlRestriction(thisClass);
                     if (restriction != null) {
                        ElementKey domainKey = restriction.getKey();
                        owlProperty.addDomain(restriction);
                        restrictions.add(restriction);
                        addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), domainKey);
                     }
                  }
               }
            }
            ExtendedIterator declRange = objProperty.listRange();
            if (!declRange.hasNext()) {
               addToPropertyToClassMap(rangeClassToProperties, owlProperty.getKey(), thingKey);
            } else {
               while (declRange.hasNext()) {
                  OntClass thisClass = (OntClass) declRange.next();
                  if (thisClass.isUnionClass()) {
                     UnionClass unionClass = thisClass.asUnionClass();
                     ExtendedIterator<? extends OntClass> it = unionClass.listOperands();
                     while (it.hasNext()) {
                        OntClass theClass = it.next();
                        OwlRestriction restriction = getOwlRestriction(theClass);
                        if (restriction != null) {
                           ElementKey domainKey = restriction.getKey();
                           owlProperty.addRange(restriction);
                           restrictions.add(restriction);
                           addToPropertyToClassMap(rangeClassToProperties, owlProperty.getKey(), domainKey);
                        }
                     }
                  } else {
                     OwlRestriction restriction = getOwlRestriction(thisClass);
                     if (restriction != null) {
                        ElementKey rangeKey = restriction.getKey();
                        owlProperty.addRange(restriction);
                        restrictions.add(restriction);
                        addToPropertyToClassMap(rangeClassToProperties, owlProperty.getKey(), rangeKey);
                     }
                  }
               }
            }
         } else if (thisProperty.isDatatypeProperty()) {
            DatatypeProperty datatypeProperty = (DatatypeProperty) thisProperty;
            OwlDatatypeProperty owlProperty = new OwlDatatypeProperty(datatypeProperty, nameSpace, thisProperty.getLocalName());
            setDefaultAnnotations(owlProperty, thisProperty);
            addEquivalentProperties(equivalentProperties, datatypeProperty, owlProperty.getKey());
            ExtendedIterator declDomain = datatypeProperty.listDomain();
            if (!declDomain.hasNext()) {
               addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), thingKey);
            } else {
               while (declDomain.hasNext()) {
                  OntClass thisClass = (OntClass) declDomain.next();
                  if (thisClass.isUnionClass()) {
                     UnionClass unionClass = thisClass.asUnionClass();
                     ExtendedIterator<? extends OntClass> it = unionClass.listOperands();
                     while (it.hasNext()) {
                        OntClass theClass = it.next();
                        OwlRestriction restriction = getOwlRestriction(theClass);
                        if (restriction != null) {
                           ElementKey domainKey = restriction.getKey();
                           owlProperty.addDomain(restriction);
                           restrictions.add(restriction);
                           addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), domainKey);
                        }
                     }
                  } else {
                     OwlRestriction restriction = getOwlRestriction(thisClass);
                     if (restriction != null) {
                        ElementKey domainKey = restriction.getKey();
                        owlProperty.addDomain(restriction);
                        restrictions.add(restriction);
                        addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), domainKey);
                     }
                  }
               }
            }
            owlProp = owlProperty;
            ExtendedIterator<? extends OntResource> resources = datatypeProperty.listRange();
            while (resources.hasNext()) {
               OntResource resource = resources.next();
               if (resource.isURIResource()) {
                  ElementKey tkey = new ElementKey(resource.getNameSpace(), resource.getLocalName());
                  OwlDatatype dtype = new OwlDatatype(tkey);
                  owlProperty.addType(dtype);
               }
            }
            graph.addOwlProperty(owlProperty);
         } else if (thisProperty.isResource()) {
            Resource resource = thisProperty.asResource();
            annotationkeys.add(ElementKey.create(thisProperty.getNameSpace(), thisProperty.getLocalName()));
            graph.addAnnotation(new OwlAnnotation(resource));
         }
         if (owlProp != null) {
            getRestrictions(thisProperty, owlProp);
         }
      }

      boolean hasThingClass = false;
      Map<ElementKey, Set<ElementKey>> equivalentClasses = new HashMap<>();
      // list classes
      ExtendedIterator classes = model.listClasses();
      while (classes.hasNext()) {
         OntClass thisClass = (OntClass) classes.next();
         if (thisClass.getNameSpace() == null && thisClass.getLocalName() == null) {
            continue;
         }
         OwlClass owlClass;
         if (thisClass.equals(thingClass)) {
            if (addThingClass) {
               owlClass = owlThingClass;
            } else {
               owlClass = null;
            }
            hasThingClass = true;
         } else {
            String nameSpace = thisClass.getNameSpace();
            owlClass = new OwlClass(nameSpace, thisClass.getLocalName());
            ExtendedIterator<OntProperty> declProp = thisClass.listDeclaredProperties();
            while (declProp.hasNext()) {
               OntProperty prop = declProp.next();
               ElementKey theKey = ElementKey.create(prop.getNameSpace(), prop.getLocalName());
               if (annotationkeys.contains(theKey)) {
                  addAnnotationValue(thisClass, owlClass, theKey, prop);
               }
            }            
            setDefaultAnnotations(owlClass, thisClass);
         }
         if (owlClass != null) {
            addEquivalentClasses(equivalentClasses, thisClass, owlClass.getKey());
            graph.addOwlClass(owlClass);
         }
      }
      if (!hasThingClass && addThingClass) {
         graph.addOwlClass(owlThingClass);
      }
      fillEquivalentClasses(equivalentClasses);
      fillEquivalentProperties(equivalentProperties);
      
      Iterator<OwlProperty> it2 = graph.getOwlProperties().values().iterator();
      while (it2.hasNext()) {
         OwlProperty owlProperty = it2.next();
         if (owlProperty instanceof OwlObjectProperty) {
            OwlObjectProperty _owlProperty = (OwlObjectProperty)owlProperty;
            ObjectProperty objproperty = _owlProperty.getProperty();        
            StmtIterator iterSmt = objproperty.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               Property predicate = statement.getPredicate();
               RDFNode node = statement.getObject();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               if (annotationkeys.contains(theKey)) {
                  addAnnotationValue(node, _owlProperty, theKey);
               }               
            }
         } else if (owlProperty instanceof OwlDatatypeProperty) {
            OwlDatatypeProperty _owlProperty = (OwlDatatypeProperty)owlProperty;
            DatatypeProperty datatypeProperty = _owlProperty.getProperty(); 
            StmtIterator iterSmt = datatypeProperty.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               RDFNode node = statement.getObject();
               Property predicate = statement.getPredicate();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               if (annotationkeys.contains(theKey)) {
                  addAnnotationValue(node, _owlProperty, theKey);
               }               
            }            
         }          
      }

      // list individuals
      if (BrowserConfiguration.getInstance().includeIndividuals) {
         ExtendedIterator individuals = model.listIndividuals();
         while (individuals.hasNext()) {
            Individual thisIndividual = (Individual) individuals.next();
            List<OntClass> theClasses = getOwlClasses(thisIndividual);
            if (!theClasses.isEmpty()) {
               Iterator<OntClass> it = theClasses.iterator();
               Map<ElementKey, OwlClass> parentClasses = new HashMap<>();
               while (it.hasNext()) {
                  OntClass theClass = it.next();
                  ElementKey theKey = new ElementKey(theClass.getNameSpace(), theClass.getLocalName());
                  if (graph.hasOwlClass(theKey)) {
                     OwlClass theOwlClass = graph.getOwlClass(theKey);
                     parentClasses.put(theKey, theOwlClass);
                  }
               }
               OwlIndividual owlIndividual = new OwlIndividual(parentClasses, thisIndividual);  
            StmtIterator iterSmt = thisIndividual.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               RDFNode node = statement.getObject();
               Property predicate = statement.getPredicate();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               if (annotationkeys.contains(theKey)) {
                  addAnnotationValue(node, owlIndividual, theKey);
               }               
            }                
               setDefaultAnnotations(owlIndividual, thisIndividual);
               graph.addIndividual(owlIndividual);
            }
         }
      }

      hasThingClass = false;
      // parent classes
      classes = model.listClasses();
      while (classes.hasNext()) {
         OntClass thisClass = (OntClass) classes.next();
         if (thisClass.getNameSpace() == null && thisClass.getLocalName() == null) {
            continue;
         }
         ElementKey key = new ElementKey(thisClass.getNameSpace(), thisClass.getLocalName());
         if (graph.hasOwlClass(key)) {
            OwlClass owlClass = graph.getOwlClass(key);
            boolean isEmpty = true;
            ExtendedIterator<OntClass> parents = thisClass.listSuperClasses();
            while (parents.hasNext()) {
               OntClass superClass = parents.next();
               isEmpty = false;
               ElementKey skey = new ElementKey(superClass.getNameSpace(), superClass.getLocalName());
               if (graph.hasOwlClass(skey)) {
                  OwlClass superOwlClass = graph.getOwlClass(skey);
                  if (addThingClass || !skey.equals(thingKey)) {
                     owlClass.addSuperClass(skey, superOwlClass, thingKey);
                     superOwlClass.addSubClass(key, owlClass);
                  }
               }
            }
            // add root classes as children of Thing
            if (addThingClass && isEmpty && !key.equals(thingKey)) {
               owlThingClass.addSubClass(key, owlClass);
               owlClass.addSuperClass(thingKey, owlThingClass, thingKey);
            }
         }
      }
      if (!hasThingClass && addThingClass) {
         ExtendedIterator<OntClass> children = thingClass.listSubClasses();
         while (children.hasNext()) {
            OntClass childClass = children.next();
            ElementKey skey = new ElementKey(childClass.getNameSpace(), childClass.getLocalName());
            if (graph.hasOwlClass(skey)) {
               OwlClass childOwlClass = graph.getOwlClass(skey);
               childOwlClass.addSuperClass(thingKey, owlThingClass, thingKey);
               owlThingClass.addSubClass(skey, childOwlClass);
            }
         }
      }

      // class to properties dependencies
      addDependencies(graph, restrictions, domainClassToProperties, rangeClassToProperties);

      // setup restrictions
      Iterator<OwlRestriction> itr = restrictions.iterator();
      while (itr.hasNext()) {
         OwlRestriction restriction = itr.next();
         restriction.setup(graph);
      }

      if (showPackages) {
         PackagesExtractor pExtractor = new PackagesExtractor(graph);
         Map<ElementKey, OwlClass> packages = pExtractor.extractPackages();
         graph.setPackages(packages);
      }

      return graph;
   }

   private void fillEquivalentClasses(Map<ElementKey, Set<ElementKey>> equivalentKeys) {
      Iterator<Entry<ElementKey, Set<ElementKey>>> it = equivalentKeys.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, Set<ElementKey>> entry = it.next();
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

   private void fillEquivalentProperties(Map<ElementKey, Set<ElementKey>> equivalentKeys) {
      Iterator<Entry<ElementKey, Set<ElementKey>>> it = equivalentKeys.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, Set<ElementKey>> entry = it.next();
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

   private void addEquivalentProperties(Map<ElementKey, Set<ElementKey>> equivalentProperties, OntProperty thisProperty, ElementKey key) {
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
         if (theProperty.getNameSpace() == null && theProperty.getLocalName() == null) {
            continue;
         }
         ElementKey otherKey = new ElementKey(theProperty.getNameSpace(), theProperty.getLocalName());
         set.add(otherKey);
      }
   }

   private void addEquivalentClasses(Map<ElementKey, Set<ElementKey>> equivalentClasses, OntClass thisClass, ElementKey key) {
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
         if (thisClass.getNameSpace() == null && thisClass.getLocalName() == null) {
            continue;
         }
         ElementKey otherKey = new ElementKey(theClass.getNameSpace(), theClass.getLocalName());
         set.add(otherKey);
      }
   }

   private void addDependencies(OwlSchema graph, List<OwlRestriction> restrictions, Map<ElementKey, Set<ElementKey>> domainClassToProperties,
      Map<ElementKey, Set<ElementKey>> rangeClassToProperties) {
      Map<ElementKey, OwlProperty> modelProps = graph.getOwlProperties();
      Map<ElementKey, OwlClass> classes = graph.getOwlClasses();

      Iterator<ElementKey> it = classes.keySet().iterator();
      while (it.hasNext()) {
         ElementKey classKey = it.next();
         OwlClass theClass = classes.get(classKey);
         if (domainClassToProperties.containsKey(classKey)) {
            Set<ElementKey> properties = domainClassToProperties.get(classKey);
            Iterator<ElementKey> it2 = properties.iterator();
            while (it2.hasNext()) {
               ElementKey propKey = it2.next();
               OwlProperty property = modelProps.get(propKey);
               theClass.addOwlProperty(graph, property);
               UnrestrictedOwlRestriction restriction = property.addDomain(classKey);
               restrictions.add(restriction);
            }
         }
         if (rangeClassToProperties.containsKey(classKey)) {
            Set<ElementKey> properties = rangeClassToProperties.get(classKey);
            Iterator<ElementKey> it2 = properties.iterator();
            while (it2.hasNext()) {
               ElementKey propKey = it2.next();
               OwlProperty property = modelProps.get(propKey);
               if (property instanceof OwlObjectProperty) {
                  UnrestrictedOwlRestriction restriction = ((OwlObjectProperty) property).addRange(classKey);
                  restrictions.add(restriction);
               }
            }
         }
      }

      Iterator<OwlProperty> it2 = modelProps.values().iterator();
      while (it2.hasNext()) {
         OwlProperty property = it2.next();
         if (property.isObjectProperty()) {
            ElementKey propKey = property.getKey();
            OwlObjectProperty objectProp = (OwlObjectProperty) property;
            OntProperty ontProperty = objectProp.getProperty();
            if (ontProperty.hasInverse()) {
               ontProperty = ontProperty.getInverse();
               ElementKey inverseKey = new ElementKey(ontProperty.getNameSpace(), ontProperty.getLocalName());
               if (modelProps.containsKey(inverseKey)) {
                  objectProp.setInverseProperty((OwlObjectProperty) modelProps.get(inverseKey));
               }
            }
            List<PropertyClassRef> listRangeRef = new ArrayList<>();
            List<PropertyClassRef> listDomainRef = new ArrayList<>();
            it = objectProp.getDomain().keySet().iterator();
            while (it.hasNext()) {
               ElementKey classKey = it.next();
               listDomainRef.add(new PropertyClassRef(classKey, propKey));
            }
            it = objectProp.getRange().keySet().iterator();
            while (it.hasNext()) {
               ElementKey classKey = it.next();
               listRangeRef.add(new PropertyClassRef(classKey, propKey));
            }
            it = objectProp.getDomain().keySet().iterator();
            while (it.hasNext()) {
               ElementKey classKey = it.next();
               OwlClass theClass = classes.get(classKey);
               Iterator<PropertyClassRef> it3 = listRangeRef.iterator();
               while (it3.hasNext()) {
                  theClass.addFromDomain(it3.next());
               }
            }
            it = objectProp.getRange().keySet().iterator();
            while (it.hasNext()) {
               ElementKey classKey = it.next();
               OwlClass theClass = classes.get(classKey);
               Iterator<PropertyClassRef> it3 = listDomainRef.iterator();
               while (it3.hasNext()) {
                  theClass.addToRange(it3.next());
               }
            }
         }
      }
   }

   private void getRestrictions(OntProperty property, OwlProperty owlProperty) {
      ExtendedIterator restrictions = property.listReferringRestrictions();
      while (restrictions.hasNext()) {
         Restriction restriction = (Restriction) restrictions.next();
         WrappedValue value = getPropertyValueAsInt(restriction, OWL.maxCardinality);
         if (value.exist()) {
            owlProperty.setMaxCardinality(value.valueAsInt());
         }
         value = getPropertyValueAsInt(restriction, OWL2.maxQualifiedCardinality);
         if (value.exist()) {
            owlProperty.setMaxCardinality(value.valueAsInt());
         }
         value = getPropertyValueAsInt(restriction, OWL.minCardinality);
         if (value.exist()) {
            owlProperty.setMinCardinality(value.valueAsInt());
         }
         value = getPropertyValueAsInt(restriction, OWL2.minQualifiedCardinality);
         if (value.exist()) {
            owlProperty.setMinCardinality(value.valueAsInt());
         }
         value = getPropertyValueAsInt(restriction, OWL.cardinality);
         if (value.exist()) {
            owlProperty.setMinCardinality(value.valueAsInt());
            owlProperty.setMaxCardinality(value.valueAsInt());
         }
         value = getPropertyValueAsInt(restriction, OWL2.qualifiedCardinality);
         if (value.exist()) {
            owlProperty.setMinCardinality(value.valueAsInt());
            owlProperty.setMaxCardinality(value.valueAsInt());
         }
      }
   }

   private WrappedValue getPropertyValueAsInt(Restriction restriction, Property property) {
      if (restriction.hasProperty(property)) {
         RDFNode node = restriction.getPropertyValue(property);
         if (node instanceof Literal) {
            Literal literal = (Literal) node;
            try {
               int i = literal.getInt();
               return new WrappedValue(i);
            } catch (DatatypeFormatException e) {
               int i = DatatypeUtils.getValueAsInt(literal);
               return new WrappedValue(i);
            }
         } else {
            return new WrappedValue();
         }
      } else {
         return new WrappedValue();
      }
   }

   private static class WrappedValue {
      private final Object value;
      private final boolean exist;

      private WrappedValue(Object value) {
         this.value = value;
         this.exist = true;
      }

      private WrappedValue() {
         this.value = null;
         this.exist = false;
      }

      private boolean exist() {
         return exist;
      }

      private int valueAsInt() {
         if (value instanceof Integer) {
            return (Integer) value;
         } else {
            return -1;
         }
      }
   }
}
