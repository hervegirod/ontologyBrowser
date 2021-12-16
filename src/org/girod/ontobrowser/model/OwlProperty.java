/*
Copyright (c) 2021, Hervé Girod
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
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.model.restriction.UnrestrictedOwlRestriction;

/**
 * An abstract OwlProperty.
 * @param <T> the property type
 * @since 0.1
 */
public abstract class OwlProperty<T> extends NamedOwlElement {
   private final Map<ElementKey, OwlRestriction> domain = new HashMap<>();
   private int minCardinality = 0;
   private int maxCardinality = -1;
   private T ontProperty = null;

   public OwlProperty(T ontProperty, String namespace, String name) {
      super(namespace, name);
      this.ontProperty = ontProperty;
   }

   public T getProperty() {
      return ontProperty;
   }

   public void addDomain(OwlRestriction restriction) {
      domain.put(restriction.getKey(), restriction);
   }

   public void addDomain(ElementKey key) {
      domain.put(key, new UnrestrictedOwlRestriction(key));
   }

   public Map<ElementKey, OwlRestriction> getDomain() {
      return domain;
   }

   @Override
   public OwlProperty clone() {
      Object o = super.clone();
      return (OwlProperty) o;
   }

   public boolean isDatatypeProperty() {
      return false;
   }

   public boolean isObjectProperty() {
      return false;
   }

   public void setMinCardinality(int minCardinality) {
      this.minCardinality = minCardinality;
   }

   public int getMinCardinality() {
      return minCardinality;
   }

   public void setMaxCardinality(int maxCardinality) {
      this.maxCardinality = maxCardinality;
   }

   public int getMaxCardinality() {
      return maxCardinality;
   }
}
