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
package org.girod.ontobrowser.actions.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.PackageConfigType;
import org.girod.ontobrowser.model.PackageType;
import org.girod.ontobrowser.model.PackagesConfiguration;
import org.girod.ontobrowser.model.restriction.OwlRestriction;

/**
 * This class attempt to extract packages from the class hierarchy.
 *
 * @version 0.8
 */
public class PackagesExtractor {
   private final OwlSchema schema;
   private ElementKey thingKey;
   private PackagesConfiguration packagesConfiguration = null;
   private final Map<ElementKey, Boolean> processedPackages = new HashMap<>();
   private final Map<ElementKey, OwlClass> packages = new HashMap<>();
   private final Set<ElementKey> propertiesRanges = new HashSet<>();

   public PackagesExtractor(OwlSchema schema) {
      this.schema = schema;
   }

   public Map<ElementKey, OwlClass> extractPackages() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      packagesConfiguration = conf.getPackagesConfiguration();

      extractPackagesImpl();
      Map<ElementKey, OwlClass> notAddedClasses = extractPackagesContent();
      if (!notAddedClasses.isEmpty()) {
         extractPackagesContent2(notAddedClasses);
      }
      checkEmptyPackages();
      return packages;
   }

   private void checkEmptyPackages() {
      List<ElementKey> toRemove = new ArrayList<>();
      Iterator<OwlClass> it = packages.values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         ElementKey key = theClass.getKey();
         boolean hasChildren = false;
         Iterator<OwlClass> it2 = theClass.getSubClasses().values().iterator();
         while (it2.hasNext()) {
            OwlClass subclass = it2.next();
            if (subclass.isInPackage() && subclass.getPackage().equals(key)) {
               hasChildren = true;
               break;
            }
         }
         if (!hasChildren) {
            theClass.setIsPackage(false);
            toRemove.add(key);
         }
      }
      if (!toRemove.isEmpty()) {
         Iterator<ElementKey> it2 = toRemove.iterator();
         while (it2.hasNext()) {
            ElementKey key = it2.next();
            packages.remove(key);
         }
      }
   }

   private Map<ElementKey, OwlClass> extractPackagesContent() {
      Map<ElementKey, OwlClass> notAddedClasses = new HashMap<>();
      Iterator<OwlClass> it = packages.values().iterator();
      while (it.hasNext()) {
         OwlClass thePackage = it.next();
         ElementKey packageKey = thePackage.getKey();
         extractPackagesContent(packageKey, thePackage, notAddedClasses);
      }
      return notAddedClasses;
   }

   private void extractPackagesContent2(Map<ElementKey, OwlClass> notAddedClasses) {
      Iterator<OwlClass> it = notAddedClasses.values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         PackagesList packListFromSuperClass = getPackagesListFromSuperClass(theClass);
         PackagesList packListFromProperties = getPackagesListFromProperties(theClass);
         if (!packListFromSuperClass.hasUndefinedPackage) {
            Set<ElementKey> set = new HashSet<>(packListFromSuperClass.packages);
            Iterator<ElementKey> it2 = packListFromSuperClass.packages.iterator();
            while (it2.hasNext()) {
               ElementKey key = it2.next();
               if (!packListFromProperties.packages.contains(key)) {
                  set.remove(key);
               }
            }
            if (set.size() == 1) {
               ElementKey key = set.iterator().next();
               theClass.setPackage(key);
            } else {
               Set<ElementKey> possiblePackages = new HashSet<>();
               Iterator<OwlClass> it3 = theClass.getSuperClasses().values().iterator();
               while (it3.hasNext()) {
                  OwlClass superClass = it3.next();
                  if (superClass.isPackage()) {
                     possiblePackages.add(superClass.getKey());
                  }
               }
               if (possiblePackages.size() == 1) {
                  theClass.setPackage(possiblePackages.iterator().next());
               } else {
                  theClass.setPackage(null);
               }
            }
         }
      }
   }

   private void extractPackagesContent(ElementKey packageKey, OwlClass theClass, Map<ElementKey, OwlClass> notAddedClasses) {
      boolean isPackage = theClass.isPackage();
      if (isPackage) {
         Map<ElementKey, OwlClass> subClasses = theClass.getSubClasses();
         Iterator<OwlClass> it2 = subClasses.values().iterator();
         while (it2.hasNext()) {
            OwlClass theClass2 = it2.next();
            if (!theClass2.isPackage()) {
               PackagesList packList = getPackagesListFromSuperClass(theClass2);
               if (packList.hasUniquePackage()) {
                  theClass2.setPackage(packageKey);
                  extractPackagesContent(packageKey, theClass2, notAddedClasses);
               } else {
                  notAddedClasses.put(theClass2.getKey(), theClass2);
               }
            } else {
               theClass2.setPackage(packageKey);
            }
         }
      } else {
         Map<ElementKey, OwlClass> subClasses = theClass.getSubClasses();
         Iterator<OwlClass> it2 = subClasses.values().iterator();
         while (it2.hasNext()) {
            OwlClass theClass2 = it2.next();
            if (!theClass2.isPackage()) {
               PackagesList packList = getPackagesListFromSuperClass(theClass);
               if (packList.hasUniquePackage()) {
                  theClass2.setPackage(packageKey);
                  extractPackagesContent(packageKey, theClass2, notAddedClasses);
               } else {
                  notAddedClasses.put(theClass2.getKey(), theClass2);
               }
            } else {
               theClass2.setPackage(packageKey);
            }
         }
      }
   }

   private void extractPackagesImpl() {
      Iterator<OwlObjectProperty> it3 = schema.getOwlObjectProperties().values().iterator();
      while (it3.hasNext()) {
         OwlObjectProperty property = it3.next();
         Map<ElementKey, OwlRestriction> restrictions = property.getRange();
         Iterator<OwlRestriction> it2 = restrictions.values().iterator();
         while (it2.hasNext()) {
            OwlRestriction restriction = it2.next();
            ElementKey restrictionKey = restriction.getKey();
            propertiesRanges.add(restrictionKey);
         }
      }

      thingKey = schema.getThingClass().getKey();
      Iterator<OwlClass> it = schema.getOwlClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         ElementKey key = theClass.getKey();
         if (!key.equals(thingKey)) {
            checkIsPackage(theClass);
         }
      }
      Iterator<Entry<ElementKey, Boolean>> it2 = processedPackages.entrySet().iterator();
      while (it2.hasNext()) {
         Entry<ElementKey, Boolean> entry = it2.next();
         if (entry.getValue()) {
            packages.put(entry.getKey(), schema.getOwlClass(entry.getKey()));
         }
      }
   }

   /**
    * Return true if the Owl class is a potential package. A package is an Owl class which:
    * <ul>
    * <li>Has no dataproperties or object properties</li>
    * <li>Has no individuals</li>
    * <li>Has only one superclass, which is a potential package</li>
    * </ul>
    *
    * @param thingKey the Thing class key
    * @return true if the Owl class is a potential package
    */
   private boolean checkIsPackage(OwlClass theClass) {
      ElementKey theKey = theClass.getKey();
      switch (packagesConfiguration.acceptAsPackage(theKey)) {
         case PackageConfigType.FORCE_PACKAGE:
            processedPackages.put(theKey, true);
            theClass.setPackageType(PackageType.FORCE_PACKAGE);
            return true;
         case PackageConfigType.FORGET_PACKAGE:
            processedPackages.put(theKey, false);
            theClass.setPackageType(PackageType.FORCE_NOT_PACKAGE);
            return false;
      }
      if (!packagesConfiguration.isAcceptingDefaults()) {
         if (processedPackages.containsKey(theKey)) {
            return processedPackages.get(theKey);
         } else {
            processedPackages.put(theKey, false);
            return false;
         }
      }
      if (propertiesRanges.contains(theKey)) {
         processedPackages.put(theKey, false);
         return false;
      } else if (processedPackages.containsKey(theKey)) {
         return processedPackages.get(theKey);
      } else if (theClass.hasOwlProperties() || theClass.hasIndividuals()) {
         processedPackages.put(theKey, false);
         return false;
      } else if (!theClass.hasSubClasses()) {
         processedPackages.put(theKey, false);
         theClass.setIsPackage(false);
         return false;
      } else if (theClass.hasDefinedSuperClass()) {
         if (theClass.getSuperClasses().size() > 1) {
            processedPackages.put(theKey, false);
            return false;
         } else {
            OwlClass superClass = theClass.getSuperClasses().values().iterator().next();
            if (!superClass.getKey().equals(thingKey)) {
               boolean isPackage = checkIsPackage(superClass);
               processedPackages.put(theKey, isPackage);
               theClass.setIsPackage(isPackage);
               return isPackage;
            } else {
               boolean isPackage = true;
               Iterator<OwlClass> it = theClass.getSubClasses().values().iterator();
               while (it.hasNext()) {
                  OwlClass subClass = it.next();
                  if (subClass.getSuperClasses().size() > 1) {
                     processedPackages.put(theKey, false);
                     isPackage = false;
                  }
               }
               theClass.setIsPackage(isPackage);
               return isPackage;
            }
         }
      } else {
         processedPackages.put(theKey, true);
         theClass.setIsPackage(true);
         return true;
      }
   }

   private PackagesList getPackagesListFromProperties(OwlClass theClass) {
      PackagesList list = new PackagesList();
      Iterator<OwlProperty> it = theClass.getOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property instanceof OwlObjectProperty) {
            OwlObjectProperty objectProperty = (OwlObjectProperty) property;
            Iterator<ElementKey> it2 = objectProperty.getRange().keySet().iterator();
            while (it2.hasNext()) {
               ElementKey key = it2.next();
               OwlClass theRangeClass = schema.getOwlClass(key);
               if (theRangeClass.getPackage() != null) {
                  list.packages.add(theRangeClass.getPackage());
               } else if (!packagesConfiguration.forceAsNotPackage(key)) {
                  list.setUndefinedPackage(true);
               }
            }
         }
      }
      return list;
   }

   private PackagesList getPackagesListFromSuperClass(OwlClass theClass) {
      PackagesList list = new PackagesList();
      Iterator<OwlClass> it = theClass.getSuperClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theSuperclass = it.next();
         if (theSuperclass.isPackage()) {
            list.packages.add(theSuperclass.getKey());
         } else if (theSuperclass.getPackage() != null) {
            list.packages.add(theSuperclass.getPackage());
         } else if (!packagesConfiguration.forceAsNotPackage(theSuperclass.getKey())) {
            list.setUndefinedPackage(true);
         }
      }
      return list;
   }

   private static class PackagesList {
      private boolean hasUndefinedPackage = false;
      private Set<ElementKey> packages = new HashSet<>();

      private boolean hasUniquePackage() {
         return packages.size() == 1 && !hasUndefinedPackage;
      }

      private void setUndefinedPackage(boolean hasUndefinedPackage) {
         this.hasUndefinedPackage = hasUndefinedPackage;
      }

      @Override
      public String toString() {
         return Integer.toString(packages.size());
      }
   }
}
