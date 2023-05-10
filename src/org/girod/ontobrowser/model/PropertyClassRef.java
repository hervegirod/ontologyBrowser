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

import java.util.Objects;

/**
 * Represents the reference to a class property.
 *
 * @version 0.4
 */
public class PropertyClassRef implements Cloneable {
   private final ElementKey domainKey;
   private final ElementKey propertyKey;

   public PropertyClassRef(ElementKey domainKey, ElementKey propertyKey) {
      this.domainKey = domainKey;
      this.propertyKey = propertyKey;
   }

   @Override
   protected PropertyClassRef clone() throws CloneNotSupportedException {
      try {
         Object o = super.clone();
         return (PropertyClassRef) o;
      } catch (CloneNotSupportedException ex) {
         return null;
      }
   }

   @Override
   public String toString() {
      return domainKey.toString() + "#" + propertyKey.toString();
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 73 * hash + Objects.hashCode(this.domainKey);
      hash = 73 * hash + Objects.hashCode(this.propertyKey);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final PropertyClassRef other = (PropertyClassRef) obj;
      if (!Objects.equals(this.domainKey, other.domainKey)) {
         return false;
      }
      if (!Objects.equals(this.propertyKey, other.propertyKey)) {
         return false;
      }
      return true;
   }

   /**
    * The domain key.
    *
    * @return the domain key
    */
   public ElementKey getDomainKey() {
      return domainKey;
   }

   /**
    * The property key.
    *
    * @return the property key
    */
   public ElementKey getPropertyKey() {
      return propertyKey;
   }
}
