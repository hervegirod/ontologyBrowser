/*
Copyright (c) 2021, Hervé Girod
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
import java.util.Map;
import java.util.Set;
import org.apache.jena.ontology.OntClass;

/**
 *
 * @since 0.1
 */
public class OwlClass extends NamedOwlElement {
   private final Map<ElementKey, Set<PropertyClassRef>> fromDomain = new HashMap<>();
   private final Map<ElementKey, Set<PropertyClassRef>> toRange = new HashMap<>();
   private final Set<ElementKey> superClasses = new HashSet<>();
   private final Set<ElementKey> subClasses = new HashSet<>();
   private final Map<ElementKey, OwlIndividual> individuals = new HashMap<>();
   private final Map<ElementKey, OwlProperty> properties = new HashMap<>();

   public OwlClass(OntClass ontClass) {
      super(ontClass.getNameSpace(), ontClass.getLocalName());
   }

   public OwlClass(String namespace, String name) {
      super(namespace, name);
   }

   @Override
   public OwlClass clone() {
      Object o = super.clone();
      return (OwlClass) o;
   }

   public void addIndividual(OwlIndividual individual) {
      individuals.put(individual.getKey(), individual);
   }

   public Map<ElementKey, OwlIndividual> getIndividuals() {
      return individuals;
   }

   public boolean hasIndividuals() {
      return !individuals.isEmpty();
   }

   public void addSuperClass(ElementKey superClass) {
      superClasses.add(superClass);
   }

   public Set<ElementKey> getSuperClasses() {
      return superClasses;
   }

   public void addSubClass(ElementKey superClass) {
      subClasses.add(superClass);
   }

   public Set<ElementKey> getSubClasses() {
      return subClasses;
   }

   public void addFromDomain(PropertyClassRef ref) {
      Set<PropertyClassRef> set;
      ElementKey classKey = ref.getClassKey();
      if (fromDomain.containsKey(classKey)) {
         set = fromDomain.get(classKey);
      } else {
         set = new HashSet<>();
         fromDomain.put(classKey, set);
      }
      set.add(ref);
   }

   public void addToRange(PropertyClassRef ref) {
      Set<PropertyClassRef> set;
      ElementKey classKey = ref.getClassKey();
      if (toRange.containsKey(classKey)) {
         set = toRange.get(classKey);
      } else {
         set = new HashSet<>();
         toRange.put(classKey, set);
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

}
