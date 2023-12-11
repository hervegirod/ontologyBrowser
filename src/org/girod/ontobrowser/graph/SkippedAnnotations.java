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
package org.girod.ontobrowser.graph;

import java.util.HashSet;
import java.util.Set;
import org.girod.ontobrowser.model.ElementKey;

/**
 *
 * @since 0.7
 */
public class SkippedAnnotations {
   private static SkippedAnnotations annotationsInstance = null;
   private final Set<ElementKey> skipped = new HashSet<>();
   private final Set<ElementKey> comments = new HashSet<>();

   private SkippedAnnotations() {
      setup();
   }

   public static SkippedAnnotations getInstance() {
      if (annotationsInstance == null) {
         annotationsInstance = new SkippedAnnotations();
      }
      return annotationsInstance;
   }

   public boolean isComment(ElementKey key) {
      return comments.contains(key);
   }

   public boolean isSkipped(ElementKey key) {
      return skipped.contains(key);
   }

   public boolean isSkippedForClass(ElementKey key) {
      return skipped.contains(key) || comments.contains(key);
   }

   private void setup() {
      String owl = "http://www.w3.org/2002/07/owl#";
      addSkipped(owl, "inverseOf");
      addSkipped(owl, "equivalentClass");

      String rdfSchema = "http://www.w3.org/2000/01/rdf-schema#";
      addSkipped(rdfSchema, "domain");
      addSkipped(rdfSchema, "range");
      addSkipped(rdfSchema, "subClassOf");

      String rdfsyntax = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
      addSkipped(rdfsyntax, "type");
   }

   private void addComment(String namespace, String name) {
      ElementKey key = ElementKey.create(namespace, name);
      comments.add(key);
   }

   private void addSkipped(String namespace, String name) {
      ElementKey key = ElementKey.create(namespace, name);
      skipped.add(key);
   }
}
