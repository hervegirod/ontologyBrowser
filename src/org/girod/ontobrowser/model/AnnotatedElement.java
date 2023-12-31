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

import java.util.HashMap;
import java.util.Map;

/**
 * An annotated element.
 *
 * @since 0.7
 */
public abstract class AnnotatedElement {
   public static final ElementKey DUBLINCORE_DESCRIPTION = ElementKey.create("http://purl.org/dc/terms/", "description");
   public static final ElementKey COMMENT = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "comment");
   public static final ElementKey SEE_ALSO = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "seeAlso");
   public static final ElementKey DEFINED_BY = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "isDefinedBy");
   public static final ElementKey VERSION_INFO = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "versionInfo");
   private static final ElementKey LABEL = ElementKey.create("http://www.w3.org/2000/01/rdf-schema/", "label");
   public final Map<ElementKey, AnnotationValue> annotations = new HashMap<>();
   private ElementDocumentation elementDoc = null;
   /**
    * The element label.
    */
   protected String label = null;

   public AnnotatedElement() {
   }

   /**
    * Return the element documentation.
    *
    * @return the element documentation
    */
   public ElementDocumentation getDocumentation() {
      return elementDoc;
   }

   protected ElementDocumentation createDocumentation() {
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

   /**
    * Return true if there is an annotation for a specific key.
    *
    * @param key the annotation key
    * @return true if there is an annotation for the key
    */
   public boolean hasAnnotation(ElementKey key) {
      return annotations.containsKey(key);
   }

   /**
    * Return the annotation of a specific key.
    *
    * @param key the annotation key
    * @return the annotation of a specific key
    */
   public AnnotationValue getAnnotation(ElementKey key) {
      return annotations.get(key);
   }

   /**
    * Return the element annotations.
    *
    * @return the element annotations
    */
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
    * Set the element label.
    *
    * @param label the label
    */
   public void setLabel(String label) {
      if (label != null) {
         this.label = label;
         annotations.put(LABEL, new AnnotationValue.LiteralAnnotationValue(label));
         createDocumentation().setLabel(label);
      }
   }

   /**
    * Return the element label.
    *
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * Return true if the element has a label.
    *
    * @return true if the element has a abel
    */
   public boolean hasLabel() {
      return label != null;
   }

}
