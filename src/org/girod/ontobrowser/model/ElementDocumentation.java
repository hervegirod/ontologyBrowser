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

/**
 * Represents any element documentation.
 *
 * @version 0.7
 */
public class ElementDocumentation {
   private String desc = null;
   private String comments = null;
   private String label = null;
   private String versionInfo = null;
   private AnnotationValue isDefinedBy = null;
   private AnnotationValue seeAlso = null;

   public ElementDocumentation() {
   }
   
   /**
    * Set the description.
    *
    * @param desc the description
    */
   public void setDescription(String desc) {
      this.desc = desc;
   }

   /**
    * Return true if the element has a description.
    *
    * @return true if the element has a description
    */
   public boolean hasDescription() {
      return desc != null;
   }   
   
   /**
    * Return the description.
    *
    * @return the description
    */
   public String getDescription() {
      return desc;
   }   
   
   /**
    * Return true if the element has a description or comments.
    *
    * @return true if the element has a description or comments
    */
   public boolean hasDescriptionOrComments() {
      if (desc != null) {
         return true;
      } else {
         return comments != null;
      }
   }   
   
   /**
    * Return the description or comments.
    *
    * @return the description or comments
    */
   public String getDescriptionOrComments() {
      if (desc != null) {
         return desc;
      } else {
         return comments;
      }
   }     

   /**
    * Set the comment.
    *
    * @param comments the comments
    */
   public void setComments(String comments) {
      this.comments = comments;
   }

   /**
    * Return true if the element has comments.
    *
    * @return true if the element has comments
    */
   public boolean hasComments() {
      return comments != null;
   }

   /**
    * Return the comments.
    *
    * @return the comments
    */
   public String getComments() {
      return comments;
   }

   /**
    * Set the version information.
    *
    * @param versionInfo the version information
    */
   public void setVersionInfo(String versionInfo) {
      this.versionInfo = versionInfo;
   }

   /**
    * Return the version information.
    *
    * @return the version information
    */
   public String getVersionInfo() {
      return versionInfo;
   }
   
   /**
    * Set the label information.
    *
    * @param label the label information
    */
   public void setLabel(String label) {
      this.label = label;
   }
   
   /**
    * Return true if the element has a label.
    *
    * @return true if the element has a label
    */
   public boolean hasLabel() {
      return label != null;
   }    

   /**
    * Return the label information.
    *
    * @return the label information
    */
   public String getLabel() {
      return label;
   }   
   
   /**
    * Set the isDefinedBy literal.
    *
    * @param isDefinedBy the isDefinedBy literal
    */
   public void setIsDefinedBy(AnnotationValue isDefinedBy) {
      this.isDefinedBy = isDefinedBy;
   }

   /**
    * Return the isDefinedBy literal.
    *
    * @return the isDefinedBy literal
    */
   public AnnotationValue getIsDefinedBy() {
      return isDefinedBy;
   }   
   
   /**
    * Set the seeAlso information.
    *
    * @param seeAlso the seeAlso information
    */
   public void setSeeAlso(AnnotationValue seeAlso) {
      this.seeAlso = seeAlso;
   }

   /**
    * Return the seeAlso information.
    *
    * @return the seeAlso information
    */
   public AnnotationValue getSeeAlso() {
      return seeAlso;
   }   
     
}
