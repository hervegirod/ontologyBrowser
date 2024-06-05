/*
Copyright (c) 2024 Hervé Girod
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
package org.ontobrowser.xsdplugin.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;

/**
 *
 * @since 0.13
 */
public class XSDModel {
   private String rootName = null;
   private final OwlSchema schema;
   private final Map<ElementKey, ComplexTypeRep> complexTypes = new TreeMap<>();
   private final Map<ElementKey, ComplexTypeRep> topLevelTypes = new HashMap<>();

   public XSDModel(OwlSchema schema) {
      this.schema = schema;
   }

   public OwlSchema getSchema() {
      return schema;
   }

   public void setUp() {
      Iterator<ComplexTypeRep> it = complexTypes.values().iterator();
      while (it.hasNext()) {
         ComplexTypeRep rep = it.next();
         if (rep.isPackage()) {
            topLevelTypes.put(rep.getKey(), rep);
         }
         rep.setUp(this);
      }
      it = complexTypes.values().iterator();
      while (it.hasNext()) {
         ComplexTypeRep rep = it.next();
         if (rep.isTopElement()) {
            OwlClass theClass = rep.getOwlClass();
            Set<ElementKey> superClassesSet = new HashSet<>();
            Map<ElementKey, OwlClass> superClasses = theClass.getSuperClasses();
            Iterator<OwlClass> it2 = superClasses.values().iterator();
            while (it2.hasNext()) {
               OwlClass theSuperClass  = it2.next();
               if (! theSuperClass.isForeign()) {
                 superClassesSet.add(theSuperClass.getKey());
               } 
            }
            if (superClassesSet.size() == 1) {
               ElementKey theSuperClassKey = superClassesSet.iterator().next();
               ComplexTypeRep theSuperClassRep;
               if (! complexTypes.containsKey(theSuperClassKey)) {
                  theSuperClassRep = addOwlClass(schema.getOwlClass(theSuperClassKey));                  
               } else {
                  theSuperClassRep = complexTypes.get(theSuperClassKey);
               }
               rep.setSuperElement(theSuperClassRep);
            }
         }
      }
   }

   public String getRootName() {
      if (rootName == null) {
         rootName = schema.getName();
         if (rootName.endsWith("#")) {
            rootName = rootName.substring(0, rootName.length() - 1);
            int slash = rootName.lastIndexOf('/');
            if (slash != -1) {
               rootName = rootName.substring(slash + 1, rootName.length());
            }
         }
      }
      return rootName;
   }

   public void setAsTopLevel(ComplexTypeRep rep) {
      topLevelTypes.put(rep.getKey(), rep);
   }

   public ComplexTypeRep addOwlClass(OwlClass theClass) {
      ComplexTypeRep rep = new ComplexTypeRep(theClass);
      complexTypes.put(theClass.getKey(), rep);
      return rep;
   }

   public Map<ElementKey, ComplexTypeRep> getComplexTypes() {
      return complexTypes;
   }

   public ComplexTypeRep getComplexType(ElementKey key) {
      return complexTypes.get(key);
   }
   
   public boolean isTopLevelType(ElementKey key) {
      return topLevelTypes.containsKey(key);
   }

   public Map<ElementKey, ComplexTypeRep> getTopLevelTypes() {
      return topLevelTypes;
   }
}
