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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a named Owl element.
 *
 * @version 0.7
 */
public abstract class NamedOwlElement extends AnnotatedElement implements Cloneable {

   protected String namespace;
   protected final String name;

   protected final String prefix;
   private ElementKey key = null;
   private final Map<ElementKey, OwlClass> inEquivalentExpressions = new HashMap<>();

   public NamedOwlElement(String namespace, String name, String prefix) {
      super();
      this.namespace = namespace;
      this.name = name;
      this.prefix = prefix;
   }

   public NamedOwlElement(String namespace, String name) {
      super();
      this.namespace = namespace;
      this.name = name;
      this.prefix = null;
   }

   public String getPrefix() {
      return prefix;
   }

   public URI toURI() {
      return getKey().toURI();
   }

   @Override
   public String toString() {
      if (prefix == null) {
         return name;
      } else {
         return prefix + ":" + name;
      }
   }

   void addInEquivalentExpression(OwlClass owlClass) {
      inEquivalentExpressions.put(owlClass.getKey(), owlClass);
   }

   /**
    * Return the owl classes in which this element is used in equivalent expressions.
    *
    * @return the owl classes
    */
   public Map<ElementKey, OwlClass> getInEquivalentExpressions() {
      return inEquivalentExpressions;
   }

   /**
    * Return true if this element is used in equivalent expressions.
    *
    * @return true if this element is used in equivalent expressions
    */
   public boolean isInEquivalentExpressions() {
      return !inEquivalentExpressions.isEmpty();
   }

   /**
    * Return the type of the element.
    *
    * @return the element type
    */
   public String getElementType() {
      if (this instanceof OwlClass) {
         return ElementTypes.CLASS;
      } else if (this instanceof OwlProperty) {
         return ElementTypes.PROPERTY;
      } else if (this instanceof OwlIndividual) {
         return ElementTypes.INDIVIDUAL;
      } else if (this instanceof OwlAnnotation) {
         return ElementTypes.ANNOTATION;
      } else {
         return null;
      }
   }

   @Override
   public NamedOwlElement clone() {
      try {
         Object o = super.clone();
         return (NamedOwlElement) o;
      } catch (CloneNotSupportedException ex) {
         return null;
      }
   }

   /**
    * Return the element name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Return the element displayed name.
    *
    * @return the element displayed name.
    */
   public String getDisplayedName() {
      if (label != null) {
         return label;
      } else {
         return name;
      }
   }

   /**
    * Return the element namespace.
    *
    * @return the namespace
    */
   public String getNamespace() {
      return namespace;
   }

   /**
    * Return the element key.
    *
    * @return the key
    */
   public ElementKey getKey() {
      if (key == null) {
         key = new ElementKey(namespace, name);
      }
      return key;
   }
}
