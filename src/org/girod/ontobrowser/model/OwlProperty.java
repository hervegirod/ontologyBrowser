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

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.ontology.OntProperty;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.model.restriction.UnrestrictedOwlRestriction;

/**
 * An abstract OwlProperty.
 *
 * @param <T> the property type
 * @version 0.10
 */
public abstract class OwlProperty<T extends OntProperty> extends NamedOwlElement<OwlProperty> {
   private final Map<ElementKey, OwlRestriction> domain = new HashMap<>();
   private int minCardinality = 0;
   private int maxCardinality = -1;
   private T ontProperty = null;
   private final Map<ElementKey, OwlProperty> superProperties = new HashMap<>();
   private final Map<ElementKey, OwlProperty> subProperties = new HashMap<>();
   private final Map<ElementKey, OwlProperty> aliasProperties = new HashMap<>();
   private final Map<ElementKey, OwlProperty> propertyFromAlias = new HashMap<>();

   public OwlProperty(T ontProperty, String namespace, String name) {
      super(namespace, name);
      this.ontProperty = ontProperty;
   }

   /**
    * Return the underlying property.
    *
    * @return the underlying property
    */
   public T getProperty() {
      return ontProperty;
   }

   /**
    * Return true if the property is a Funtional property.
    *
    * @return true if the property is a Funtional property
    */
   public boolean isFunctionalProperty() {
      return ontProperty.isFunctionalProperty();
   }

   /**
    * Return true if the property is an inverse Funtional property.
    *
    * @return true if the property is an inverse Funtional property
    */
   public boolean isInverseFunctionalProperty() {
      return ontProperty.isInverseFunctionalProperty();
   }

   /**
    * Add a super property.
    *
    * @param key the super property key
    * @param owlProperty the super property
    */
   public void addSuperProperty(ElementKey key, OwlProperty owlProperty) {
      superProperties.put(key, owlProperty);
   }

   /**
    * Return the super properties of this Owl property.
    *
    * @return the super properties
    */
   public Map<ElementKey, OwlProperty> getSuperProperties() {
      return superProperties;
   }

   /**
    * Return true if this Owl property has super properties.
    *
    * @return true if this Owl property has super properties
    */
   public boolean hasSuperProperties() {
      return !superProperties.isEmpty();
   }

   /**
    * Add a sub-property.
    *
    * @param key the sub-property key
    * @param owlProperty the subClass
    */
   public void addSubProperty(ElementKey key, OwlProperty owlProperty) {
      subProperties.put(key, owlProperty);
   }

   /**
    * Return the sub-properties of this Owl property.
    *
    * @return the sub-properties
    */
   public Map<ElementKey, OwlProperty> getSubProperties() {
      return subProperties;
   }

   /**
    * Return true if this Owl property has sub-properties.
    *
    * @return true if this Owl property has sub-properties
    */
   public boolean hasSubProperties() {
      return !subProperties.isEmpty();
   }

   public void addDomain(OwlRestriction restriction) {
      domain.put(restriction.getKey(), restriction);
   }

   public UnrestrictedOwlRestriction addDomain(ElementKey key) {
      UnrestrictedOwlRestriction restriction = new UnrestrictedOwlRestriction(key);
      domain.put(key, restriction);
      return restriction;
   }

   /**
    * Return true if the property has a domain.
    *
    * @return true if the property has a domain
    */
   public boolean hasDomain() {
      return !domain.isEmpty();
   }

   /**
    * Return the domain of the property.
    *
    * @return the domain
    */
   public Map<ElementKey, OwlRestriction> getDomain() {
      return domain;
   }

   /**
    * Add an equivalent (alias) property.
    *
    * @param aliasProperty the equivalent property
    */
   public void addEquivalentProperty(OwlProperty aliasProperty) {
      this.aliasProperties.put(aliasProperty.getKey(), aliasProperty);
      aliasProperty.propertyFromAlias.put(getKey(), this);
   }

   /**
    * Return the map of equivalent (alias) properties.
    *
    * @return the equivalent properties
    */
   public Map<ElementKey, OwlProperty> getEquivalentProperties() {
      return aliasProperties;
   }

   public boolean hasEquivalentProperties() {
      return !aliasProperties.isEmpty();
   }

   /**
    * Return true if this class has equivalent properties.
    *
    * @return true if this class has equivalent properties.
    */
   public boolean isEquivalentProperty() {
      return !aliasProperties.isEmpty() || !propertyFromAlias.isEmpty();
   }

   /**
    * Return the map of equivalent (alias) properties.
    *
    * @return the equivalent properties
    */
   public Map<ElementKey, OwlProperty> getFromAliasProperties() {
      return propertyFromAlias;
   }

   public boolean hasFromAliasedProperties() {
      return !propertyFromAlias.isEmpty();
   }

   /**
    * Return this element alias elements.
    *
    * @return this element alias elements
    */
   @Override
   public boolean hasAliasElements() {
      return !aliasProperties.isEmpty() || !propertyFromAlias.isEmpty();
   }

   /**
    * Return the alias elements.
    *
    * @return the alias elements
    */
   @Override
   public Map<ElementKey, OwlProperty> getAliasElements() {
      if (!hasAliasElements()) {
         return null;
      }
      Map<ElementKey, OwlProperty> map = new HashMap<>();
      map.putAll(aliasProperties);
      map.putAll(propertyFromAlias);
      return map;
   }

   @Override
   public OwlProperty clone() {
      Object o = super.clone();
      return (OwlProperty) o;
   }

   public boolean isDatatypeProperty() {
      return false;
   }

   public boolean isObjectProperty() {
      return false;
   }

   public boolean hasCardinalityRestriction() {
      return hasMinCardinality() || hasMaxCardinality();
   }

   public void setMinCardinality(int minCardinality) {
      this.minCardinality = minCardinality;
   }

   public boolean hasMinCardinality() {
      return minCardinality > 0;
   }

   /**
    * Compute the minimum cardinality of the property.
    *
    * @return the cardinality
    */
   public int computeMinCardinality() {
      if (isFunctionalProperty() || isInverseFunctionalProperty()) {
         return 0;
      } else if (!hasMinCardinality()) {
         return 0;
      } else {
         return getMinCardinality();
      }
   }

   /**
    * Return the minimum cardinality of the property.
    *
    * @return the cardinality
    */       
   public int getMinCardinality() {
      return minCardinality;
   }

   public void setMaxCardinality(int maxCardinality) {
      this.maxCardinality = maxCardinality;
   }

   public boolean hasMaxCardinality() {
      return maxCardinality != -1;
   }

   /**
    * Compute the maximum cardinality of the property. Note that if the property is functional or inverse functional, then the maximum cardinality will be 1.
    *
    * @return the cardinality
    */   
   public int computeMaxCardinality() {
      if (isFunctionalProperty() || isInverseFunctionalProperty()) {
         return 1;
      } else if (!hasMaxCardinality()) {
         return -1;
      } else {
         return getMaxCardinality();
      }
   }

   /**
    * Return the maximum cardinality of the property, not taking into account if the property is functional or inverse functional.
    *
    * @return the cardinality
    */    
   public int getMaxCardinality() {
      return maxCardinality;
   }

   @Override
   public void accept(ElementVisitor visitor) {
      visitor.visit(this);
   }
}
