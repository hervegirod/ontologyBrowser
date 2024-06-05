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
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Resource;
import org.girod.ontobrowser.model.ElementKey;

/**
 * An OwlRestriction which has effectively a restriction in the OntModel.
 *
 * @since 0.1
 * @param <R> the restriction type
 */
public class RestrictedOwlRestriction<R extends Restriction> extends OwlRestriction {
   /**
    * The restriction.
    */
   protected final R restriction;

   public RestrictedOwlRestriction(R restriction) {
      this.restriction = restriction;
   }

   /**
    * Compute the key of the element referred by the restriction.
    *
    * @param resource the resource
    */
   protected void computeKey(Resource resource) {
      String localName = resource.getLocalName();
      String nameSpace = resource.getNameSpace();
      key = new ElementKey(nameSpace, localName);
   }

   /**
    * Return the effective Owl restriction in the OntModel.
    *
    * @return the Owl restriction
    */   
   public R getRestriction() {
      return restriction;
   }
}
