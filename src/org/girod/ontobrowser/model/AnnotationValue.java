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
 * The value of an annotation on an element.
 *
 * @version 0.7
 */
public interface AnnotationValue {
   public static final short LITERAL_TYPE = 0;
   public static final short ELEMENT_TYPE = 1;
   public static final short URI_TYPE = 2;

   /**
    * Return true if the annotation value is an URI.
    *
    * @return true if the annotation value is an URI
    */
   public short getType();

   /**
    * Return the annotation value as an URI.
    *
    * @return the annotation value as an URI
    */
   public default URIAnnotationValue asURI() {
      return null;
   }

   /**
    * Return the annotation value as an URI.
    *
    * @return the annotation value as an URI
    */
   public default ElementAnnotationValue asElement() {
      return null;
   }

   /**
    * Return the annotation value as a Literal.
    *
    * @return the annotation value as a Literal
    */
   public default LiteralAnnotationValue asLiteral() {
      return null;
   }

   /**
    * Return the literal of the annotation value. If the annotation is an URI, the String value of the URI will be returned.
    *
    * @return the literal of the annotation value
    */
   public String getLiteral();

   public static class URIAnnotationValue implements AnnotationValue {
      private final URI uri;

      public URIAnnotationValue(URI uri) {
         this.uri = uri;
      }

      /**
       * Return {@link #URI_TYPE}.
       *
       * @return {@link #URI_TYPE}
       */
      @Override
      public short getType() {
         return URI_TYPE;
      }

      public URI getURI() {
         return uri;
      }

      @Override
      public String toString() {
         return getLiteral();
      }

      @Override
      public String getLiteral() {
         return uri.toString();
      }

      @Override
      public URIAnnotationValue asURI() {
         return this;
      }
   }

   public static class ElementAnnotationValue implements AnnotationValue {
      private final NamedOwlElement element;

      public ElementAnnotationValue(NamedOwlElement element) {
         this.element = element;
      }

      @Override
      public String toString() {
         return getLiteral();
      }

      public NamedOwlElement getElement() {
         return element;
      }

      @Override
      public String getLiteral() {
         return element.toString();
      }

      /**
       * Return {@link #LITERAL_TYPE}.
       *
       * @return {@link #LITERAL_TYPE}
       */
      @Override
      public short getType() {
         return ELEMENT_TYPE;
      }

      @Override
      public ElementAnnotationValue asElement() {
         return this;
      }
   }

   public static class LiteralAnnotationValue implements AnnotationValue {
      private final String literal;

      public LiteralAnnotationValue(String literal) {
         this.literal = literal;
      }

      @Override
      public String toString() {
         return getLiteral();
      }

      @Override
      public String getLiteral() {
         return literal;
      }

      /**
       * Return {@link #LITERAL_TYPE}.
       *
       * @return {@link #LITERAL_TYPE}
       */
      @Override
      public short getType() {
         return LITERAL_TYPE;
      }

      @Override
      public LiteralAnnotationValue asLiteral() {
         return this;
      }
   }
}
