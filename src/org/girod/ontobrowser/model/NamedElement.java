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
 * A named element.
 *
 * @since 0.8
 */
public interface NamedElement {
   /**
    * Return the element prefix.
    *
    * @return the prefix
    */
   public String getPrefix();

   /**
    * Return the element URI.
    *
    * @return the URI
    */
   public URI toURI();

   /**
    * Return the element name.
    *
    * @return the name
    */
   public String getName();

   /**
    * Return the element displayed name.
    *
    * @return the element displayed name.
    */
   public String getDisplayedName();

   /**
    * Return the element prefixed displayed name.
    *
    * @return the element prefixed displayed name.
    */
   public String getPrefixedDisplayedName();

   /**
    * Return the element namespace.
    *
    * @return the namespace
    */
   public String getNamespace();

   /**
    * Return the element key.
    *
    * @return the key
    */
   public ElementKey getKey();

   /**
    * Used to apply the Visitor pattern with this Element. The typical behavior is to call for
    * the <i>Visit</i> method of the Visitor, and to call the <i>accept</i> method on the Element children.
    *
    * @param visitor the Visitor
    */
   public default void accept(ElementVisitor visitor) {
      visitor.visit(this);
   }

   /**
    * Return the type of the element.
    *
    * @return the element type
    */
   public default String getElementType() {
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
}
