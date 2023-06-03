/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.gui;

import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * Represrtns a class reprensetned in the tree.
 * @since 0.4
 */
public class ClassRep {
   private final OwlClass clazz;
   private final String name;
   private final String prefix;

   public ClassRep(OwlClass clazz, OwlSchema schema) {
      this.clazz = clazz;
      this.name = clazz.getName();
      this.prefix = schema.getPrefix(clazz.getNamespace());
   }
   
   public boolean isPackage() {
      return clazz.isPackage();
   }

   @Override
   public String toString() {
      if (prefix == null) {
         return name;
      } else {
         return prefix + ":" + name;
      }
   }

   public OwlClass getOwlClass() {
      return clazz;
   }
}
