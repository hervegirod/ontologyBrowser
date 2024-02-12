/*
Copyright (c) 2023, 2024 Hervé Girod
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
package org.girod.ontobrowser.gui.tree;

import java.util.Iterator;
import java.util.Map;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * Represents an Owl element represented in its tree.
 *
 * @version 0.11
 */
public class OwlElementRep {
   private final NamedOwlElement element;
   private final String name;
   private final String prefix;
   private final boolean hasDefaultPrefix;
   private final String schemaDefaultPrefix;
   private final boolean allowBold;
   private final boolean isDefaultPrefix;

   public OwlElementRep(NamedOwlElement element, OwlSchema schema, boolean hasDefaultPrefix, boolean allowBold) {
      this.element = element;
      this.name = element.getDisplayedName();
      this.prefix = schema.getPrefix(element.getNamespace());
      this.hasDefaultPrefix = hasDefaultPrefix;
      this.schemaDefaultPrefix = schema.getDefaultPrefix();
      this.allowBold = allowBold;
      this.isDefaultPrefix = hasDefaultPrefix && prefix != null && schemaDefaultPrefix.equals(prefix);
   }

   public boolean isPackage() {
      if (element instanceof OwlClass) {
         return ((OwlClass) element).isPackage();
      } else {
         return false;
      }
   }

   private String getDisplayedName(NamedOwlElement element) {
      String theName = element.getPrefixedDisplayedName();
      if (allowBold) {
         String thePrefix = element.getPrefix();
         boolean isDefaultPrefixForElement = hasDefaultPrefix && thePrefix != null && schemaDefaultPrefix.equals(thePrefix);

         if (!hasDefaultPrefix || isDefaultPrefixForElement) {
            theName = "<b>" + theName + "</b>";
         }
      }
      return theName;
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("<html>");
      String defaultStringName = getDefaultStringName();
      if (allowBold) {
         if (!hasDefaultPrefix || isDefaultPrefix) {
            defaultStringName = "<b>" + defaultStringName + "</b>";
         }
      }
      if (element.hasAliasElements()) {
         Map<ElementKey, ? extends NamedOwlElement> map = element.getAliasElements();
         buf.append(defaultStringName);
         int max = map.size();
         if (max > 2) {
            max = 2;
         }
         Iterator<? extends NamedOwlElement> it = map.values().iterator();
         while (it.hasNext()) {
            NamedOwlElement aliasElement = it.next();
            buf.append(" \u2263 ").append(getDisplayedName(aliasElement));
         }
         buf.append("</html>");
         return buf.toString();
      } else {
         buf.append(defaultStringName);
         buf.append("</html>");
         return buf.toString();
      }
   }

   public String getDefaultStringName() {
      if (prefix == null || prefix.isEmpty()) {
         return name;
      } else {
         return prefix + ":" + name;
      }
   }

   /**
    * Return the Owl element.
    *
    * @return the Owl element
    */
   public NamedOwlElement getOwlElement() {
      return element;
   }
}
