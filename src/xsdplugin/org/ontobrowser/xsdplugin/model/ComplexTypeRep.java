/*
Copyright (c) 2024 Hervé Girod
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
package org.ontobrowser.xsdplugin.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.restriction.OwlCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMaxCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMaxQualifiedCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMinCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlMinQualifiedCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlQualifiedCardinalityRestriction;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.utils.SchemaUtils;

/**
 *
 * @since 0.13
 */
public class ComplexTypeRep {
   private final OwlClass owlClass;
   private String name = null;
   private ComplexTypeRep superElement = null;
   private final Map<ElementKey, OwlObjectProperty> objectProperties = new HashMap<>();
   private final Map<ElementKey, OwlDatatypeProperty> dataProperties = new HashMap<>();
   private SequenceListElement childrenElements = null;

   public ComplexTypeRep(OwlClass owlClass) {
      this.owlClass = owlClass;
      setupProperties();
   }
   
   private void setupProperties() {
      Iterator<OwlProperty> it = owlClass.getDomainOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property.isDatatypeProperty()) {
            OwlDatatypeProperty datatypeProperty = (OwlDatatypeProperty)property;
            dataProperties.put(property.getKey(), datatypeProperty);
            
         } else {
            OwlObjectProperty objectProperty = (OwlObjectProperty)property; 
            objectProperties.put(property.getKey(), objectProperty);
         }
      }
   }

   public ElementKey getKey() {
      return owlClass.getKey();
   }

   public String getName() {
      if (name == null) {
         ElementKey key = owlClass.getKey();
         name = key.getName();
         if (name.endsWith("#")) {
            name = name.substring(0, name.length() - 1);
            int slash = name.lastIndexOf('/');
            if (slash != -1) {
               name = name.substring(slash + 1, name.length());
            }
         }
      }
      return name;
   }
   
   public boolean hasObjectProperties() {
      return !objectProperties.isEmpty();
   }
   
   public Map<ElementKey, OwlObjectProperty> getObjectProperties() {
      return objectProperties;
   }
      
   public boolean hasDataProperties() {
      return! dataProperties.isEmpty();
   }   
   
   public Map<ElementKey, OwlDatatypeProperty> getDataProperties() {
      return dataProperties;
   }   

   public boolean hasProperties() {
      return owlClass.hasOwlProperties();
   }

   public ComplexTypeRep getSuperElement() {
      return superElement;
   }
   
   public boolean hasChildrenElements() {
      return childrenElements != null && !childrenElements.getChildren().isEmpty();
   }   
   
   public SequenceListElement getChildrenElements() {
      return childrenElements;
   }

   public boolean isTopElement() {
      return superElement == null;
   }
   
   public boolean isPackage() {
      return owlClass.isPackage();
   }   
   
   public boolean isEmpty() {
      return !isPackage() && !owlClass.hasSubClasses() && ! owlClass.hasOwlProperties();
   }

   public OwlClass getOwlClass() {
      return owlClass;
   }

   public String getAnnotation() {
      String desc = owlClass.getDescription();
      return desc;
   }

   public void setSuperElement(ComplexTypeRep superElement) {
      this.superElement = superElement;
      if (superElement.childrenElements == null) {
         superElement.childrenElements = new SequenceListElement(getKey(), SequenceListElement.CHILDREN_SEQUENCE);
      }
      SequenceClassElement sequence = new SequenceClassElement(this, true);
      superElement.childrenElements.addChild(sequence);
   }

   public void setUp(XSDModel model) {
      // check properties
      Iterator<OwlProperty> it = owlClass.getDomainOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property.isObjectProperty()) {
            OwlObjectProperty objectProperty = (OwlObjectProperty) property;
            Map<ElementKey, OwlRestriction> range = objectProperty.getRange();
            Iterator<OwlRestriction> it2 = range.values().iterator();
            while (it2.hasNext()) {
               OwlRestriction restriction = it2.next();
               OwlClass theClass = restriction.getOwlClass();
               Map<ElementKey, OwlClass> theDomain = SchemaUtils.inRangeOf(theClass, owlClass);
               SequenceClassElement sequence;
               if (theDomain.size() > 1) {
                  sequence = new SequenceClassElement(model.getComplexType(theClass.getKey()), true);
               } else {
                  sequence = new SequenceClassElement(model.getComplexType(theClass.getKey()), false);
                  ComplexTypeRep otherRep = model.getComplexType(theClass.getKey());
                  otherRep.superElement = this;
               }
               if (childrenElements == null) {
                  childrenElements = new SequenceListElement(getKey(), SequenceListElement.CHILDREN_SEQUENCE);
               }
               if (restriction instanceof OwlCardinalityRestriction) {
                  OwlCardinalityRestriction cardRestriction = (OwlCardinalityRestriction) restriction;
                  sequence.setCardinality(cardRestriction.getCardinality());
               } else if (restriction instanceof OwlQualifiedCardinalityRestriction) {
                  OwlQualifiedCardinalityRestriction cardRestriction = (OwlQualifiedCardinalityRestriction) restriction;
                  sequence.setCardinality(cardRestriction.getCardinality());
               } else if (restriction instanceof OwlMinCardinalityRestriction) {
                  OwlMinCardinalityRestriction cardRestriction = (OwlMinCardinalityRestriction) restriction;
                  sequence.setMinimum(cardRestriction.getMinCardinality());
               } else if (restriction instanceof OwlMinQualifiedCardinalityRestriction) {
                  OwlMinQualifiedCardinalityRestriction cardRestriction = (OwlMinQualifiedCardinalityRestriction) restriction;
                  sequence.setMinimum(cardRestriction.getMinCardinality());
               } else if (restriction instanceof OwlMaxCardinalityRestriction) {
                  OwlMaxCardinalityRestriction cardRestriction = (OwlMaxCardinalityRestriction) restriction;
                  sequence.setMinimum(cardRestriction.getMaxCardinality());
               } else if (restriction instanceof OwlMaxQualifiedCardinalityRestriction) {
                  OwlMaxQualifiedCardinalityRestriction cardRestriction = (OwlMaxQualifiedCardinalityRestriction) restriction;
                  sequence.setMinimum(cardRestriction.getMaxCardinality());
               }
               childrenElements.addChild(sequence);
            }
         }
      }
   }
}
