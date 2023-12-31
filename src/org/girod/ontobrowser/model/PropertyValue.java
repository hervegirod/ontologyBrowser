/*
Copyright (c) 2023 Hervé Girod
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

/**
 * An abstract property value.
 *
 * @param <P> the property type
 * @since 0.8
 */
public abstract class PropertyValue<P extends OwlProperty> implements NamedElement {
   /**
    * The property.
    */
   protected final P property;
   /**
    * The individual source.
    */
   protected final OwlIndividual source;

   public PropertyValue(P property, OwlIndividual source) {
      this.property = property;
      this.source = source;
   }

   /**
    * Return the property element key.
    *
    * @return the key
    */
   @Override
   public ElementKey getKey() {
      return property.getKey();
   }

   /**
    * Return the individual which is the source of the value.
    *
    * @return ther individual
    */
   public OwlIndividual getSource() {
      return source;
   }

   /**
    * Return the property element.
    *
    * @return the property
    */
   public P getProperty() {
      return property;
   }

   /**
    * Return the property element prefix.
    *
    * @return the prefix
    */
   @Override
   public String getPrefix() {
      return property.getPrefix();
   }

   /**
    * Return the property element prefixed displayed name.
    *
    * @return the prefixed displayed name.
    */
   @Override
   public String getPrefixedDisplayedName() {
      String theName = getDisplayedName();
      String prefix = getPrefix();
      if (prefix == null) {
         return theName;
      } else {
         return prefix + ":" + theName;
      }
   }

   /**
    * Return the property element URI.
    *
    * @return the URI
    */
   @Override
   public URI toURI() {
      return property.toURI();
   }

   /**
    * Return the property element name.
    *
    * @return the name
    */
   @Override
   public String getName() {
      return property.getName();
   }

   /**
    * Return the property element displayed name.
    *
    * @return the displayed name
    */
   @Override
   public String getDisplayedName() {
      return property.getDisplayedName();
   }

   /**
    * Return the property element namespace.
    *
    * @return the namespace
    */
   @Override
   public String getNamespace() {
      return property.getNamespace();
   }
}
