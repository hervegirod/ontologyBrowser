/*
Copyright (c) 2023, 2024, 2025 Hervé Girod
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;
import org.girod.ontobrowser.model.DatatypePropertyValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.ElementFilter;
import org.girod.ontobrowser.model.ObjectPropertyValue;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.parsers.graph.IndividualsHelper;
import org.girod.ontobrowser.utils.SchemaUtils;
import org.mdiutil.lang.HexaDecoder;
import org.scripthelper.context.ScriptContext;
import org.scripthelper.context.ScriptHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The script helper.
 *
 * @version 0.17
 */
public class OwlScriptHelper implements ScriptHelper {
   private static final Pattern ID_PAT = Pattern.compile("(\\d+\\s*)(.*)");
   private final OwlScriptContext context;
   private final OwlSchema schema;
   private final OntModel ontModel;
   private Script script;
   private IndividualsHelper individualsHelper = null;

   public OwlScriptHelper(OwlScriptContext context) {
      this.context = context;
      this.schema = context.getSchema();
      this.ontModel = schema.getOntModel();
   }

   void setScript(Script script) {
      this.script = script;
   }

   @Override
   public ScriptContext getContext() {
      return context;
   }

   private void getIndividualsHelper() {
      if (individualsHelper == null) {
         individualsHelper = new IndividualsHelper(schema);
      }
   }
   
   public static ElementKey normalizeKey(ElementKey key) {
      String name = key.getName();
      String namespace = key.getNamespace();
      if (name.contains(" ")) {
         name = name.replaceAll(" ", "_");
         return new ElementKey(namespace, name);
      } else {
         return key;
      }
   }

   /**
    * Return the data properties of a Class.
    *
    * @param theClass the Class
    * @param filter the request filter
    * @return the data properties
    */
   public static Map<ElementKey, OwlProperty> getDataProperties(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDataProperties(theClass, filter);
   }

   /**
    * Return the properties for which this Class is in their domain.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the properties for which this Class is in their domain
    */
   public static Map<ElementKey, OwlProperty> getDomainProperties(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDomainProperties(theClass, filter);
   }

