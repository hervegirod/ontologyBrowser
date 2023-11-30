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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The set of packages to process specifically.
 *
 * @since 0.5
 */
public class PackagesConfiguration {
   private final Map<ElementKey, Short> classes = new HashMap<>();
   private final Set<String> forgetNameSpaces = new HashSet<>();
   private boolean acceptDefaults = true;

   public PackagesConfiguration() {
   }

   public void acceptDefaults(boolean acceptDefaults) {
      this.acceptDefaults = acceptDefaults;
   }

   public boolean isAcceptingDefaults() {
      return acceptDefaults;
   }

   public final void reset() {
      classes.clear();
      forgetNameSpaces.clear();
      acceptDefaults = true;
   }

   public void forgetNamespace(String namespace) {
      forgetNameSpaces.add(namespace);
   }

   public void addClass(ElementKey key, short type) {
      classes.put(key, type);
   }

   public Map<ElementKey, Short> getPackagesConfig() {
      return classes;
   }

   /**
    * Return the type of the package for a Owl class as defined in the packages configuration. The possible values are:
    * <ul>
    * <li>{@link PackageConfigType#FORCE_PACKAGE}: the Owl class is forced as a package</li>
    * <li>{@link PackageConfigType#FORGET_PACKAGE}: the Owl class is forced as not a package</li>
    * <li>{@link PackageConfigType#DEFAULT}: the Owl class will be considered as a package depending on the context</li>
    * </ul>
    *
    * @param key the class key
    * @return the type of the package
    */
   public short acceptAsPackage(ElementKey key) {
      if (!classes.containsKey(key)) {
         String namespace = key.getNamespace();
         if (namespace != null && forgetNameSpaces.contains(namespace)) {
            return PackageConfigType.FORGET_PACKAGE;
         } else {
            return PackageConfigType.DEFAULT;
         }
      } else {
         return classes.get(key);
      }
   }

   /**
    * Return true if a Owl class must be forced as a package.
    *
    * @param key Owl the class key
    * @return true if the Owl class must be forced as a package
    */
   public boolean forceAsPackage(ElementKey key) {
      if (!classes.containsKey(key)) {
         return false;
      } else {
         return classes.get(key).equals(PackageConfigType.FORCE_PACKAGE);
      }
   }

   /**
    * Return true if a Owl class must be forced as not a package.
    *
    * @param key Owl the class key
    * @return true if the Owl class must be forced as not a package
    */
   public boolean forceAsNotPackage(ElementKey key) {
      if (!classes.containsKey(key)) {
         return false;
      } else {
         return classes.get(key).equals(PackageConfigType.FORGET_PACKAGE);
      }
   }
}
