/*
Copyright (c) 2021, 2022, 2023, 2024 Hervé Girod
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

import java.io.File;
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
import org.girod.ontobrowser.actions.AbstractWarningAction;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlAutoIndividual;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlDeclaredIndividual;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlRepresentationType;
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
 * @version 0.15
 */
public class GraphExtractor extends AbstractWarningAction {
   private static final ElementKey TYPE_NS = ElementKey.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
   private static final ElementKey SEE_ALSO_NS = ElementKey.create("http://www.w3.org/2000/01/rdf-schema#", "seeAlso");
   private static final ElementKey DESCRIPTION_NS = ElementKey.create("http://purl.org/dc/terms/#", "Description");
   private final File file;
   private final OntModel model;
   private OwlSchema graph = null;
   private ElementKey thingKey = null;
   private final boolean addThingClass;
   private final boolean showPackages;
   private ExpressionsHelper exprHelper = null;
   private IndividualsHelper individualsHelper = null;
   private AnnotationsHelper annotationsHelper = null;
   private boolean lookForDefaultNamespace = false;
   private String namespaceFromFile;
   private String defaultNamespace = null;
   private String defaultSquashedNamespace = null;
   private short representationType = OwlRepresentationType.TYPE_OWL_XML;

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

   /**
    * Set the representation type.
    *
    * @param representationType the representation type
    */
   public void setRepresentationType(short representationType) {
      this.representationType = representationType;
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
         owlRestriction = new UnrestrictedOwlRestriction(clazz, graph.getNamespace(clazz));
      }
      return owlRestriction;
   }

   /**
    * Return the list of Owl classes which have an existing individual. There may be more than one of course. The getOntClass() method from Jena is not what we
    * look for here because we will only have the first one in that case.
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
            this.addError(e);
         }
      }
      return list;
   }

   public AnnotationsHelper getAnnotationsHelper() {
      return annotationsHelper;
   }

   private OwlIndividual createOwlIndividual(Map<ElementKey, OwlClass> parentClasses, Individual individual) {
      OwlIndividual owlIndividual;
      if (defaultNamespace == null) {
         owlIndividual = new OwlDeclaredIndividual(parentClasses, individual);
      } else {
         String namespace = individual.getNameSpace();
         if (namespace.startsWith(namespaceFromFile)) {
            owlIndividual = new OwlDeclaredIndividual(parentClasses, individual, defaultNamespace);
         } else {
            owlIndividual = new OwlDeclaredIndividual(parentClasses, individual);
         }
      }
      setPrefix(owlIndividual);
      return owlIndividual;
   }

   private OwlClass createOwlClass(OntClass ontClass) {
      if (defaultNamespace == null) {
         return new OwlClass(ontClass);
      } else {
         String namespace = ontClass.getNameSpace();
         if (namespace.startsWith(namespaceFromFile)) {
            return new OwlClass(ontClass, defaultNamespace);
         } else {
            return new OwlClass(ontClass);
         }
      }
   }

   private boolean isDefinedKey(Resource resource, ElementKey key) {
      ElementKey theKey = ElementKey.create(resource.getNameSpace(), resource.getLocalName());
      return theKey.equals(key);
   }

   /**
    * Return the graph.
    *
    * @return the graph
    */
   public OwlSchema getGraph() throws OntologyException {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      graph = new OwlSchema(model, representationType, file);
      namespaceFromFile = graph.getPotentialNamespaceFromFile();
      defaultNamespace = graph.getDefaultNamespace();
      defaultSquashedNamespace = graph.getDefaultSquashedNamespace();
      graph.setIncludeIndividuals(conf.includeIndividuals);
      annotationsHelper = new AnnotationsHelper(graph, this);
      exprHelper = new ExpressionsHelper(this, graph);
      individualsHelper = new IndividualsHelper(graph);
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
      annotationsHelper.addSchemaAnnotations();

      // list properties
      Map<ElementKey, Set<ElementKey>> equivalentProperties = new HashMap<>();
      ExtendedIterator properties = model.listAllOntProperties();
      while (properties.hasNext()) {
         OntProperty thisProperty = (OntProperty) properties.next();
         String nameSpace = graph.getNamespace(thisProperty);
         OwlProperty owlProp = null;
         if (thisProperty.isObjectProperty()) {
            owlProp = addObjectProperty(thisProperty, nameSpace, equivalentProperties, domainClassToProperties, rangeClassToProperties, restrictions);
         } else if (thisProperty.isDatatypeProperty()) {
            owlProp = addDatatypeProperty(thisProperty, nameSpace, equivalentProperties, domainClassToProperties, restrictions);
         } else if (thisProperty.isResource()) {
            Resource resource = thisProperty.asResource();
            OwlAnnotation annotation = new OwlAnnotation(resource);
            graph.addElementAnnotation(annotation);
            StmtIterator iterSmt = resource.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               Property predicate = statement.getPredicate();
               RDFNode node = statement.getObject();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               annotationsHelper.addAnnotationValue(node, annotation, theKey);
            }
         }
         if (owlProp != null) {
            getRestrictions(thisProperty, owlProp);
         }
      }

      lookForDefaultNamespace = !graph.hasDefaultNamespace();
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
            owlClass = createOwlClass(thisClass);
            setPrefix(owlClass);

            StmtIterator stmt = thisClass.listProperties();
            while (stmt.hasNext()) {
               Statement statement = stmt.next();
               Property prop = statement.getPredicate();
               ElementKey theKey = ElementKey.create(prop.getNameSpace(), prop.getLocalName());
               OwlAnnotation annotation = graph.getOrCreateAnnotation(theKey);
               annotationsHelper.addAnnotationValue(thisClass, owlClass, annotation, prop);
            }
         }
         if (owlClass != null) {
            graph.addOwlClass(owlClass);
         }
      }
      if (!hasThingClass && addThingClass) {
         graph.addOwlClass(owlThingClass);
      }

      Iterator<OwlProperty> it2 = graph.getOwlProperties().values().iterator();
      while (it2.hasNext()) {
         OwlProperty owlProperty = it2.next();
         if (owlProperty instanceof OwlObjectProperty) {
            OwlObjectProperty _owlProperty = (OwlObjectProperty) owlProperty;
            ObjectProperty objproperty = _owlProperty.getProperty();
            StmtIterator iterSmt = objproperty.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               Property predicate = statement.getPredicate();
               RDFNode node = statement.getObject();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               OwlAnnotation annotation = graph.getOrCreateAnnotation(theKey);
               annotationsHelper.addAnnotationValue(node, _owlProperty, annotation);
            }
         } else if (owlProperty instanceof OwlDatatypeProperty) {
            OwlDatatypeProperty _owlProperty = (OwlDatatypeProperty) owlProperty;
            DatatypeProperty datatypeProperty = _owlProperty.getProperty();
            StmtIterator iterSmt = datatypeProperty.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               RDFNode node = statement.getObject();
               Property predicate = statement.getPredicate();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               OwlAnnotation annotation = graph.getOrCreateAnnotation(theKey);
               annotationsHelper.addAnnotationValue(node, _owlProperty, annotation);
            }
         }
      }

      // list individuals
      if (conf.includeIndividuals) {
         try {
            ExtendedIterator individuals = model.listIndividuals();
            while (individuals.hasNext()) {
               Individual thisIndividual = (Individual) individuals.next();
               addIndividual(thisIndividual);
            }

            Iterator<OwlClass> itc = graph.getOwlClasses().values().iterator();
            while (itc.hasNext()) {
               OwlClass owlClass = itc.next();
               OntClass theClass = owlClass.getOntClass();
               if (theClass != null) {
                  individuals = model.listIndividuals(theClass);
                  while (individuals.hasNext()) {
                     Individual thisIndividual = (Individual) individuals.next();
                     addIndividual(thisIndividual);
                  }
               }
            }

            StmtIterator stmts = model.listStatements();
            while (stmts.hasNext()) {
               Statement stmt = stmts.next();
               RDFNode object = stmt.getObject();
               RDFNode subject = stmt.getSubject();
               if (subject.isURIResource() && object.isURIResource()) {
                  Property p = stmt.getPredicate();
                  if (isDefinedKey(p, TYPE_NS)) {
                     Resource objectResource = object.asResource();
                     String namespace = graph.getNamespace(objectResource);
                     ElementKey objectKey = ElementKey.create(namespace, objectResource.getLocalName());
                     Resource subjectResource = subject.asResource();
                     String namespace2 = graph.getNamespace(subjectResource);
                     ElementKey subjectKey = ElementKey.create(namespace2, subjectResource.getLocalName());
                     if (graph.hasOwlClass(objectKey) && !graph.hasIndividual(subjectKey)) {
                        OwlClass owlClass = graph.getOwlClass(objectKey);
                        OwlIndividual individual = new OwlAutoIndividual(owlClass, subjectResource, namespace2);
                        graph.addIndividual(individual);
                     }
                  }
               }
            }

         } catch (ConversionException ex) {
            this.addError(ex);
         }
      }

      hasThingClass = false;
      // parent classes
      classes = model.listClasses();
      while (classes.hasNext()) {
         OntClass thisClass = (OntClass) classes.next();
         addClassToGraph(thisClass, owlThingClass);
      }
      if (!hasThingClass && addThingClass) {
         ExtendedIterator<OntClass> children = thingClass.listSubClasses();
         while (children.hasNext()) {
            OntClass childClass = children.next();
            String childNamespace = graph.getNamespace(childClass);
            ElementKey skey = new ElementKey(childNamespace, childClass.getLocalName());
            if (graph.hasOwlClass(skey)) {
               OwlClass childOwlClass = graph.getOwlClass(skey);
               childOwlClass.addSuperClass(thingKey, owlThingClass, thingKey);
               owlThingClass.addSubClass(skey, childOwlClass);
            }
         }
      }

      // parent and sub-properties
      properties = model.listAllOntProperties();
      while (properties.hasNext()) {
         OntProperty thisProperty = (OntProperty) properties.next();
         if (thisProperty.getNameSpace() == null && thisProperty.getLocalName() == null) {
            continue;
         }
         ElementKey key = new ElementKey(thisProperty.getNameSpace(), thisProperty.getLocalName());
         if (graph.hasOwlProperty(key)) {
            OwlProperty owlProperty = graph.getOwlProperty(key);
            ExtendedIterator<? extends OntProperty> parents = thisProperty.listSuperProperties(true);
            while (parents.hasNext()) {
               OntProperty superProperty = parents.next();
               ElementKey skey = new ElementKey(superProperty.getNameSpace(), superProperty.getLocalName());
               if (graph.hasOwlProperty(skey)) {
                  OwlProperty superOwlProperty = graph.getOwlProperty(skey);
                  owlProperty.addSuperProperty(skey, superOwlProperty);
               }
            }
            ExtendedIterator<? extends OntProperty> children = thisProperty.listSubProperties(true);
            while (children.hasNext()) {
               OntProperty subProperty = children.next();
               ElementKey skey = new ElementKey(subProperty.getNameSpace(), subProperty.getLocalName());
               if (!skey.equals(key) && graph.hasOwlProperty(skey)) {
                  OwlProperty superOwlProperty = graph.getOwlProperty(skey);
                  owlProperty.addSubProperty(skey, superOwlProperty);
               }
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

      // equivalent classes
      Iterator<OwlClass> ite = graph.getOwlClasses().values().iterator();
      while (ite.hasNext()) {
         OwlClass owlClass = ite.next();
         OntClass thisClass = owlClass.getOntClass();
         if (thisClass != null) {
            exprHelper.addEquivalentClasses(equivalentClasses, thisClass, owlClass);
         }
      }

      // add individuals property values
      if (conf.includeIndividuals) {
         Iterator<OwlIndividual> it = graph.getIndividuals().values().iterator();
         while (it.hasNext()) {
            OwlIndividual owlIndividual = it.next();
            individualsHelper.addIndividualProperties(owlIndividual);
         }
      }

      exprHelper.fillEquivalentClasses(equivalentClasses);
      exprHelper.fillEquivalentProperties(equivalentProperties);
      annotationsHelper.addDefaultAnnotations();

      if (showPackages) {
         PackagesExtractor pExtractor = new PackagesExtractor(graph);
         Map<ElementKey, OwlClass> packages = pExtractor.extractPackages();
         graph.setPackages(packages);
      }
      lookForForeignElements();

      return graph;
   }

   private void addClassToGraph(OntClass thisClass, OwlClass owlThingClass) {
      if (thisClass.getLocalName() == null) {
         return;
      }
      String classNamespace = graph.getNamespace(thisClass);
      ElementKey key = new ElementKey(classNamespace, thisClass.getLocalName());
      if (graph.hasOwlClass(key)) {
         OwlClass owlClass = graph.getOwlClass(key);
         boolean isEmpty = true;
         try {
            if (thisClass.hasSuperClass()) {
               ExtendedIterator<OntClass> parents = thisClass.listSuperClasses(true);
               while (parents.hasNext()) {
                  OntClass superClass = parents.next();
                  isEmpty = false;
                  if (superClass.getLocalName() == null) {
                     continue;
                  }
                  String superclassNamespace = graph.getNamespace(superClass);
                  ElementKey skey = new ElementKey(superclassNamespace, superClass.getLocalName());
                  if (graph.hasOwlClass(skey)) {
                     OwlClass superOwlClass = graph.getOwlClass(skey);
                     if (addThingClass || !skey.equals(thingKey)) {
                        owlClass.addSuperClass(skey, superOwlClass, thingKey);
                        superOwlClass.addSubClass(key, owlClass);
                     }
                  } else if (defaultSquashedNamespace == null) {
                     addOwlClass(superClass, skey, owlClass, key, owlThingClass);
                  } else if (!SkippedNamespaces.isSkipped(defaultSquashedNamespace, skey)) {
                     addOwlClass(superClass, skey, owlClass, key, owlThingClass);
                  }
               }
            }
         } catch (ConversionException ex) {
            this.addError(ex);
         }
         // add root classes as children of Thing
         if (addThingClass && isEmpty && !key.equals(thingKey)) {
            owlThingClass.addSubClass(key, owlClass);
            owlClass.addSuperClass(thingKey, owlThingClass, thingKey);
         }
      }
   }

   private void addOwlClass(OntClass superClass, ElementKey skey, OwlClass owlClass, ElementKey key, OwlClass owlThingClass) {
      OwlClass foreignClass = createOwlClass(superClass);
      foreignClass.setForeign(true);
      graph.setHasForeignElements(true);
      setPrefix(foreignClass);
      graph.addOwlClass(foreignClass);
      foreignClass.addSubClass(key, owlClass);
      owlClass.addSuperClass(skey, foreignClass, thingKey);
      addClassToGraph(superClass, owlThingClass);
   }

   private OwlProperty addDatatypeProperty(OntProperty thisProperty, String nameSpace,
           Map<ElementKey, Set<ElementKey>> equivalentProperties, Map<ElementKey, Set<ElementKey>> domainClassToProperties,
           List<OwlRestriction> restrictions) {
      try {
         DatatypeProperty datatypeProperty = thisProperty.asDatatypeProperty();
         OwlDatatypeProperty owlProperty = new OwlDatatypeProperty(datatypeProperty, nameSpace, thisProperty.getLocalName());
         setPrefix(owlProperty);
         exprHelper.addEquivalentProperties(equivalentProperties, datatypeProperty, owlProperty.getKey());
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
         OwlDatatypeProperty owlProp = owlProperty;
         ExtendedIterator<? extends OntResource> resources = datatypeProperty.listRange();
         while (resources.hasNext()) {
            OntResource resource = resources.next();
            if (resource.isURIResource()) {
               String namespace = graph.getNamespace(resource);
               ElementKey tkey = new ElementKey(namespace, resource.getLocalName());
               OwlDatatype dtype;
               if (graph.hasDatatype(tkey)) {
                  dtype = graph.getDatatype(tkey);
               } else {
                  dtype = new OwlDatatype(tkey);
                  graph.addDatatype(dtype);
               }
               owlProperty.addType(dtype);
            }
         }
         graph.addOwlProperty(owlProperty);
         return owlProp;
      } catch (ClassCastException ex) {
         this.addError(ex);
         return null;
      }
   }

   private OwlProperty addObjectProperty(OntProperty thisProperty, String nameSpace,
           Map<ElementKey, Set<ElementKey>> equivalentProperties, Map<ElementKey, Set<ElementKey>> domainClassToProperties,
           Map<ElementKey, Set<ElementKey>> rangeClassToProperties, List<OwlRestriction> restrictions) {
      try {
         ObjectProperty objProperty = thisProperty.asObjectProperty();
         OwlObjectProperty owlProperty = new OwlObjectProperty(objProperty, nameSpace, thisProperty.getLocalName());
         setPrefix(owlProperty);
         exprHelper.addEquivalentProperties(equivalentProperties, objProperty, owlProperty.getKey());
         OwlProperty owlProp = owlProperty;
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
         return owlProp;
      } catch (ClassCastException ex) {
         this.addError(ex);
         return null;
      }
   }

   private void lookForForeignElements() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      if ((!conf.includeForeignDisconnectedElements || conf.showOwnElementsInBold) && graph.hasDefaultNamespace()) {
         // get foreign elements
         Iterator<OwlClass> it = graph.getOwlClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass theClass = it.next();
            String namespace = theClass.getSquashedNamespace();
            if (namespace != null && !namespace.equals(defaultSquashedNamespace)) {
               graph.setHasForeignElements(true);
               theClass.setForeign(true);
            } else {
               graph.setHasNonForeignElements(true);
            }
         }

         Iterator<OwlProperty> it2 = graph.getOwlProperties().values().iterator();
         while (it2.hasNext()) {
            OwlProperty theProperty = it2.next();
            String namespace = theProperty.getSquashedNamespace();
            if (namespace != null && !namespace.equals(defaultSquashedNamespace)) {
               graph.setHasForeignElements(true);
               theProperty.setForeign(true);
            } else {
               graph.setHasNonForeignElements(true);
            }
         }

         Iterator<OwlIndividual> it3 = graph.getIndividuals().values().iterator();
         while (it3.hasNext()) {
            OwlIndividual theIndividual = it3.next();
            String namespace = theIndividual.getSquashedNamespace();
            if (namespace != null && !namespace.equals(defaultSquashedNamespace)) {
               graph.setHasForeignElements(true);
               theIndividual.setForeign(true);
            } else {
               graph.setHasNonForeignElements(true);
            }
         }
         if (!conf.includeForeignDisconnectedElements) {
            removeDisconnectedForeignElements();
         }
      }
   }

   private void removeDisconnectedForeignElements() {
      removeDisconnectedForeignClasses();
      removeDisconnectedForeignProperties();
      removeDisconnectedForeignIndividuals();
   }

   private void removeDisconnectedForeignIndividuals() {
      List<ElementKey> toRemove = new ArrayList<>();
      Iterator<OwlIndividual> it = graph.getIndividuals().values().iterator();
      while (it.hasNext()) {
         OwlIndividual theIndividual = it.next();
         if (theIndividual.isForeign()) {
            boolean connected = false;
            Iterator<OwlClass> it2 = theIndividual.getParentClasses().values().iterator();
            while (it2.hasNext()) {
               OwlClass theClass = it2.next();
               if (!theClass.isForeign()) {
                  connected = true;
                  break;
               }
            }

            if (!connected) {
               toRemove.add(theIndividual.getKey());
            }
         }
      }
      if (!toRemove.isEmpty()) {
         Map<ElementKey, OwlIndividual> individuals = graph.getIndividuals();
         Iterator<ElementKey> itr = toRemove.iterator();
         while (itr.hasNext()) {
            ElementKey key = itr.next();
            individuals.remove(key);
         }
      }
   }

   private void removeDisconnectedForeignProperties() {
      List<ElementKey> toRemove = new ArrayList<>();
      Iterator<OwlProperty> it = graph.getOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty theProperty = it.next();
         if (theProperty.isForeign()) {
            boolean connected = false;
            if (theProperty.hasFromAliasedProperties()) {
               Iterator<OwlProperty> ita = theProperty.getFromAliasProperties().values().iterator();
               while (ita.hasNext()) {
                  OwlProperty aliasProperty = ita.next();
                  if (!aliasProperty.isForeign()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theProperty.hasAliasProperties()) {
               Iterator<OwlProperty> ita = theProperty.getAliasProperties().values().iterator();
               while (ita.hasNext()) {
                  OwlProperty aliasProperty = ita.next();
                  if (!aliasProperty.isForeign()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theProperty.hasSuperProperties()) {
               Iterator<OwlProperty> ita = theProperty.getSuperProperties().values().iterator();
               while (ita.hasNext()) {
                  OwlProperty superProperty = ita.next();
                  if (!superProperty.isForeign()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theProperty.hasSubProperties()) {
               Iterator<OwlProperty> ita = theProperty.getSubProperties().values().iterator();
               while (ita.hasNext()) {
                  OwlProperty superProperty = ita.next();
                  if (!superProperty.isForeign()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            Iterator<Entry<ElementKey, OwlClass>> itd = theProperty.getDomainIterator();
            while (itd.hasNext()) {
               Entry<ElementKey, OwlClass> entry = itd.next();
               if (!entry.getValue().isForeign()) {
                  connected = true;
                  break;
               }
            }
            if (connected) {
               continue;
            }
            if (theProperty.isObjectProperty()) {
               OwlObjectProperty objectProperty = (OwlObjectProperty) theProperty;
               itd = objectProperty.getRangeIterator();
               while (itd.hasNext()) {
                  Entry<ElementKey, OwlClass> entry = itd.next();
                  if (!entry.getValue().isForeign()) {
                     connected = true;
                     break;
                  }
               }
               if (connected) {
                  continue;
               }
               if (objectProperty.hasInverseProperty()) {
                  OwlObjectProperty inverseProperty = objectProperty.getInverseProperty();
                  if (!inverseProperty.isForeign()) {
                     connected = true;
                     break;
                  }
               }
               if (connected) {
                  continue;
               }
            }
            if (!connected) {
               toRemove.add(theProperty.getKey());
            }
         }
      }
      if (!toRemove.isEmpty()) {
         Map<ElementKey, OwlProperty> properties = graph.getOwlProperties();
         Iterator<ElementKey> itr = toRemove.iterator();
         while (itr.hasNext()) {
            ElementKey key = itr.next();
            OwlProperty theProperty = properties.remove(key);
            if (theProperty instanceof OwlObjectProperty) {
               graph.getOwlObjectProperties().remove(key);
            } else {
               graph.getOwlDatatypeProperties().remove(key);
            }
         }
      }
   }

   private void removeDisconnectedForeignClasses() {
      List<ElementKey> toRemove = new ArrayList<>();
      Iterator<OwlClass> it = graph.getOwlClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         if (theClass.isForeign()) {
            boolean connected = false;
            if (theClass.hasAliasClasses()) {
               Iterator<OwlClass> ita = theClass.getAliasClasses().values().iterator();
               while (ita.hasNext()) {
                  OwlClass aliasClass = ita.next();
                  if (!aliasClass.isForeign() && !aliasClass.isThing()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theClass.hasFromAliasedClasses()) {
               Iterator<OwlClass> ita = theClass.getFromAliasClasses().values().iterator();
               while (ita.hasNext()) {
                  OwlClass aliasClass = ita.next();
                  if (!aliasClass.isForeign() && !aliasClass.isThing()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theClass.hasSuperClasses()) {
               Iterator<OwlClass> its = theClass.getSuperClasses().values().iterator();
               while (its.hasNext()) {
                  OwlClass superClass = its.next();
                  if (!superClass.isForeign() && !superClass.isThing()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theClass.hasSubClasses()) {
               Iterator<OwlClass> its = theClass.getSubClasses().values().iterator();
               while (its.hasNext()) {
                  OwlClass subClass = its.next();
                  if (!subClass.isForeign() && !subClass.isThing()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (theClass.hasOwlProperties()) {
               Iterator<OwlProperty> itp = theClass.getOwlProperties().values().iterator();
               while (itp.hasNext()) {
                  OwlProperty property = itp.next();
                  if (!property.isForeign()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (connected) {
               continue;
            }
            if (theClass.hasIndividuals()) {
               Iterator<OwlIndividual> iti = theClass.getIndividuals().values().iterator();
               while (iti.hasNext()) {
                  OwlIndividual individual = iti.next();
                  if (!individual.isForeign()) {
                     connected = true;
                     break;
                  }
               }
            }
            if (!connected) {
               toRemove.add(theClass.getKey());
            }
         }
      }
      if (!toRemove.isEmpty()) {
         Map<ElementKey, OwlClass> classes = graph.getOwlClasses();
         Iterator<ElementKey> itr = toRemove.iterator();
         while (itr.hasNext()) {
            ElementKey key = itr.next();
            classes.remove(key);
         }
      }
   }

   private void setPrefix(NamedOwlElement element) {
      String namespace = element.getNamespace();
      if (namespace != null) {
         String prefix = graph.getPrefix(namespace);
         if (prefix != null) {
            element.setPrefix(prefix);
            if (lookForDefaultNamespace) {
               graph.setDefaultNamespace(prefix, namespace);
               lookForDefaultNamespace = false;
            }
         }
      }
   }

   private void addIndividual(Individual individual) {
      if (individual.getLocalName() == null) {
         List<OntClass> theClasses = getOwlClasses(individual);
         if (!theClasses.isEmpty()) {
            OntClass ontClass = theClasses.get(0);
            addError("individual.class.noname", ontClass.getLocalName());
         } else {
            addError("individual.noname");
         }
         return;
      }
      List<OntClass> theClasses = getOwlClasses(individual);
      if (!theClasses.isEmpty()) {
         Iterator<OntClass> it = theClasses.iterator();
         Map<ElementKey, OwlClass> parentClasses = new HashMap<>();
         while (it.hasNext()) {
            OntClass theClass = it.next();
            String namespace = graph.getNamespace(theClass);
            ElementKey theKey = new ElementKey(namespace, theClass.getLocalName());
            if (graph.hasOwlClass(theKey)) {
               OwlClass theOwlClass = graph.getOwlClass(theKey);
               parentClasses.put(theKey, theOwlClass);
            }
         }
         OwlIndividual owlIndividual = createOwlIndividual(parentClasses, individual);
         setPrefix(owlIndividual);
         if (!graph.hasIndividual(owlIndividual.getKey())) {
            StmtIterator iterSmt = individual.listProperties();
            while (iterSmt.hasNext()) {
               Statement statement = iterSmt.next();
               RDFNode node = statement.getObject();
               Property predicate = statement.getPredicate();
               ElementKey theKey = ElementKey.create(predicate.getNameSpace(), predicate.getLocalName());
               if (!graph.hasOwlProperty(theKey)) {
                  OwlAnnotation annotation = graph.getOrCreateAnnotation(theKey);
                  annotationsHelper.addAnnotationValue(node, owlIndividual, annotation);
               }
            }
            graph.addIndividual(owlIndividual);
         }
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
               try {
                  ontProperty = ontProperty.getInverse();
                  ElementKey inverseKey = new ElementKey(ontProperty.getNameSpace(), ontProperty.getLocalName());
                  if (modelProps.containsKey(inverseKey)) {
                     objectProp.setInverseProperty((OwlObjectProperty) modelProps.get(inverseKey));
                  }
               } catch (ConversionException ex) {
                  this.addError(ex);
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
               if (theClass != null) {
                  Iterator<PropertyClassRef> it3 = listRangeRef.iterator();
                  while (it3.hasNext()) {
                     theClass.addFromDomain(it3.next());
                  }
               }
            }
            it = objectProp.getRange().keySet().iterator();
            while (it.hasNext()) {
               ElementKey classKey = it.next();
               OwlClass theClass = classes.get(classKey);
               if (theClass != null) {
                  Iterator<PropertyClassRef> it3 = listDomainRef.iterator();
                  while (it3.hasNext()) {
                     theClass.addToRange(it3.next());
                  }
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