   /**
    * Return the properties for which this Class is in their range.
    *
    * @param theClass the Class
    * @param filter the request filter
    * @return the properties for which this Class is in their range
    */
   public Map<ElementKey, OwlProperty> getRangeProperties(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getRangeProperties(theClass, filter);
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param theClass the class
    * @param filter the filter
    * @return the dependant classes
    */
   public static Map<ElementKey, OwlClass> getDependentClasses(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDependentClasses(theClass, filter);
   }

   /**
    * Open an XML file.
    *
    * @param desc the descrption of the dialog
    * @return the file (or null if the dialog was aborted)
    */
   public File openXMLFile(String desc) {
      ScriptXMLFileDialog dialog = new ScriptXMLFileDialog(context.getApplication().getApplicationWindow(), desc, context);
      int ret = dialog.showDialog();
      if (ret == JFileChooser.APPROVE_OPTION) {
         return dialog.getSelectedFile();
      } else {
         return null;
      }
   }

   /**
    * Refresh the model from the underlying file.
    */
   public void refreshFromFile() {
      context.getApplication().refreshModel(context.getDiagram());
   }

   /**
    * Refresh the model tree.
    */
   public void refreshTree() {
      context.getApplication().refreshTree(context.getDiagram());
   }

   /**
    * Return the key corresponding to a name and a prefix.
    *
    * @param prefix the prefix
    * @param name the name
    * @return the key
    */
   public ElementKey getKeyFromPrefix(String prefix, String name) {
      String namespace = schema.getNamespaceFromPrefix(prefix);
      if (namespace != null) {
         ElementKey key = ElementKey.create(namespace, name); 
         return normalizeKey(key);
      } else {
         return getKeyFromDefaultNamespace(name);
      }
   }

   /**
    * Return the key corresponding to a name in the default namespace.
    *
    * @param name the name
    * @return the key
    */
   public ElementKey getKeyFromDefaultNamespace(String name) {
      ElementKey key =  ElementKey.create(schema.getDefaultNamespace(), name);
      return normalizeKey(key);
   }

   /**
    * Return the Owl Class corresponding to a name or key.
    *
    * @param classObj the Class name or key
    * @return the Owl Class
    */
   public OwlClass getOwlClass(Object classObj) {
      if (classObj == null) {
         context.echo("Tried to get a Class, Class name or key is null", "red");
         return null;
      }
      if (classObj instanceof String) {
         return getOwlClass((String) classObj);
      } else if (classObj instanceof ElementKey) {
         return getOwlClass((ElementKey) classObj);
      } else {
         context.echo("Tried to get a Class, type of Class reference (" + classObj.getClass().getName() + ") is incorrrect", "red");
         return null;
      }
   }

   /**
    * Return the Owl Class corresponding to a name in the default namespace.
    *
    * @param name the name
    * @return the Owl Class
    */
   private OwlClass getOwlClass(String name) {
      return getOwlClass(getKeyFromDefaultNamespace(name));
   }

   /**
    * Return the Owl Class corresponding to a key.
    *
    * @param classKey the key
    * @return the Owl Class
    */
   private OwlClass getOwlClass(ElementKey classKey) {
      if (schema.hasOwlClass(classKey)) {
         return schema.getOwlClass(classKey);
      } else {
         context.echo("Tried to get a Class, Class of key " + classKey + " does not exist", "red");
         return null;
      }
   }

   /**
    * Return the Owl Individual corresponding to a name or key.
    *
    * @param individualObj the Individual name or key
    * @return the Individual
    */
   public OwlIndividual getIndividual(Object individualObj) {
      if (individualObj == null) {
         context.echo("Tried to get an Individual, Individual name or key is null", "red");
         return null;
      }
      if (individualObj instanceof String) {
         return getIndividual((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         return getIndividual((ElementKey) individualObj);
      } else {
         context.echo("Tried to get an Individual, type of Individual reference (" + individualObj.getClass().getName() + ") is incorrrect", "red");
         return null;
      }
   }

   /**
    * Return the Owl Individual corresponding to a name.
    *
    * @param name the name
    * @return the Owl Individual
    */
   private OwlIndividual getIndividual(String name) {
      ElementKey key = getKeyFromDefaultNamespace(name);
      if (schema.hasIndividual(key)) {
         return schema.getIndividual(key);
      } else {
         context.echo("Tried to get an Individual, Individual of key " + key + " does not exist", "red");
         return null;
      }
   }

   /**
    * Return the Owl Individual corresponding to a key.
    *
    * @param individualKey the key
    * @return the Owl Individual
    */
   private OwlIndividual getIndividual(ElementKey individualKey) {
      return schema.getIndividual(individualKey);
   }

   /**
    * Return the Owl Property corresponding to a name or key.
    *
    * @param propertyObj the Property name or key
    * @return the Property
    */
   public OwlProperty getOwlProperty(Object propertyObj) {
      if (propertyObj == null) {
         context.echo("Tried to get a Property, Property name or key is null", "red");
         return null;
      }
      if (propertyObj instanceof String) {
         return getOwlProperty((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         return getOwlProperty((ElementKey) propertyObj);
      } else {
         context.echo("Tried to get a Property, type of Property reference (" + propertyObj.getClass().getName() + ") is incorrrect", "red");
         return null;
      }
   }

   /**
    * Return the Owl Property corresponding to a name.
    *
    * @param name the name
    * @return the Owl Property
    */
   private OwlProperty getOwlProperty(String name) {
      return getOwlProperty(getKeyFromDefaultNamespace(name));
   }

   /**
    * Return the Owl Property corresponding to a key.
    *
    * @param propertyKey the key
    * @return the Owl Property
    */
   private OwlProperty getOwlProperty(ElementKey propertyKey) {
      if (schema.hasOwlProperty(propertyKey)) {
         return schema.getOwlProperty(propertyKey);
      } else {
         context.echo("Tried to get a Property, Property of key " + propertyKey + " does not exist", "red");
         return null;
      }
   }

   /**
    * Return the list of object property values for an individual and a data property. It will return null if the property does not exist.
    *
    * @param individualObj the individual name or key
    * @param propertyObj the object property name or key
    * @return the list of property values
    */
   public List<ObjectPropertyValue> getOwlObjectPropertyValues(Object individualObj, Object propertyObj) {
      if (individualObj == null) {
         context.echo("Tried to get the data property values on an Individual, Individual name or key is null", "red");
         return null;
      } else if (propertyObj == null) {
         context.echo("Tried to get the data property values on an Individual, DatatypeProperty name or key is null", "red");
         return null;
      }
      ElementKey individualKey;
      ElementKey propertyKey;
      if (individualObj instanceof String) {
         individualKey = getKeyFromDefaultNamespace((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (propertyObj instanceof String) {
         propertyKey = getKeyFromDefaultNamespace((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         propertyKey = (ElementKey) propertyObj;
      } else {
         propertyKey = null;
      }
      if (individualKey != null && propertyKey != null) {
         return getOwlObjectPropertyValuesFromKey(individualKey, propertyKey);
      } else {
         context.echo("Tried to get the object property values on an Individual, type of Individual reference (" + individualObj.getClass().getName() + ") and/ or DatatypeProperty reference (" + propertyObj.getClass().getName() + ") is incorrrect", "red");
         return null;
      }
   }

   /**
    * Return the list of object property values for an individual and an object property. It will return null if the property does not
    * exist.
    *
    * @param individualKey the individual key
    * @param propertyKey the object property key
    * @return the list of property values
    */
   private List<ObjectPropertyValue> getOwlObjectPropertyValuesFromKey(ElementKey individualKey, ElementKey propertyKey) {
      OwlIndividual individual = getIndividual(individualKey);
      OwlProperty property = getOwlProperty(propertyKey);
      if (individual != null && property != null && property.isObjectProperty()) {
         Map<ElementKey, List<ObjectPropertyValue>> map = individual.getObjectPropertyValues();
         if (map.containsKey(propertyKey)) {
            return map.get(propertyKey);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Return true if an individual has values for an object property. It will return false if the property does not exist or the individual
    * does not have values for this property.
    *
    * @param individualObj the individual name or key
    * @param propertyObj the object property name or key
    * @return true if there are object property values for the property
    */
   public boolean hasOwlObjectPropertyValues(Object individualObj, Object propertyObj) {
      if (individualObj == null) {
         context.echo("Tried to detect if there are object property values on an Individual, Individual name or key is null", "red");
         return false;
      } else if (propertyObj == null) {
         context.echo("Tried to detect if there are object property values on an Individual, DatatypeProperty name or key is null", "red");
         return false;
      }
      ElementKey individualKey;
      ElementKey propertyKey;
      if (individualObj instanceof String) {
         individualKey = getKeyFromDefaultNamespace((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (propertyObj instanceof String) {
         propertyKey = getKeyFromDefaultNamespace((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         propertyKey = (ElementKey) propertyObj;
      } else {
         propertyKey = null;
      }
      return hasOwlObjectPropertyValuesFromKey(individualKey, propertyKey);
   }

   /**
    * Return true if an individual has values for an object property. It will return false if the property does not exist or the individual
    * does not have values for this property.
    *
    * @param individualKey the individual key
    * @param propertyKey the object property key
    * @return the list of property values
    */
   private boolean hasOwlObjectPropertyValuesFromKey(ElementKey individualKey, ElementKey propertyKey) {
      OwlIndividual individual = getIndividual(individualKey);
      OwlProperty property = getOwlProperty(propertyKey);
      if (individual != null && property != null && property.isObjectProperty()) {
         Map<ElementKey, List<ObjectPropertyValue>> map = individual.getObjectPropertyValues();
         if (map.containsKey(propertyKey)) {
            return !map.isEmpty();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   /**
    * Return the list of data property values for an individual and a data property. It will return null if the property does not exist.
    *
    * @param individualObj the individual name or key
    * @param propertyObj the data property name or key
    * @return the list of property values
    */
   public List<DatatypePropertyValue> getOwlDatatypePropertyValues(Object individualObj, Object propertyObj) {
      if (individualObj == null) {
         context.echo("Tried to get the data property values on an Individual, Individual name or key is null", "red");
         return null;
      } else if (propertyObj == null) {
         context.echo("Tried to get the data property values on an Individual, DatatypeProperty name or key is null", "red");
         return null;
      }
      ElementKey individualKey;
      ElementKey propertyKey;
      if (individualObj instanceof String) {
         individualKey = getKeyFromDefaultNamespace((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (propertyObj instanceof String) {
         propertyKey = getKeyFromDefaultNamespace((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         propertyKey = (ElementKey) propertyObj;
      } else {
         propertyKey = null;
      }
      if (individualKey != null && propertyKey != null) {
         return getOwlDatatypePropertyValuesFromKey(individualKey, propertyKey);
      } else {
         context.echo("Tried to get the data property values on an Individual, type of Individual reference (" + individualObj.getClass().getName() + ") and/ or DatatypeProperty reference (" + propertyObj.getClass().getName() + ") is incorrrect", "red");
         return null;
      }
   }

   /**
    * Return the list of data property values for an individual and a data property. It will return null if the property does not exist.
    *
    * @param individualKey the individual key
    * @param propertyKey the data property key
    * @return the list of property values
    */
   private List<DatatypePropertyValue> getOwlDatatypePropertyValuesFromKey(ElementKey individualKey, ElementKey propertyKey) {
      OwlIndividual individual = getIndividual(individualKey);
      OwlProperty property = getOwlProperty(propertyKey);
      if (individual != null && property != null && property.isDatatypeProperty()) {
         Map<ElementKey, List<DatatypePropertyValue>> map = individual.getDatatypePropertyValues();
         if (map.containsKey(propertyKey)) {
            return map.get(propertyKey);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Return true if an individual has values for a data property. It will return false if the property does not exist or the individual
    * does not have values for this property.
    *
    * @param individualObj the individual name or key
    * @param propertyObj the data property name or key
    * @return true if there are data property values for the property
    */
   public boolean hasOwlDatatypePropertyValues(Object individualObj, Object propertyObj) {
      if (individualObj == null) {
         context.echo("Tried to detect if there are data property values on an Individual, Individual name or key is null", "red");
         return false;
      } else if (propertyObj == null) {
         context.echo("Tried to detect if there are data property values on an Individual, DatatypeProperty name or key is null", "red");
         return false;
      }
      ElementKey individualKey;
      ElementKey propertyKey;
      if (individualObj instanceof String) {
         individualKey = getKeyFromDefaultNamespace((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (propertyObj instanceof String) {
         propertyKey = getKeyFromDefaultNamespace((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         propertyKey = (ElementKey) propertyObj;
      } else {
         propertyKey = null;
      }
      return hasOwlDatatypePropertyValuesFromKey(individualKey, propertyKey);
   }

   /**
    * Return true if an individual has values for a data property. It will return false if the property does not exist or the individual
    * does not have values for this property.
    *
    * @param individualKey the individual key
    * @param propertyKey the data property key
    * @return true if there are data property values for the property
    */
   private boolean hasOwlDatatypePropertyValuesFromKey(ElementKey individualKey, ElementKey propertyKey) {
      OwlIndividual individual = getIndividual(individualKey);
      OwlProperty property = getOwlProperty(propertyKey);
      if (individual != null && property != null && property.isDatatypeProperty()) {
         Map<ElementKey, List<ObjectPropertyValue>> map = individual.getDatatypePropertyValues();
         if (map.containsKey(propertyKey)) {
            return !map.isEmpty();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private String replaceNameID(String name) {
      Matcher m = ID_PAT.matcher(name);
      if (m.matches()) {
         name = m.group(2);
      }
      name = name.replace(" ", "_");
      name = name.replace(":", "");
      name = name.replace("/", "_");
      name = name.replace("+", "_");
      return name;
   }

   /**
    * Parse an int value.
    *
    * @param content the value as a string
    * @param defaultValue the default value to use if the value can not be parsed as an int
    * @return the parsed value as an int
    */
   public int parseInt(String content, int defaultValue) {
      try {
         return Integer.parseInt(content);
      } catch (NumberFormatException e) {
         return defaultValue;
      }
   }

   /**
    * Add an individual.
    *
    * @param classObj the class name or key
    * @param individualObj the individual name or key
    * @return the individual, or null if it was not possible to create the individual
    */
   public ElementKey addIndividual(Object classObj, Object individualObj) {
      if (classObj == null) {
         context.echo("Tried to add an Individual, Class name or key is null", "red");
         return null;
      } else if (individualObj == null) {
         context.echo("Tried to add an Individual, Individual name or key is null", "red");
         return null;
      }
      ElementKey classKey;
      ElementKey individualKey;
      if (classObj instanceof String) {
         classKey = this.getKeyFromDefaultNamespace((String) classObj);
      } else if (classObj instanceof ElementKey) {
         classKey = (ElementKey) classObj;
      } else {
         classKey = null;
      }
      if (individualObj instanceof String) {
         individualKey = getKeyFromDefaultNamespace((String) individualObj);
      } else if (classObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (classKey != null && individualKey != null) {
         return addIndividualFromKey(classKey, individualKey);
      } else {
         context.echo("Tried to add an Individual, type of Class reference (" + classObj.getClass().getName() + ") and/ or Individual reference (" + individualObj.getClass().getName() + ") is incorrrect", "red");
         return null;
      }
   }

   /**
    * Add an individual.
    *
    * @param classKey the class key
    * @param individualKey the individual key
    * @return the individual, or null if it was not possible to create the individual
    */
   private ElementKey addIndividualFromKey(ElementKey classKey, ElementKey individualKey) {
      if (schema.hasOwlClass(classKey)) {
         if (schema.hasIndividual(individualKey)) {
            return null;
         }
         OwlClass owlClass = schema.getOwlClass(classKey);
         // see https://stackoverflow.com/questions/43719469/create-individuals-using-jena
         URI uri = individualKey.toURI();
         if (uri != null) {
            Individual individual = ontModel.createIndividual(uri.toString(), owlClass.getOntClass());
            OwlIndividual owlIndividual = new OwlIndividual(owlClass, individual);
            schema.addIndividual(owlIndividual);

            return owlIndividual.getKey();
         } else {
            context.echo("URI of Individual of key " + individualKey + " invalid", "red");
            return null;
         }
      } else {
         context.echo("Tried to add an Individual, no Class of key " + classKey, "red");
         return null;
      }
   }

   private String escapeValue(String value) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
         int c = value.charAt(i);
         if ((c >= '0') && (c <= '9')) {
            buf.append(c);
         } else if ((c >= 'a') && (c <= 'Z')) {
            buf.append(c);
         } else if ((c >= 'A') && (c <= 'Z')) {
            buf.append(c);
         } else if (c == '_') {
            buf.append(c);
         } else if (c == ' ') {
            buf.append(c);
         } else {
            String hex = HexaDecoder.toHexString((int) c);
            buf.append(hex);
         }
      }
      return buf.toString();
   }

   /**
    * Add a data property value for an individual. The value can be added if the individual exist, the property exist and is a data
    * property, and it has a datatype.
    *
    * @param individualObj the individual name or key
    * @param propertyObj the data property name or key
    * @param value the value
    * @return true if the value could be added
    */
   public boolean addIndividualDataPropertyValue(Object individualObj, Object propertyObj, Object value) {
      if (individualObj == null) {
         context.echo("Tried to add a DatatypeProperty on an Individual, Individual name or key is null", "red");
         return false;
      } else if (propertyObj == null) {
         context.echo("Tried to add a DatatypeProperty on an Individual, DatatypeProperty name or key is null", "red");
         return false;
      } else if (value == null) {
         context.echo("Tried to add a DatatypeProperty on an Individual, value is null", "red");
         return false;
      }
      ElementKey individualKey;
      ElementKey propertyKey;
      if (individualObj instanceof String) {
         individualKey = this.getKeyFromDefaultNamespace((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (propertyObj instanceof String) {
         propertyKey = getKeyFromDefaultNamespace((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         propertyKey = (ElementKey) propertyObj;
      } else {
         propertyKey = null;
      }
      if (individualKey != null && propertyKey != null) {
         return addIndividualDataPropertyValueFromKey(individualKey, propertyKey, value.toString());
      } else {
         context.echo("Tried to add a DatatypeProperty on an Individual, type of Individual reference (" + individualObj.getClass().getName() + ") and/ or DatatypeProperty reference (" + propertyObj.getClass().getName() + ") is incorrrect", "red");
         return false;
      }
   }

   /**
    * Add a data property value for an individual. The value can be added if the individual exist, the property exist and is a data
    * property, and it has a datatype.
    *
    * @param individualKey the individual key
    * @param propertyKey the data property key
    * @param value the value
    * @return true if the value could be added
    */
   private boolean addIndividualDataPropertyValueFromKey(ElementKey individualKey, ElementKey propertyKey, String value) {
      if (individualKey == null) {
         context.echo("Tried to add a DatatypeProperty on an Individual, Individual key is null", "red");
         return false;
      }
      getIndividualsHelper();
      if (schema.hasIndividual(individualKey)) {
         if (schema.hasOwlProperty(propertyKey)) {
            OwlProperty owlproperty = schema.getOwlProperty(propertyKey);
            if (owlproperty instanceof OwlDatatypeProperty) {
               OntProperty property = owlproperty.getProperty();
               OwlIndividual owlIndividual = schema.getIndividual(individualKey);
               Resource resource = owlIndividual.getIndividual();
               resource.addProperty(property, value);
               OwlDatatypeProperty datatypeproperty = (OwlDatatypeProperty) owlproperty;
               OwlDatatype datatype = datatypeproperty.getFirstType();
               if (datatype != null) {
                  //value = escapeValue(value);
                  DatatypePropertyValue propValue = new DatatypePropertyValue(datatypeproperty, owlIndividual, datatype, value);
                  owlIndividual.addDatatypePropertyValue(propValue);
                  return true;
               }
            } else {
               context.echo("Tried to add a DatatypeProperty on an Individual, no DatatypeProperty of key " + propertyKey, "red");
            }
         } else {
            context.echo("Tried to add a DatatypeProperty on an Individual, no DatatypeProperty of key " + propertyKey, "red");
         }
      } else {
         context.echo("Tried to add a DatatypeProperty on an Individual, no Individual of key " + individualKey, "red");
      }
      return false;
   }

   /**
    * Add an individual.
    *
    * @param individualObj the individual name or key
    * @param propertyObj the object property name or key
    * @param targetObj the target individual name or key
    * @return the individual, or null if it was not possible to create the individual
    */
   public boolean addIndividualObjectPropertyValue(Object individualObj, Object propertyObj, Object targetObj) {
      if (individualObj == null) {
         context.echo("Tried to add an ObjectProperty on an Individual, Individual name or key is null", "red");
         return false;
      } else if (propertyObj == null) {
         context.echo("Tried to add an ObjectProperty on an Individual, ObjectProperty name or key is null", "red");
         return false;
      } else if (targetObj == null) {
         context.echo("Tried to add an ObjectProperty on an Individual, target Individual name or key is null", "red");
         return false;
      }
      ElementKey individualKey;
      ElementKey propertyKey;
      ElementKey targetKey;
      if (individualObj instanceof String) {
         individualKey = this.getKeyFromDefaultNamespace((String) individualObj);
      } else if (individualObj instanceof ElementKey) {
         individualKey = (ElementKey) individualObj;
      } else {
         individualKey = null;
      }
      if (propertyObj instanceof String) {
         propertyKey = getKeyFromDefaultNamespace((String) propertyObj);
      } else if (propertyObj instanceof ElementKey) {
         propertyKey = (ElementKey) propertyObj;
      } else {
         propertyKey = null;
      }
      if (targetObj instanceof String) {
         targetKey = this.getKeyFromDefaultNamespace((String) targetObj);
      } else if (targetObj instanceof ElementKey) {
         targetKey = (ElementKey) targetObj;
      } else {
         targetKey = null;
      }
      if (individualKey != null && propertyKey != null && targetKey != null) {
         return addIndividualObjectPropertyValueFromKey(individualKey, propertyKey, targetKey);
      } else {
         context.echo("Tried to add an ObjectProperty on an Individual, type of Individual reference (" + individualObj.getClass().getName() + ") and/ or ObjectProperty reference (" + propertyObj.getClass().getName() + ") and/ or Target reference (" + targetObj.getClass().getName() + ") is incorrrect", "red");
         return false;
      }
   }

   /**
    * Add an object property value for an individual. The value can be added if the individual exist, the property exist and is an object
    * property, and the target exists.
    *
    * @param individualKey the individual key
    * @param propertyKey the object property key
    * @param targetKey the target key
    * @return true if the value could be added
    */
   private boolean addIndividualObjectPropertyValueFromKey(ElementKey individualKey, ElementKey propertyKey, ElementKey targetKey) {
      if (individualKey == null) {
         context.echo("Tried to add an ObjectProperty on an Individual, Individual key is null", "red");
         return false;
      } else if (targetKey == null) {
         context.echo("Tried to add an ObjectProperty on an Individual, Individual Target key is null", "red");
         return false;
      } else if (propertyKey == null) {
         context.echo("Tried to add an ObjectProperty on an Individual, ObjectProperty key is null", "red");
         return false;
      }
      getIndividualsHelper();
      if (schema.hasIndividual(individualKey) && schema.hasIndividual(targetKey)) {
         if (schema.hasOwlProperty(propertyKey)) {
            OwlProperty owlproperty = schema.getOwlProperty(propertyKey);
            if (owlproperty instanceof OwlObjectProperty) {
               OntProperty property = owlproperty.getProperty();
               OwlIndividual owlIndividual = schema.getIndividual(individualKey);
               OwlIndividual owlTargetIndividual = schema.getIndividual(targetKey);
               OwlObjectProperty objectproperty = (OwlObjectProperty) owlproperty;
               ObjectPropertyValue propValue = new ObjectPropertyValue(objectproperty, owlIndividual, owlTargetIndividual);
               owlIndividual.addObjectPropertyValue(propValue);

               Resource individual = owlIndividual.getIndividual();
               Resource individual2 = owlTargetIndividual.getIndividual();
               individual.addProperty(property, individual2);
               return true;
            } else {
               context.echo("Tried to add an ObjectProperty on an Individual, no ObjectProperty of key " + propertyKey, "red");
            }
         }
      } else {
         if (!schema.hasIndividual(individualKey)) {
            context.echo("Tried to add an ObjectProperty on an Individual, no Individual of key " + individualKey, "red");
         }
         if (!schema.hasIndividual(targetKey)) {
            context.echo("Tried to add an ObjectProperty on an Individual, no Individual Target of key " + individualKey, "red");
         }
      }
      return false;
   }

   /**
    * Parse an XML file.
    *
    * @param file the XML file
    * @param handler the XML handler
    */
   public void parseXML(File file, XMLHandler handler) {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      try {
         SAXParser parser = factory.newSAXParser();
         XMLParserHandler phandler = new XMLParserHandler(script);
         parser.parse(file, phandler);
      } catch (ParserConfigurationException | SAXException | IOException ex) {
         script.fireXMLException(ex);
      }
   }

   /**
    * Parse an XML file.
    *
    * @param file the XML file
    */
   public void parseXML(File file) {
      parseXML(file, script);
   }

   private class XMLParserHandler extends DefaultHandler {
      private final XMLHandler xmlHandler;
      private StringBuilder buf = null;

      private XMLParserHandler(XMLHandler xmlHandler) {
         this.xmlHandler = xmlHandler;
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attr) {
         Map<String, String> attributes = new HashMap<>();
         buf = null;
         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            attributes.put(attrname, attrvalue);
         }
         xmlHandler.startXMLElement(qName, attributes);
      }

      @Override
      public void characters(char[] chararacters, int start, int length) {
         if (buf == null) {
            buf = new StringBuilder();
         }
         buf.append(chararacters, start, length);
      }

      @Override
      public void endElement(String uri, String localName, String qName) {
         String cdata = null;
         if (buf != null) {
            cdata = buf.toString();
            cdata = cdata.trim();
            if (cdata.isEmpty()) {
               cdata = null;
            }
            buf = null;
         }
         xmlHandler.endXMLElement(cdata, qName);
      }

      @Override
      public void error(SAXParseException e) {
         xmlHandler.xmlError(e, XMLExceptionType.ERROR);
         context.echo(e.getMessage(), "red");
      }

      @Override
      public void warning(SAXParseException e) {
         xmlHandler.xmlError(e, XMLExceptionType.WARNING);
         context.echo(e.getMessage(), "red");
      }

      @Override
      public void fatalError(SAXParseException e) {
         context.echo(e.getMessage(), "red");
         xmlHandler.xmlError(e, XMLExceptionType.FATAL);
      }
   }
}
