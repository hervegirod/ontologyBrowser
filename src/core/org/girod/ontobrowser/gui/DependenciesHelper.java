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
package org.girod.ontobrowser.gui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.ElementFilter;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.ObjectPropertyValue;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.utils.SchemaUtils;

/**
 * The dependencies helper.
 *
 * @since 0.13
 */
public class DependenciesHelper {
   private final OwlSchema schema;
   private final DefaultListModel<Object> model;

   /**
    * Constructor.
    *
    * @param schema the schema
    * @param model the list model
    */
   public DependenciesHelper(OwlSchema schema, DefaultListModel<Object> model) {
      this.schema = schema;
      this.model = model;
   }

   /**
    * Create the dependencies list for an individual.
    *
    * @param element the element
    */
   private void createDependenciesList(OwlIndividual individual) {
      // Classes of the Individual
      model.addElement("Classes");
      SortedMap<ElementKey, OwlClass> map = new TreeMap<>(individual.getParentClasses());
      Iterator<OwlClass> it = map.values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         model.addElement(theClass);
      }
      if (individual.isInEquivalentExpressions()) {
         model.addElement("Used in Equivalent Expressions");
         map = new TreeMap<>(individual.getInEquivalentExpressions());
         it = map.values().iterator();
         while (it.hasNext()) {
            OwlClass theClass = it.next();
            model.addElement(theClass);
         }
      }
      if (individual.hasObjectPropertyValues()) {
         model.addElement("Object properties targets");

         SortedMap<ElementKey, List<ObjectPropertyValue>> mapv = new TreeMap<>(individual.getObjectPropertyValues());
         Iterator<List<ObjectPropertyValue>> itv = mapv.values().iterator();
         while (itv.hasNext()) {
            List<ObjectPropertyValue> values = itv.next();
            Iterator<ObjectPropertyValue> it2 = values.iterator();
            while (it2.hasNext()) {
               ObjectPropertyValue theValue = it2.next();
               model.addElement(theValue.getTarget());
            }            
         }
      }  
      if (individual.hasObjectTargetPropertyValues()) {
         model.addElement("Object properties source");

         SortedMap<ElementKey, List<ObjectPropertyValue>> mapv = new TreeMap<>(individual.getObjectTargetPropertyValues());
         Iterator<List<ObjectPropertyValue>> itv = mapv.values().iterator();
         while (itv.hasNext()) {
            List<ObjectPropertyValue> values = itv.next();
            Iterator<ObjectPropertyValue> it2 = values.iterator();
            while (it2.hasNext()) {
               ObjectPropertyValue theValue = it2.next();
               model.addElement(theValue.getSource());
            }                
         }
      }         
   }

   /**
    * Create the dependencies list for an Owl class.
    *
    * @param element the element
    * @filter the filter
    */
   private void createDependenciesList(OwlClass theClass, ElementFilter filter) {
      // data properties of the Class
      model.addElement("Data Properties");
      SortedMap<ElementKey, OwlProperty> mapp = new TreeMap<>(SchemaUtils.getDataProperties(theClass, filter));
      Iterator<OwlProperty> itp = mapp.values().iterator();
      while (itp.hasNext()) {
         OwlProperty property = itp.next();
         if (property instanceof OwlDatatypeProperty) {
            model.addElement(new PropertyBridge(property, true));
         }
      }
      // domain properties of the Class
      model.addElement("Object Properties Domain");
      mapp = new TreeMap<>(SchemaUtils.getDomainProperties(theClass, filter));
      itp = mapp.values().iterator();
      while (itp.hasNext()) {
         OwlProperty property = itp.next();
         if (property instanceof OwlObjectProperty) {
            model.addElement(new PropertyBridge(property, true));
         }
      }
      // range properties of the Class
      model.addElement("Object Properties Range");
      mapp = new TreeMap<>(SchemaUtils.getRangeProperties(theClass, filter));
      itp = mapp.values().iterator();
      while (itp.hasNext()) {
         OwlProperty property = itp.next();
         if (property instanceof OwlObjectProperty) {
            model.addElement(new PropertyBridge(property, false));
         }
      }
      if (theClass.hasSuperClasses()) {
         // super Classes
         model.addElement("Super-Classes");
         SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getSuperClasses());
         Iterator<OwlClass> itc = mapc.values().iterator();
         while (itc.hasNext()) {
            OwlClass parentClass = itc.next();
            model.addElement(parentClass);
         }
      }
      if (theClass.hasSubClasses()) {
         // sub Classes
         model.addElement("Sub-Classes");
         SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getSubClasses());
         Iterator<OwlClass> itc = mapc.values().iterator();
         while (itc.hasNext()) {
            OwlClass childClass = itc.next();
            model.addElement(childClass);
         }
      }

      // equivalent classes
      if (theClass.hasAliasClasses()) {
         model.addElement("Alias Classes");
         SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getAliasClasses());
         Iterator<OwlClass> iti = mapc.values().iterator();
         while (iti.hasNext()) {
            OwlClass alias = iti.next();
            model.addElement(alias);
         }
      }
      if (theClass.hasFromAliasedClasses()) {
         model.addElement("Aliased Classes");
         SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getFromAliasClasses());
         Iterator<OwlClass> iti = mapc.values().iterator();
         while (iti.hasNext()) {
            OwlClass aliased = iti.next();
            model.addElement(aliased);
         }
      }
      if (theClass.hasEquivalentExpressions()) {
         model.addElement("Use in Equivalent Expressions");
         SortedMap<ElementKey, NamedOwlElement> mapc = new TreeMap<>(theClass.getElementsInEquivalentExpressions());
         Iterator<NamedOwlElement> itc = mapc.values().iterator();
         while (itc.hasNext()) {
            NamedOwlElement theElement = itc.next();
            model.addElement(theElement);
         }
      }
      if (theClass.isInEquivalentExpressions()) {
         model.addElement("Used in Equivalent Expressions");
         SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getInEquivalentExpressions());
         Iterator<OwlClass> it = mapc.values().iterator();
         while (it.hasNext()) {
            OwlClass eqClass = it.next();
            model.addElement(eqClass);
         }
      }
      Map<ElementKey, OwlClass> mapd = schema.getDependentClasses(theClass);
      if (!mapd.isEmpty()) {
         model.addElement("Associated Classes");
         SortedMap<ElementKey, OwlClass> mapd2 = new TreeMap<>(mapd);
         Iterator<OwlClass> iti = mapd2.values().iterator();
         while (iti.hasNext()) {
            OwlClass class2 = iti.next();
            model.addElement(class2);
         }
      }

      // individuals of the Class
      model.addElement("Individuals");
      SortedMap<ElementKey, OwlIndividual> mapi = new TreeMap<>(theClass.getIndividuals());
      Iterator<OwlIndividual> iti = mapi.values().iterator();
      while (iti.hasNext()) {
         OwlIndividual individual = iti.next();
         model.addElement(individual);
      }
   }

   /**
    * Create the dependencies list for an Owl property.
    *
    * @param element the element
    */
   private void createGeneralDependenciesList(OwlProperty theProperty) {
      if (theProperty.hasSuperProperties()) {
         // super properties
         model.addElement("Super-Properties");
         SortedMap<ElementKey, OwlProperty> mapp = new TreeMap<>(theProperty.getSuperProperties());
         Iterator<OwlProperty> itc = mapp.values().iterator();
         while (itc.hasNext()) {
            OwlProperty parentProperty = itc.next();
            model.addElement(parentProperty);
         }
      }
      if (theProperty.hasSubProperties()) {
         // sub properties
         model.addElement("Sub-Properties");
         SortedMap<ElementKey, OwlProperty> mapp = new TreeMap<>(theProperty.getSubProperties());
         Iterator<OwlProperty> itc = mapp.values().iterator();
         while (itc.hasNext()) {
            OwlProperty childProperty = itc.next();
            model.addElement(childProperty);
         }
      }

      // equivalent properties
      if (theProperty.hasAliasProperties()) {
         model.addElement("Alias Properties");
         SortedMap<ElementKey, OwlProperty> mapc = new TreeMap<>(theProperty.getAliasProperties());
         Iterator<OwlProperty> iti = mapc.values().iterator();
         while (iti.hasNext()) {
            OwlProperty alias = iti.next();
            model.addElement(alias);
         }
      }
      if (theProperty.hasFromAliasedProperties()) {
         model.addElement("Aliased Properties");
         SortedMap<ElementKey, OwlProperty> mapc = new TreeMap<>(theProperty.getFromAliasProperties());
         Iterator<OwlProperty> iti = mapc.values().iterator();
         while (iti.hasNext()) {
            OwlProperty aliased = iti.next();
            model.addElement(aliased);
         }
      }
      // Classes of the domain
      model.addElement("Domain Classes");
      SortedMap<ElementKey, OwlRestriction> map = new TreeMap<>(theProperty.getDomain());
      Iterator<OwlRestriction> it = map.values().iterator();
      while (it.hasNext()) {
         OwlRestriction restriction = it.next();
         model.addElement(restriction.getOwlClass());
      }
   }

   /**
    * Create the dependencies list for a datatype property.
    *
    * @param element the element
    */
   private void createDependenciesList(OwlDatatypeProperty datatypeProperty) {
      createGeneralDependenciesList(datatypeProperty);

      Map<ElementKey, OwlDatatype> datatypes = datatypeProperty.getTypes();
      if (!datatypes.isEmpty()) {
         model.addElement("Datatypes");
         SortedMap<ElementKey, OwlDatatype> mapdt = new TreeMap<>(datatypes);
         Iterator<OwlDatatype> it2 = mapdt.values().iterator();
         while (it2.hasNext()) {
            OwlDatatype datatype = it2.next();
            model.addElement(datatype);
         }
      }
   }

   /**
    * Create the dependencies list for an object property.
    *
    * @param element the element
    */
   private void createDependenciesList(OwlObjectProperty objectProperty) {
      createGeneralDependenciesList(objectProperty);

      // Classes of the range
      model.addElement("Range Classes");
      SortedMap<ElementKey, OwlRestriction> map = new TreeMap<>(objectProperty.getRange());
      Iterator<OwlRestriction> it = map.values().iterator();
      while (it.hasNext()) {
         OwlRestriction restriction = it.next();
         model.addElement(restriction.getOwlClass());
      }

      // Inverse property
      OwlObjectProperty theInverseProperty = objectProperty.getInverseProperty();
      if (theInverseProperty != null) {
         model.addElement("Inverse Property");
         model.addElement(theInverseProperty);
      }
      if (objectProperty.isInEquivalentExpressions()) {
         model.addElement("Used in Equivalent Expressions");
         SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(objectProperty.getInEquivalentExpressions());
         Iterator<OwlClass> it2 = mapc.values().iterator();
         while (it2.hasNext()) {
            OwlClass eqClass = it2.next();
            model.addElement(eqClass);
         }
      }
   }

   /**
    * Create the dependencies list for an element.
    *
    * @param element the element
    */
   public void createDependenciesList(NamedOwlElement element) {
      boolean includeParentRelations = BrowserConfiguration.getInstance().includeParentRelations;
      boolean includeAlias = BrowserConfiguration.getInstance().includeAlias;
      ElementFilter filter = new ElementFilter();
      filter.includeParentRelations = includeParentRelations;
      filter.includeAlias = includeAlias;
      if (element instanceof OwlIndividual) {
         createDependenciesList((OwlIndividual) element);
      } else if (element instanceof OwlClass) {
         createDependenciesList((OwlClass) element, filter);
      } else if (element instanceof OwlProperty) {
         OwlProperty theProperty = (OwlProperty) element;
         if (theProperty instanceof OwlObjectProperty) {
            createDependenciesList((OwlObjectProperty) theProperty);
         } else {
            createDependenciesList((OwlDatatypeProperty) theProperty);
         }
      }

   }
}
