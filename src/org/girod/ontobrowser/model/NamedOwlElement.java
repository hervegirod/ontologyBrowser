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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a named Owl element.
 *
 * @version 0.6
 */
public abstract class NamedOwlElement implements Cloneable {
   private static final ElementKey DUBLINCORE_DESCRIPTION = ElementKey.create("http://purl.org/dc/terms/", "description");
   private static final ElementKey COMMENT = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "comment");
   private static final ElementKey SEE_ALSO = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "seeAlso");
   private static final ElementKey DEFINED_BY = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "isDefinedBy");
   private static final ElementKey VERSION_INFO = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "versionInfo");
   protected final String namespace;
   protected final String name;
   protected final String prefix;
   private ElementKey key = null;
   private final Map<ElementKey, AnnotationValue> annotations = new HashMap<>();
   private ElementDocumentation elementDoc = null;

   public NamedOwlElement(String namespace, String name, String prefix) {
      this.namespace = namespace;
      this.name = name;
      this.prefix = prefix;
   }

   public NamedOwlElement(String namespace, String name) {
      this.namespace = namespace;
      this.name = name;
      this.prefix = null;
   }

   public String getPrefix() {
      return prefix;
   }

   @Override
   public String toString() {
      if (prefix == null) {
         return name;
      } else {
         return prefix + ":" + name;
      }
   }

   /**
    * Return the element dopcumentation.
    *
    * @return the element dopcumentation
    */
   public ElementDocumentation getDocumentation() {
      return elementDoc;
   }

   private ElementDocumentation createDocumentation() {
      if (elementDoc == null) {
         elementDoc = new ElementDocumentation();
      }
      return elementDoc;
   }

   public void addAnnotation(ElementKey key, AnnotationValue value) {
      annotations.put(key, value);
      if (key.equals(DUBLINCORE_DESCRIPTION)) {
         createDocumentation().setDescription(value.toString());
      }
   }

   public Map<ElementKey, AnnotationValue> getAnnotations() {
      return annotations;
   }

   /**
    * Set the element description.
    *
    * @param desc the description
    */
   public void setDescription(String desc) {
      if (desc != null) {
         createDocumentation().setDescription(desc);
      }
   }

   /**
    * Set the element comments.
    *
    * @param comments comments the comments
    */
   public void setComments(String comments) {
      if (comments != null) {
         annotations.put(COMMENT, new AnnotationValue.LiteralAnnotationValue(comments));
         createDocumentation().setComments(comments);
      }
   }

   /**
    * Set the element isDefinedBy.
    *
    * @param isDefinedBy the isDefinedBy
    */
   public void setIsDefinedBy(AnnotationValue isDefinedBy) {
      if (isDefinedBy != null) {
         annotations.put(DEFINED_BY, isDefinedBy);
         createDocumentation().setIsDefinedBy(isDefinedBy);
      }
   }

   /**
    * Set the element versionInfo.
    *
    * @param versionInfo the versionInfo
    */
   public void setVersionInfo(String versionInfo) {
      if (versionInfo != null) {
         annotations.put(VERSION_INFO, new AnnotationValue.LiteralAnnotationValue(versionInfo));
         createDocumentation().setVersionInfo(versionInfo);
      }
   }

   /**
    * Set the element seeAlso.
    *
    * @param seeAlso the seeAlso
    */
   public void setSeeAlso(AnnotationValue seeAlso) {
      if (seeAlso != null) {
         annotations.put(SEE_ALSO, seeAlso);
         createDocumentation().setSeeAlso(seeAlso);
      }
   }

   /**
    * Return the element description.
    *
    * @return the description
    */
   public String getDescription() {
      if (elementDoc == null) {
         return null;
      } else {
         return elementDoc.getDescription();
      }
   }

   /**
    * Return the element description or comments.
    *
    * @return the description or comments
    */
   public String getDescriptionOrComments() {
      if (elementDoc == null) {
         return null;
      } else {
         return elementDoc.getDescriptionOrComments();
      }
   }

   /**
    * Return the element comments.
    *
    * @return the comments
    */
   public String getComments() {
      if (elementDoc == null) {
         return null;
      } else {
         return elementDoc.getComments();
      }
   }
   
   /**
    * Return true if the element has comments.
    *
    * @return true if the element has comments
    */
   public boolean hasDescriptionOrComments() {
      if (elementDoc == null) {
         return false;
      } else {
         return elementDoc.hasDescriptionOrComments();
      }
   }   

   /**
    * Return true if the element has comments.
    *
    * @return true if the element has comments
    */
   public boolean hasComments() {
      if (elementDoc == null) {
         return false;
      } else {
         return elementDoc.hasComments();
      }
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
