/*
Copyright (c) 2021, 2023 Hervé Girod
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
package org.girod.ontobrowser.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.girod.ontobrowser.model.restriction.OwlRestriction;

/**
 * Specifies the graph of an Owl ontology.
 *
 * @version 0.6
 */
public class OwlSchema implements Cloneable, Serializable {
   private final OntModel ontModel;
   private OwlClass owlThingClass;
   private final Map<String, String> prefixMap = new HashMap<>();
   private final Map<ElementKey, OwlClass> classes = new HashMap<>();
   private final Map<ElementKey, OwlIndividual> individuals = new HashMap<>();
   private final Map<ElementKey, OwlDatatypeProperty> datatypeProperties = new HashMap<>();
   private final Map<ElementKey, OwlObjectProperty> objectProperties = new HashMap<>();
   private final Map<ElementKey, OwlProperty> properties = new HashMap<>();
   private final Map<ElementKey, OwlAnnotation> annotations = new HashMap<>();
   private final Set<String> namespaces = new HashSet<>();
   private Map<ElementKey, OwlClass> packages = null;

   public OwlSchema(OntModel ontModel) {
      this.ontModel = ontModel;
      computePrefixMap();
   }

   private void computePrefixMap() {
      Iterator<Entry<String, String>> it = ontModel.getNsPrefixMap().entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         String ns = entry.getValue();
         String prefix = entry.getKey();
         if (!prefix.isEmpty()) {
            prefixMap.put(ns, prefix);
         }
      }
   }

   /**
    * Return the prefix for a namespace.
    *
    * @param namespace the namespace
    * @return the prefix
    */
   public String getPrefix(String namespace) {
      return prefixMap.get(namespace);
   }

   /**
    * Return the namespaces.
    *
    * @return the namespaces
    */
   public Set<String> getNamespaces() {
      return namespaces;
   }

   /**
    * Return the Thing class.
    *
    * @return the Thing class
    */
   public OwlClass getThingClass() {
      if (owlThingClass == null) {
         OntClass thingClass = ontModel.getOntClass("http://www.w3.org/2002/07/owl#Thing");
         owlThingClass = new OwlClass(thingClass);
      }
      return owlThingClass;
   }

   /**
    * Return true if there are packages in the model.
    *
    * @return true if there are packages in the model
    */
   public boolean hasPackages() {
      return packages != null;
   }

   /**
    * Set the packages in the model.
    *
    * @param packages the packages
    */
   public void setPackages(Map<ElementKey, OwlClass> packages) {
      this.packages = packages;
   }

   /**
    * Return the packages in the model. Note that the returned value may be null if the model was not created with packages.
    *
    * @return the packages
    */
   public Map<ElementKey, OwlClass> getPackages() {
      return packages;
   }

   /**
    * Return true if a class is a package.
    *
    * @param key the class key
    * @return true if the class is a package
    */
   public boolean isPackage(ElementKey key) {
      if (packages == null) {
         return false;
      } else {
         return packages.containsKey(key);
      }
   }

   /**
    * Add an individual.
    *
    * @param individual the individual
    */
   public void addIndividual(OwlIndividual individual) {
      addNamespace(individual);
      individuals.put(individual.getKey(), individual);
      Iterator<OwlClass> it = individual.getParentClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         theClass.addIndividual(individual);
      }
   }

   /**
    * Return the individuals.
    *
    * @return the individuals
    */
   public Map<ElementKey, OwlIndividual> getIndividuals() {
      return individuals;
   }

   /**
    * Return true if there is ajn individual for a specified key.
    *
    * @param key the key
    * @return true if there is ajn individual for the specified key
    */
   public boolean hasIndividual(ElementKey key) {
      return individuals.containsKey(key);
   }

   /**
    * Return the individual of a specified key.
    *
    * @param key the key
    * @return the individual
    */
   public OwlIndividual getIndividual(ElementKey key) {
      return individuals.get(key);
   }
   
   /**
    * Add an annotation.
    *
    * @param annotation the annotation
    */
   public void addAnnotation(OwlAnnotation annotation) {
      addNamespace(annotation);
      annotations.put(annotation.getKey(), annotation);
   }   
   
   /**
    * Return true if there is an annotation for a specified key.
    *
    * @param key the key
    * @return true if there is an annotation for the specified key
    */
   public boolean hasAnnotation(ElementKey key) {
      return annotations.containsKey(key);
   }
   
   /**
    * Return the annotation for a specified key.
    *
    * @param key the key
    * @return the annotation
    */
   public OwlAnnotation getAnnotation(ElementKey key) {
      return annotations.get(key);
   }      

   /**
    * Return the annotations.
    *
    * @return the annotations
    */
   public Map<ElementKey, OwlAnnotation> getAnnotations() {
      return annotations;
   }   

   /**
    * Add an Owl class.
    *
    * @param owlClass the owl class
    */
   public void addOwlClass(OwlClass owlClass) {
      addNamespace(owlClass);
      classes.put(owlClass.getKey(), owlClass);
   }

   private void addNamespace(NamedOwlElement element) {
      String namespace = element.getNamespace();
      if (namespace != null && !namespaces.contains(namespace)) {
         namespaces.add(namespace);
      }
   }

   /**
    * Return the Owl classes.
    *
    * @return the Owl classes
    */
   public Map<ElementKey, OwlClass> getOwlClasses() {
      return classes;
   }

   /**
    * Return true if there is a Owl class for a specified key.
    *
    * @param key the key
    * @return true if there is a Owl class for the specified key
    */
   public boolean hasOwlClass(ElementKey key) {
      return classes.containsKey(key);
   }

   /**
    * Return the Owl class of a specified key.
    *
    * @param key the key
    * @return the Owl class
    */
   public OwlClass getOwlClass(ElementKey key) {
      return classes.get(key);
   }

   /**
    * Add an Owl property.
    *
    * @param owlProperty the Owl property
    */
   public void addOwlProperty(OwlProperty owlProperty) {
      addNamespace(owlProperty);
      properties.put(owlProperty.getKey(), owlProperty);
      if (owlProperty instanceof OwlDatatypeProperty) {
         datatypeProperties.put(owlProperty.getKey(), (OwlDatatypeProperty) owlProperty);
      } else if (owlProperty instanceof OwlObjectProperty) {
         objectProperties.put(owlProperty.getKey(), (OwlObjectProperty) owlProperty);
      }
   }

   /**
    * Return true if there is an Owl property for a specified key.
    *
    * @param key the key
    * @return true if there is an Owl property for the specified key
    */
   public boolean hasOwlProperty(ElementKey key) {
      return properties.containsKey(key);
   }

   /**
    * Return the Owl properties.
    *
    * @return the Owl properties
    */
   public Map<ElementKey, OwlProperty> getOwlProperties() {
      return properties;
   }

   /**
    * Return the datatypes properties.
    *
    * @return the properties
    */
   public Map<ElementKey, OwlDatatypeProperty> getOwlDatatypeProperties() {
      return datatypeProperties;
   }

   /**
    * Return the object properties.
    *
    * @return the properties
    */
   public Map<ElementKey, OwlObjectProperty> getOwlObjectProperties() {
      return objectProperties;
   }

   /**
    * Return the Owl property of a specified key.
    *
    * @param key the key
    * @return the Owl property
    */
   public OwlProperty getOwlProperty(ElementKey key) {
      return properties.get(key);
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param key the class key
    * @return the dependant classes
    */
   public Map<ElementKey, OwlClass> getDependentClasses(ElementKey key) {
      if (!hasOwlClass(key)) {
         return null;
      } else {
         OwlClass theClass = getOwlClass(key);
         return getDependentClasses(theClass);
      }
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param theClass the class
    * @return the dependant classes
    */
   public Map<ElementKey, OwlClass> getDependentClasses(OwlClass theClass) {
      Map<ElementKey, OwlClass> map = new HashMap<>();
      Iterator<OwlProperty> it = theClass.getDomainOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property instanceof OwlObjectProperty) {
            OwlObjectProperty objectProperty = (OwlObjectProperty) property;
            Map<ElementKey, OwlRestriction> restrictions = objectProperty.getRange();
            Iterator<OwlRestriction> it2 = restrictions.values().iterator();
            while (it2.hasNext()) {
               OwlRestriction restriction = it2.next();
               OwlClass class2 = restriction.getOwlClass();
               if (class2 != theClass) {
                  map.put(class2.getKey(), class2);
               }
            }
         }
      }
      it = theClass.getRangeOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property instanceof OwlObjectProperty) {
            OwlObjectProperty objectProperty = (OwlObjectProperty) property;
            Map<ElementKey, OwlRestriction> restrictions = objectProperty.getDomain();
            Iterator<OwlRestriction> it2 = restrictions.values().iterator();
            while (it2.hasNext()) {
               OwlRestriction restriction = it2.next();
               OwlClass class2 = restriction.getOwlClass();
               if (class2 != theClass) {
                  map.put(class2.getKey(), class2);
               }
            }
         }
      }
      return map;
   }

   @Override
   public OwlSchema clone() {
      try {
         Object o = super.clone();
         return (OwlSchema) o;
      } catch (CloneNotSupportedException ex) {
         return null;
      }
   }
}
