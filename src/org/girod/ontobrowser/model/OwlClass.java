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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.OntClass;

/**
 * Represents an Owl class.
 *
 * @version 0.4
 */
public class OwlClass extends NamedOwlElement {
   private final Map<ElementKey, Set<PropertyClassRef>> fromDomain = new HashMap<>();
   private final Map<ElementKey, Set<PropertyClassRef>> toRange = new HashMap<>();
   private final Map<ElementKey, OwlClass> superClasses = new HashMap<>();
   private final Map<ElementKey, OwlClass> subClasses = new HashMap<>();
   private boolean hasDefinedSuperClass = false;
   private boolean isPackage = false;
   private ElementKey packageKey = null;
   private final Map<ElementKey, OwlIndividual> individuals = new HashMap<>();
   private final Map<ElementKey, OwlProperty> properties = new HashMap<>();

   public OwlClass(OntClass ontClass) {
      super(ontClass.getNameSpace(), ontClass.getLocalName());
   }

   public OwlClass(String namespace, String name) {
      super(namespace, name);
   }

   /**
    * Set if this class is a package.
    *
    * @param isPackage true if this class is a package
    */
   public void setIsPackage(boolean isPackage) {
      this.isPackage = isPackage;
   }

   /**
    * Return true if this class is a package.
    *
    * @return true if this class is a package
    */
   public boolean isPackage() {
      return isPackage;
   }

   /**
    * Set the package of this class.
    *
    * @param packageKey the package key
    */
   public void setPackage(ElementKey packageKey) {
      this.packageKey = packageKey;
   }

   public boolean isInPackage() {
      return packageKey != null;
   }

   public ElementKey getPackage() {
      return packageKey;
   }

   /**
    * Return true if the Owl class has a defined superclass (excluing the Thing class).
    *
    * @return true if the Owl class has a defined superclass
    */
   public boolean hasDefinedSuperClass() {
      return hasDefinedSuperClass;
   }

   @Override
   public OwlClass clone() {
      Object o = super.clone();
      return (OwlClass) o;
   }

   public void addIndividual(OwlIndividual individual) {
      individuals.put(individual.getKey(), individual);
   }

   /**
    * Return the individuals of this Owl class.
    *
    * @return the individuals
    */
   public Map<ElementKey, OwlIndividual> getIndividuals() {
      return individuals;
   }

   public boolean hasIndividuals() {
      return !individuals.isEmpty();
   }

   public void addSuperClass(ElementKey superClassKey, OwlClass owlClass, ElementKey thingKey) {
      superClasses.put(superClassKey, owlClass);
      if (thingKey == null) {
         hasDefinedSuperClass = true;
         if (owlClass.isInPackage()) {
            packageKey = owlClass.getPackage();
         }
      } else {
         hasDefinedSuperClass = !superClassKey.equals(thingKey);
         if (hasDefinedSuperClass && owlClass.isInPackage()) {
            packageKey = owlClass.getPackage();
         }
      }
   }

   /**
    * Return the superclasses of this Owl class.
    *
    * @return the superclasses
    */
   public Map<ElementKey, OwlClass> getSuperClasses() {
      return superClasses;
   }

   /**
    * Count the number of superclasses of this class.
    *
    * @return the number of superclasses
    */
   public int countSuperClasses() {
      return superClasses.size();
   }

   /**
    * Return the first found superclass, or null if there is no superclass.
    *
    * @return the first found superclass
    */
   public OwlClass getFirstSuperClass() {
      if (superClasses.isEmpty()) {
         return null;
      } else {
         return superClasses.values().iterator().next();
      }
   }

   public void addSubClass(ElementKey key, OwlClass owlClass) {
      subClasses.put(key, owlClass);
   }

   /**
    * Return the subclasses of this Owl class.
    *
    * @return the subclasses
    */
   public Map<ElementKey, OwlClass> getSubClasses() {
      return subClasses;
   }

   /**
    * Return true if this Owl class has subclasses.
    *
    * @return true if this Owl class has subclasses
    */
   public boolean hasSubClasses() {
      return !subClasses.isEmpty();
   }

   public void addFromDomain(PropertyClassRef ref) {
      Set<PropertyClassRef> set;
      ElementKey domainKey = ref.getDomainKey();
      if (fromDomain.containsKey(domainKey)) {
         set = fromDomain.get(domainKey);
      } else {
         set = new HashSet<>();
         fromDomain.put(domainKey, set);
      }
      set.add(ref);
   }

   public void addToRange(PropertyClassRef ref) {
      Set<PropertyClassRef> set;
      ElementKey domainKey = ref.getDomainKey();
      if (toRange.containsKey(domainKey)) {
         set = toRange.get(domainKey);
      } else {
         set = new HashSet<>();
         toRange.put(domainKey, set);
      }
      set.add(ref);
   }

   public Map<ElementKey, Set<PropertyClassRef>> fromDomain() {
      return fromDomain;
   }

   public Map<ElementKey, Set<PropertyClassRef>> toRange() {
      return toRange;
   }

   public void addOwlProperty(OwlProperty owlProperty) {
      properties.put(owlProperty.getKey(), owlProperty);
   }

   public Map<ElementKey, OwlProperty> getOwlProperties() {
      return properties;
   }

   public OwlProperty getOwlProperty(ElementKey key) {
      return properties.get(key);
   }

   public boolean hasOwlProperties() {
      return !properties.isEmpty();
   }

}
