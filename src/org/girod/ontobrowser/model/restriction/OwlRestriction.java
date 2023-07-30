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
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.OntClass;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * An owl restriction specifies an element in the domain or range of a property.
 *
 * @version 0.5
 */
public abstract class OwlRestriction {
   /**
    * The key of the Owl Class which is refered by the restriction.
    */
   protected ElementKey key = null;
   /**
    * The Owl Class which is refered by the restriction.
    */
   private OwlClass theClass = null;

   public OwlRestriction() {
   }

   /**
    * Constructor.
    *
    * @param clazz the owl class of the element which is refered by the restriction
    */
   public OwlRestriction(OntClass clazz) {
      String localName = clazz.getLocalName();
      String namespace = clazz.getNameSpace();
      this.key = new ElementKey(namespace, localName);
   }

   /**
    * Constructor.
    *
    * @param key the key of the element which is refered by the restriction
    */
   public OwlRestriction(ElementKey key) {
      this.key = key;
   }

   /**
    * Return the key of the Owl Class which is refered by the restriction.
    *
    * @return the key
    */
   public ElementKey getKey() {
      return key;
   }

   /**
    * Return the Owl Class which is refered by the restriction.
    *
    * @return the Owl Class
    */
   public OwlClass getOwlClass() {
      return theClass;
   }

   /**
    * Setup the restriction.
    *
    * @param schema the schema
    */
   public void setup(OwlSchema schema) {
      if (schema.hasOwlClass(key)) {
         theClass = schema.getOwlClass(key);
      }
   }

}
