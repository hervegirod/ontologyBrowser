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
package org.girod.ontobrowser.gui;

import java.util.Iterator;
import java.util.Map;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.restriction.OwlRestriction;

/**
 * A property bridge used in the class properties window.
 *
 * @since 0.5
 */
public class PropertyBridge {
   private final OwlProperty property;
   private final boolean fromDomain;

   public PropertyBridge(OwlProperty property, boolean from) {
      this.property = property;
      this.fromDomain = from;
   }
   
   public OwlProperty getOwlProperty() {
      return property;
   }

   @Override
   public String toString() {
      if (isObjectProperty()) {
         OwlObjectProperty objectProperty =(OwlObjectProperty)property;
         StringBuilder buf = new StringBuilder();
         buf.append(property.getName());
         Map<ElementKey, OwlRestriction> restrictions;
         if (fromDomain) {
            buf.append(": to ");
            restrictions = objectProperty.getRange();
         } else {
            buf.append(": from ");
            restrictions = objectProperty.getDomain();
         }
         Iterator<ElementKey> it = restrictions.keySet().iterator();
         while (it.hasNext()) {
            ElementKey key = it.next();
            buf.append(key.toString());
            if (it.hasNext()) {
               buf.append(", ");
            }
         }
         return buf.toString();
      } else {
         OwlDatatypeProperty datatypeproperty = (OwlDatatypeProperty)property;
         StringBuilder buf = new StringBuilder();
         buf.append(property.getName());
         buf.append(": ");
         Iterator<OwlDatatype> it = datatypeproperty.getTypes().values().iterator();
         while (it.hasNext()) {
            OwlDatatype datatype = it.next();
            buf.append(datatype.toString());
            if (it.hasNext()) {
               buf.append(", ");
            }            
         }
         return buf.toString();
      }
   }

   public boolean isFromProperty() {
      return fromDomain;
   }

   public boolean isObjectProperty() {
      return property instanceof OwlObjectProperty;
   }
}
