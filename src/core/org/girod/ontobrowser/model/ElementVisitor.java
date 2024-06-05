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
 * The interface that all Visitors that visits Owl Schemas must implement.
 *
 * @since 0.8
 */
public interface ElementVisitor {
   /**
    * Visit an Element. Return true by default.
    *
    * @param schema the schema
    * @return true if the children of the elements should also be visited.
    */
   public default boolean visit(OwlSchema schema) {
      return true;
   }   
   
   /**
    * Visit an Element. Return true by default.
    *
    * @param elt the element
    * @return true if the children of the elements should also be visited.
    */
   public default boolean visit(NamedElement elt) {
      if (elt instanceof OwlClass) {
         visitImpl((OwlClass) elt);
      } else if (elt instanceof OwlProperty) {
         visitImpl((OwlProperty) elt);
      } else if (elt instanceof OwlIndividual) {
         visitImpl((OwlIndividual) elt);
      } else if (elt instanceof PropertyValue) {
         visitImpl((PropertyValue) elt);         
      }
      return true;
   }

   /**
    * Visit an OwlClass. Do nothing by default.
    *
    * @param theClass the OwlClass.
    */
   public default void visitImpl(OwlClass theClass) {
   }
   
   /**
    * Visit an OwlProperty. Do nothing by default.
    *
    * @param theProperty the OwlProperty.
    */
   public default void visitImpl(OwlProperty theProperty) {
   }   
   
   /**
    * Visit an Individual. Do nothing by default.
    *
    * @param individual the Individual.
    */
   public default void visitImpl(OwlIndividual individual) {
   }   
   
   /**
    * Visit a property value. Do nothing by default.
    *
    * @param value the property value.
    */
   public default void visitImpl(PropertyValue value) {
   }    
}
