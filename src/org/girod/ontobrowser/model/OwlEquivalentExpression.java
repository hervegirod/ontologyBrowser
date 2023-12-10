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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.ontology.BooleanClassDescription;
import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.UnionClass;

/**
 * Represents an union or Intersection for an equivalent expression.
 *
 * @since 0.7
 */
public class OwlEquivalentExpression {
   /**
    * The Intersection type.
    */
   public static final short INTERSECTION = 0;
   /**
    * The Union type.
    */
   public static final short UNION = 1;
   /**
    * The Enumeration type.
    */
   public static final short ENUMERATION = 2;
   private final short type;
   private final OwlClass owlClass;
   private final List<NamedOwlElement> elementsList = new ArrayList<>();
   private final Map<ElementKey, NamedOwlElement> elements = new HashMap<>();

   public OwlEquivalentExpression(OwlClass owlClass, EnumeratedClass enumClass) {
      this.owlClass = owlClass;
      this.type = ENUMERATION;
   }

   public OwlEquivalentExpression(OwlClass owlClass, BooleanClassDescription expression) {
      this.owlClass = owlClass;
      if (expression instanceof UnionClass) {
         this.type = UNION;
      } else {
         this.type = INTERSECTION;
      }
   }

   /**
    * Return the type of the equivalent class.
    *
    * @return the type
    */
   public short getType() {
      return type;
   }

   /**
    * Return the Owl class which uses this expression.
    *
    * @return the Owl class
    */
   public OwlClass getOwlClass() {
      return owlClass;
   }

   /**
    * Add an element used in the expression.
    *
    * @param element the element
    */
   public void addElement(NamedOwlElement element) {
      if (!elements.containsKey(element.getKey())) {
         elements.put(element.getKey(), element);
         elementsList.add(element);
      }
   }

   /**
    * Return the list of elements used in the expression.
    *
    * @return the list of elements
    */
   public List<NamedOwlElement> getElementsList() {
      return elementsList;
   }

   /**
    * Return the Map of elements used in the expression.
    *
    * @return the Map of elements
    */
   public Map<ElementKey, NamedOwlElement> getElements() {
      return elements;
   }
}
