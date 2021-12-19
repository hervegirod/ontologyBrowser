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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Specifies the graph of an Owl ontology.
 *
 * @since 0.1
 */
public class OwlSchema implements Cloneable, Serializable {
   private final Map<ElementKey, OwlClass> classes = new HashMap<>();
   private final Map<ElementKey, OwlIndividual> individuals = new HashMap<>();
   private final Map<ElementKey, OwlDatatypeProperty> datatypeProperties = new HashMap<>();
   private final Map<ElementKey, OwlProperty> properties = new HashMap<>();

   public OwlSchema() {
   }

   public void addIndividual(OwlIndividual individual) {
      individuals.put(individual.getKey(), individual);
      individual.getParentClass().addIndividual(individual);
   }

   public Map<ElementKey, OwlIndividual> getIndividuals() {
      return individuals;
   }

   public boolean hasIndividual(ElementKey key) {
      return individuals.containsKey(key);
   }

   public OwlIndividual getIndividual(ElementKey key) {
      return individuals.get(key);
   }

   public void addOwlClass(OwlClass owlClass) {
      classes.put(owlClass.getKey(), owlClass);
   }

   public Map<ElementKey, OwlClass> getOwlClasses() {
      return classes;
   }

   public boolean hasOwlClass(ElementKey key) {
      return classes.containsKey(key);
   }

   public OwlClass getOwlClass(ElementKey key) {
      return classes.get(key);
   }

   public void addOwlProperty(OwlProperty owlProperty) {
      properties.put(owlProperty.getKey(), owlProperty);
      if (owlProperty instanceof OwlDatatypeProperty) {
         datatypeProperties.put(owlProperty.getKey(), (OwlDatatypeProperty) owlProperty);
      }
   }

   public boolean hasOwlProperty(ElementKey key) {
      return properties.containsKey(key);
   }

   public Map<ElementKey, OwlProperty> getOwlProperties() {
      return properties;
   }

   public Map<ElementKey, OwlDatatypeProperty> getOwlDatatypeProperties() {
      return datatypeProperties;
   }

   public OwlProperty getOwlProperty(ElementKey key) {
      return properties.get(key);
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
