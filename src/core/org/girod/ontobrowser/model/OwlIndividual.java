/*
Copyright (c) 2021, 2023, 2024 Hervé Girod
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Resource;

/**
 * Represents an Individual.
 *
 * @version 0.13
 * @param <I> the type of the underlying class
 */
public class OwlIndividual<I extends Resource> extends NamedOwlElement<OwlIndividual> {
   private final I individual;
   private final Map<ElementKey, OwlClass> parentClasses;
   private final Map<ElementKey, List<ObjectPropertyValue>> objectPropertyValues = new HashMap<>();
   private final Map<ElementKey, List<ObjectPropertyValue>> objectTargetPropertyValues = new HashMap<>();
   private final Map<ElementKey, List<DatatypePropertyValue>> datatypePropertyValues = new HashMap<>();

   public OwlIndividual(I individual) {
      this(individual, individual.getNameSpace());
   }

   public OwlIndividual(I individual, String namespace) {
      super(namespace, individual.getLocalName());
      this.parentClasses = new HashMap<>();
      this.individual = individual;
   }

   public OwlIndividual(OwlClass parentClass, I individual) {
      this(parentClass, individual, individual.getNameSpace());
   }

   public OwlIndividual(OwlClass parentClass, I individual, String namespace) {
      super(namespace, individual.getLocalName());
      this.parentClasses = new HashMap<>();
      this.parentClasses.put(parentClass.getKey(), parentClass);
      this.individual = individual;
      updateNameSpace();
   }

   public OwlIndividual(Map<ElementKey, OwlClass> parentClasses, I individual) {
      this(parentClasses, individual, individual.getNameSpace());
   }

   public OwlIndividual(Map<ElementKey, OwlClass> parentClasses, I individual, String namespace) {
      super(namespace, individual.getLocalName());
      this.parentClasses = parentClasses;
      this.individual = individual;
      updateNameSpace();
   }

   /**
    * Return the Individual.
    *
    * @return the Individual
    */
   public I getIndividual() {
      return individual;
   }

   private void updateNameSpace() {
      if (namespace == null && !parentClasses.isEmpty()) {
         String _namespace = null;
         Iterator<ElementKey> it = parentClasses.keySet().iterator();
         while (it.hasNext()) {
            _namespace = it.next().getNamespace();
            break;
         }
         this.namespace = _namespace;
      }
   }

   /**
    * Return the first parent Class associated with this Individual.
    *
    * @return the first parent Class
    */
   public OwlClass getFirstParentClass() {
      if (parentClasses == null || parentClasses.isEmpty()) {
         return null;
      } else {
         return parentClasses.values().iterator().next();
      }
   }

   /**
    * Return the Classes associated with this Individual.
    *
    * @return the Classes
    */
   public Map<ElementKey, OwlClass> getParentClasses() {
      return parentClasses;
   }

   /**
    * Add an object property value.
    *
    * @param value the value
    */
   public void addObjectPropertyValue(ObjectPropertyValue value) {
      ElementKey theKey = value.getKey();
      List<ObjectPropertyValue> values;
      if (objectPropertyValues.containsKey(theKey)) {
         values = objectPropertyValues.get(theKey);
      } else {
         values = new ArrayList<>();
         objectPropertyValues.put(theKey, values);
      }
      values.add(value);

      OwlIndividual target = value.getTarget();
      List<ObjectPropertyValue> valuesT;
      if (target.objectTargetPropertyValues.containsKey(theKey)) {
         valuesT = (List<ObjectPropertyValue>) target.objectTargetPropertyValues.get(theKey);
      } else {
         valuesT = new ArrayList<>();
         target.objectTargetPropertyValues.put(theKey, valuesT);
      }
      valuesT.add(value);
   }

   /**
    * Return true if this class has property values.
    *
    * @return true if this class has property values
    */
   public boolean hasPropertyValues() {
      return !objectPropertyValues.isEmpty() || !datatypePropertyValues.isEmpty();
   }

   /**
    * Return true if this class has data property values.
    *
    * @return true if this class has data property values
    */
   public boolean hasDatatypePropertyValues() {
      return !datatypePropertyValues.isEmpty();
   }
   
   /**
    * Return true if this class has data property values for a specified key.
    *
    * @param propertyKey the property key
    * @return true if this class has data property values for a specified key
    */
   public boolean hasDatatypePropertyValues(ElementKey propertyKey) {
      return datatypePropertyValues.containsKey(propertyKey);
   }   

   /**
    * Return true if this class has object property values.
    *
    * @return true if this class has object property values
    */
   public boolean hasObjectPropertyValues() {
      return !objectPropertyValues.isEmpty();
   }

   /**
    * Return true if this class has object property values for a specified key.
    *
    * @param propertyKey the property key
    * @return true if this class has object property values for a specified key
    */
   public boolean hasObjectPropertyValues(ElementKey propertyKey) {
      return objectPropertyValues.containsKey(propertyKey);
   }

   /**
    * Return true if this class has object target property values. It means that the individual is the target of a property.
    *
    * @return true if this class has object target property values
    */
   public boolean hasObjectTargetPropertyValues() {
      return !objectTargetPropertyValues.isEmpty();
   }

   /**
    * Return the object property values.
    *
    * @return the object property values
    */
   public Map<ElementKey, List<ObjectPropertyValue>> getObjectPropertyValues() {
      return objectPropertyValues;
   }

   /**
    * Return the object target property values. It means that the individual is the target of properties.
    *
    * @return the object target property values
    */
   public Map<ElementKey, List<ObjectPropertyValue>> getObjectTargetPropertyValues() {
      return objectTargetPropertyValues;
   }

   /**
    * Add a datatype property value.
    *
    * @param value the value
    */
   public void addDatatypePropertyValue(DatatypePropertyValue value) {
      ElementKey theKey = value.getKey();
      List<DatatypePropertyValue> values;
      if (datatypePropertyValues.containsKey(theKey)) {
         values = datatypePropertyValues.get(theKey);
      } else {
         values = new ArrayList<>();
         datatypePropertyValues.put(theKey, values);
      }
      values.add(value);
   }

   /**
    * Return the datatype property values.
    *
    * @return the datatype propety values
    */
   public Map<ElementKey, List<DatatypePropertyValue>> getDatatypePropertyValues() {
      return datatypePropertyValues;
   }

   /**
    * Return the datatype property values.
    *
    * @param key the property key
    * @return the datatype property values
    */
   public List<? extends PropertyValue> getPropertyValues(ElementKey key) {
      if (datatypePropertyValues.containsKey(key)) {
         return datatypePropertyValues.get(key);
      } else {
         return objectPropertyValues.get(key);
      }
   }

   @Override
   public void accept(ElementVisitor visitor) {
      boolean cont = visitor.visit(this);
      if (cont) {
         Iterator<List<ObjectPropertyValue>> it = objectPropertyValues.values().iterator();
         while (it.hasNext()) {
            List<ObjectPropertyValue> values = it.next();
            Iterator<ObjectPropertyValue> it2 = values.iterator();
            while (it2.hasNext()) {
               ObjectPropertyValue theValue = it2.next();
               theValue.accept(visitor);
            }
         }
         Iterator<List<DatatypePropertyValue>> it2 = datatypePropertyValues.values().iterator();
         while (it2.hasNext()) {
            List<DatatypePropertyValue> values = it2.next();
            Iterator<DatatypePropertyValue> it3 = values.iterator();
            while (it3.hasNext()) {
               DatatypePropertyValue theValue = it3.next();
               theValue.accept(visitor);
            }
         }
      }
   }
}
