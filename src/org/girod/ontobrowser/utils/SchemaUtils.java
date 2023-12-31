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
package org.girod.ontobrowser.utils;

import org.girod.ontobrowser.model.ElementFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.restriction.OwlRestriction;

/**
 * Provides utilities to use with elements in a Schema.
 *
 * @since 0.8
 */
public class SchemaUtils {
   private SchemaUtils() {
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param theClass the class
    * @param filter the filter
    * @return the dependant classes
    */
   public static Map<ElementKey, OwlClass> getDependentClasses(OwlClass theClass, ElementFilter filter) {
      Map<ElementKey, OwlClass> map = new HashMap<>();
      if (filter == null) {
         filter = new ElementFilter();
      }
      Iterator<OwlProperty> it = getDomainProperties(theClass, filter).values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property instanceof OwlObjectProperty) {
            OwlObjectProperty objectProperty = (OwlObjectProperty) property;
            Map<ElementKey, OwlRestriction> restrictions = objectProperty.getRange();
            Iterator<OwlRestriction> it2 = restrictions.values().iterator();
            while (it2.hasNext()) {
               OwlRestriction restriction = it2.next();
               OwlClass class2 = restriction.getOwlClass();
               if (class2 != theClass) {
                  map.put(class2.getKey(), class2);
               }
            }
         }
      }
      it = getRangeProperties(theClass, filter).values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         if (property instanceof OwlObjectProperty) {
            OwlObjectProperty objectProperty = (OwlObjectProperty) property;
            Map<ElementKey, OwlRestriction> restrictions = objectProperty.getDomain();
            Iterator<OwlRestriction> it2 = restrictions.values().iterator();
            while (it2.hasNext()) {
               OwlRestriction restriction = it2.next();
               OwlClass class2 = restriction.getOwlClass();
               if (class2 != theClass) {
                  map.put(class2.getKey(), class2);
               }
            }
         }
      }
      return map;
   }

   /**
    * Return the data properties of a Class.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the data properties
    */
   public static Map<ElementKey, OwlProperty> getDataProperties(OwlClass theClass, ElementFilter filter) {
      return getDataProperties(theClass, new HashSet<>(), filter);
   }

   /**
    * Return the data properties of a Class.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the data properties
    */
   private static Map<ElementKey, OwlProperty> getDataProperties(OwlClass theClass, Set<ElementKey> excluded, ElementFilter filter) {
      Map<ElementKey, OwlProperty> map = new HashMap<>(theClass.getOwlProperties());
      if (filter == null) {
         filter = new ElementFilter();
      }
      if (filter.includeParentRelations) {
         Iterator<OwlClass> it = theClass.getSuperClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass superClass = it.next();
            if (!superClass.isThing()) {
               Map<ElementKey, OwlProperty> map2 = getDataProperties(superClass, filter);
               map.putAll(map2);
            }
         }
      }
      if (filter.includeAlias) {
         Set<ElementKey> excluded2 = new HashSet<>(excluded);
         excluded2.add(theClass.getKey());
         Iterator<OwlClass> it = theClass.getAliasClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass aliasClass = it.next();
            if (!aliasClass.isThing()) {
               Map<ElementKey, OwlProperty> map2 = getDataProperties(aliasClass, excluded2, filter);
               map.putAll(map2);
            }
         }
      }
      Map<ElementKey, OwlProperty> map2 = new HashMap<>();
      Iterator<Map.Entry<ElementKey, OwlProperty>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<ElementKey, OwlProperty> entry = it.next();
         if (entry.getValue() instanceof OwlDatatypeProperty) {
            map2.put(entry.getKey(), entry.getValue());
         }
      }
      return map2;
   }

   /**
    * Return the properties for which this Class is in their domain.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the properties for which this Class is in their domain
    */
   public static Map<ElementKey, OwlProperty> getDomainProperties(OwlClass theClass, ElementFilter filter) {
      return getDomainProperties(theClass, new HashSet<>(), filter);
   }

   /**
    * Return the properties for which this Class is in their domain.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the properties for which this Class is in their domain
    */
   private static Map<ElementKey, OwlProperty> getDomainProperties(OwlClass theClass, Set<ElementKey> excluded, ElementFilter filter) {
      if (filter == null) {
         filter = new ElementFilter();
      }
      Map<ElementKey, OwlProperty> map = new HashMap<>(theClass.getOwlProperties());
      if (filter.includeParentRelations) {
         Iterator<OwlClass> it = theClass.getSuperClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass superClass = it.next();
            if (!superClass.isThing()) {
               Map<ElementKey, OwlProperty> map2 = getDomainProperties(superClass, filter);
               map.putAll(map2);
            }
         }
      }
      if (filter.includeAlias) {
         Set<ElementKey> excluded2 = new HashSet<>(excluded);
         excluded2.add(theClass.getKey());
         Iterator<OwlClass> it = theClass.getAliasClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass aliasClass = it.next();
            if (!aliasClass.isThing() && !excluded2.contains(aliasClass.getKey())) {
               Map<ElementKey, OwlProperty> map2 = getDomainProperties(aliasClass, excluded2, filter);
               map.putAll(map2);
            }
         }
      }
      Map<ElementKey, OwlProperty> map2 = new HashMap<>();
      Iterator<Map.Entry<ElementKey, OwlProperty>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<ElementKey, OwlProperty> entry = it.next();
         if (entry.getValue() instanceof OwlObjectProperty) {
            map2.put(entry.getKey(), entry.getValue());
         }
      }
      return map2;
   }

   /**
    * Return the properties for which this Class is in their range.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the properties for which this Class is in their range
    */
   public static Map<ElementKey, OwlProperty> getRangeProperties(OwlClass theClass, ElementFilter filter) {
      return getRangeProperties(theClass, new HashSet<>(), filter);
   }

   /**
    * Return the properties for which this Class is in their range.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the properties for which this Class is in their range
    */
   private static Map<ElementKey, OwlProperty> getRangeProperties(OwlClass theClass, Set<ElementKey> excluded, ElementFilter filter) {
      if (filter == null) {
         filter = new ElementFilter();
      }
      Map<ElementKey, OwlProperty> map = new HashMap<>(theClass.getRangeOwlProperties());
      if (filter.includeParentRelations) {
         Iterator<OwlClass> it = theClass.getSuperClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass superClass = it.next();
            if (!superClass.isThing()) {
               Map<ElementKey, OwlProperty> map2 = getRangeProperties(superClass, filter);
               map.putAll(map2);
            }
         }
      }
      if (filter.includeAlias) {
         Set<ElementKey> excluded2 = new HashSet<>(excluded);
         excluded2.add(theClass.getKey());
         Iterator<OwlClass> it = theClass.getAliasClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass aliasClass = it.next();
            if (!aliasClass.isThing()) {
               Map<ElementKey, OwlProperty> map2 = getRangeProperties(aliasClass, excluded2, filter);
               map.putAll(map2);
            }
         }
      }
      Map<ElementKey, OwlProperty> map2 = new HashMap<>();
      Iterator<Map.Entry<ElementKey, OwlProperty>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<ElementKey, OwlProperty> entry = it.next();
         if (entry.getValue() instanceof OwlObjectProperty) {
            map2.put(entry.getKey(), entry.getValue());
         }
      }
      return map2;
   }
}
