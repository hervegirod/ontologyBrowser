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
import org.apache.jena.ontology.ObjectProperty;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.model.restriction.UnrestrictedOwlRestriction;

/**
 * Represents an Owl Object property.
 *
 * @version 0.4
 */
public class OwlObjectProperty extends OwlProperty<ObjectProperty> {
   private final Map<ElementKey, OwlRestriction> range = new HashMap<>();
   private OwlObjectProperty inverse = null;

   public OwlObjectProperty(ObjectProperty ontProperty, String namespace, String name) {
      super(ontProperty, namespace, name);
   }

   public void setInverseProperty(OwlObjectProperty inverse) {
      this.inverse = inverse;
      if (inverse.getInverse() == null) {
         inverse.setInverseProperty(this);
      }
   }

   public OwlObjectProperty getInverse() {
      return inverse;
   }

   public void addRange(OwlRestriction restriction) {
      range.put(restriction.getKey(), restriction);
   }

   public void addRange(ElementKey key) {
      range.put(key, new UnrestrictedOwlRestriction(key));
   }

   public Map<ElementKey, OwlRestriction> getRange() {
      return range;
   }

   @Override
   public boolean isObjectProperty() {
      return true;
   }

   @Override
   public OwlObjectProperty clone() {
      Object o = super.clone();
      return (OwlObjectProperty) o;
   }
}
