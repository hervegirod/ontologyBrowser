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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a named Owl element.
 *
 * @version 0.9
 * @param <T> the element type
 */
public abstract class NamedOwlElement<T extends NamedOwlElement> extends AnnotatedElement implements NamedElement, Cloneable {
   /**
    * The element namespace.
    */
   protected String namespace;
   /**
    * The element name.
    */
   protected final String name;
   /**
    * The element prefix.
    */
   protected String prefix = null;
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
   }

   /**
    * Set the element prefix.
    *
    * @param prefix the prefix
    */
   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   /**
    * Return the element prefix.
    *
    * @return the prefix
    */
   @Override
   public String getPrefix() {
      return prefix;
   }

   /**
    * Return the element URI.
    *
    * @return the URI
    */
   @Override
   public URI toURI() {
      return getKey().toURI();
   }

   /**
    * Return this element alias elements. This does not take into account erquivalent expressions. Return false by default.
    *
    * @return this element alias elements
    */
   public boolean hasAliasElements() {
      return false;
   }

   /**
    * Return the alias elements. Return null by default.
    *
    * @return the alias elements
    */
   public Map<ElementKey, T> getAliasElements() {
      return null;
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
   @Override
   public String getName() {
      return name;
   }

   /**
    * Return the element displayed name.
    *
    * @return the element displayed name.
    */
   @Override
   public String getDisplayedName() {
      if (label != null) {
         return label;
      } else {
         return name;
      }
   }

   /**
    * Return the element prefixed displayed name.
    *
    * @return the element prefixed displayed name.
    */
   @Override
   public String getPrefixedDisplayedName() {
      String theName = getDisplayedName();
      if (prefix == null) {
         return theName;
      } else {
         return prefix + ":" + theName;
      }
   }

   /**
    * Return the element namespace.
    *
    * @return the namespace
    */
   @Override
   public String getNamespace() {
      return namespace;
   }

   /**
    * Return the element key.
    *
    * @return the key
    */
   @Override
   public ElementKey getKey() {
      if (key == null) {
         key = new ElementKey(namespace, name);
      }
      return key;
   }
}
