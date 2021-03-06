/*
Copyright (c) 2021, 2022 Hervé Girod
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.CardinalityRestriction;
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
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.ElementKey;
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

/**
 *
 * @version 0.2
 */
public class GraphExtractor {
   private final OntModel model;

   public GraphExtractor(OntModel model) {
      this.model = model;
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
         Restriction restriction = clazz.asRestriction();
         owlRestriction = getRestrictionFrom(restriction);
      } else {
         owlRestriction = new UnrestrictedOwlRestriction(clazz);
      }
      return owlRestriction;
   }

   public OwlSchema getGraph() {
      OwlSchema graph = new OwlSchema();
      OntClass thingClass = model.getOntClass("http://www.w3.org/2002/07/owl#Thing");
      ElementKey thingKey = new ElementKey(thingClass.getNameSpace(), thingClass.getLocalName());
      OwlClass owlThingClass = new OwlClass(thingClass);
      Map<ElementKey, Set<ElementKey>> rangeClassToProperties = new HashMap<>();
      Map<ElementKey, Set<ElementKey>> domainClassToProperties = new HashMap<>();

      // list properties
      ExtendedIterator properties = model.listAllOntProperties();
      while (properties.hasNext()) {
         OntProperty thisProperty = (OntProperty) properties.next();
         String nameSpace = thisProperty.getNameSpace();
         OwlProperty owlProp = null;
         if (thisProperty.isObjectProperty()) {
            ObjectProperty objProperty = (ObjectProperty) thisProperty;
            OwlObjectProperty owlProperty = new OwlObjectProperty(objProperty, nameSpace, thisProperty.getLocalName());
            owlProp = owlProperty;
            graph.addOwlProperty(owlProperty);
            ExtendedIterator declDomain = objProperty.listDomain();
            if (!declDomain.hasNext()) {
               addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), thingKey);
            } else {
               while (declDomain.hasNext()) {
                  OntClass thisClass = (OntClass) declDomain.next();
                  OwlRestriction restriction = getOwlRestriction(thisClass);
                  if (restriction != null) {
                     ElementKey domainKey = restriction.getKey();
                     owlProperty.addDomain(restriction);
                     addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), domainKey);
                  }
               }
            }
            ExtendedIterator declRange = objProperty.listRange();
            if (!declRange.hasNext()) {
               addToPropertyToClassMap(rangeClassToProperties, owlProperty.getKey(), thingKey);
            } else {
               while (declRange.hasNext()) {
                  OntClass thisClass = (OntClass) declRange.next();
                  OwlRestriction restriction = getOwlRestriction(thisClass);
                  if (restriction != null) {
                     ElementKey rangeKey = restriction.getKey();
                     owlProperty.addRange(restriction);
                     addToPropertyToClassMap(rangeClassToProperties, owlProperty.getKey(), rangeKey);
                  }
               }
            }
         } else if (thisProperty.isDatatypeProperty()) {
            DatatypeProperty datatypeProperty = (DatatypeProperty) thisProperty;
            OwlDatatypeProperty owlProperty = new OwlDatatypeProperty(datatypeProperty, nameSpace, thisProperty.getLocalName());
            ExtendedIterator declDomain = datatypeProperty.listDomain();
            if (!declDomain.hasNext()) {
               addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), thingKey);
            } else {
               while (declDomain.hasNext()) {
                  OntClass thisClass = (OntClass) declDomain.next();
                  OwlRestriction restriction = getOwlRestriction(thisClass);
                  if (restriction != null) {
                     ElementKey domainKey = restriction.getKey();
                     owlProperty.addDomain(restriction);
                     addToPropertyToClassMap(domainClassToProperties, owlProperty.getKey(), domainKey);
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
         }
         if (owlProp != null) {
            getRestrictions(thisProperty, owlProp);
         }
      }

      boolean hasThingClass = false;
      // list classes
      ExtendedIterator classes = model.listClasses();
      while (classes.hasNext()) {
         OntClass thisClass = (OntClass) classes.next();
         if (thisClass.getNameSpace() == null && thisClass.getLocalName() == null) {
            continue;
         }
         OwlClass owlClass;
         if (thisClass.equals(thingClass)) {
            owlClass = owlThingClass;
            hasThingClass = true;
         } else {
            String nameSpace = thisClass.getNameSpace();
            owlClass = new OwlClass(nameSpace, thisClass.getLocalName());
         }
         graph.addOwlClass(owlClass);
      }
      if (!hasThingClass) {
         graph.addOwlClass(owlThingClass);
      }

      // list individuals
      if (BrowserConfiguration.getInstance().includeIndividuals) {
         ExtendedIterator individuals = model.listIndividuals();
         while (individuals.hasNext()) {
            Individual thisIndividual = (Individual) individuals.next();
            OntClass theClass = thisIndividual.getOntClass();
            ElementKey theKey = new ElementKey(theClass.getNameSpace(), theClass.getLocalName());
            if (graph.hasOwlClass(theKey)) {
               OwlClass theOwlClass = graph.getOwlClass(theKey);
               OwlIndividual owlIndividual = new OwlIndividual(theOwlClass, thisIndividual);
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
                  owlClass.addSuperClass(skey);
                  superOwlClass.addSubClass(skey);
               }
            }
            // add root classes as children of Thing
            if (isEmpty && !key.equals(thingKey)) {
               owlThingClass.addSubClass(key);
               owlClass.addSuperClass(thingKey);
            }
         }
      }
      if (!hasThingClass) {
         ExtendedIterator<OntClass> children = thingClass.listSubClasses();
         while (children.hasNext()) {
            OntClass childClass = children.next();
            ElementKey skey = new ElementKey(childClass.getNameSpace(), childClass.getLocalName());
            if (graph.hasOwlClass(skey)) {
               OwlClass childOwlClass = graph.getOwlClass(skey);
               childOwlClass.addSuperClass(thingKey);
               owlThingClass.addSubClass(skey);
            }
         }
      }

      // class to properties dependencies
      addDependencies(graph, domainClassToProperties, rangeClassToProperties);

      return graph;
   }

   private void addDependencies(OwlSchema graph, Map<ElementKey, Set<ElementKey>> domainClassToProperties,
      Map<ElementKey, Set<ElementKey>> rangeClassToProperties) {
      Map<ElementKey, OwlClass> modelClasses = graph.getOwlClasses();
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
               theClass.addOwlProperty(property);
               property.addDomain(classKey);
            }
         }
         if (rangeClassToProperties.containsKey(classKey)) {
            Set<ElementKey> properties = rangeClassToProperties.get(classKey);
            Iterator<ElementKey> it2 = properties.iterator();
            while (it2.hasNext()) {
               ElementKey propKey = it2.next();
               OwlProperty property = modelProps.get(propKey);
               if (property instanceof OwlObjectProperty) {
                  ((OwlObjectProperty) property).addRange(classKey);
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
                  theClass.addToRange(it3.next());
               }
            }
            it = objectProp.getRange().keySet().iterator();
            while (it.hasNext()) {
               ElementKey classKey = it.next();
               OwlClass theClass = classes.get(classKey);
               Iterator<PropertyClassRef> it3 = listDomainRef.iterator();
               while (it3.hasNext()) {
                  theClass.addFromDomain(it3.next());
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
            int i = literal.getInt();
            return new WrappedValue(i);
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
