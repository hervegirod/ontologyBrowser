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
package org.girod.ontobrowser.script;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @since 0.8
 */
public class DefaultScriptImports {
   private static DefaultScriptImports theImports = null;
   private Set<String> set = null;

   private DefaultScriptImports() {
   }

   private Set<String> getImportsImpl() {
      if (set != null) {
         return set;
      }
      set = new HashSet<>();
      set.add("org.girod.ontobrowser.script.*");
      set.add("java.util.*");
      set.add("org.girod.ontobrowser.model.*");
      set.add("org.girod.ontobrowser.model.restriction.*");      
      set.add("org.scripthelper.context.*");
      return set;
   }

   public static Set<String> getImports() {
      if (theImports == null) {
         theImports = new DefaultScriptImports();
      }
      return theImports.getImportsImpl();

   }

}
