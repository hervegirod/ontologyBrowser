/*
Copyright (c) 2021, 2023, 2024 Hervé Girod
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.girod.ontobrowser.utils.SchemaUtils;

/**
 * Specifies the graph of an Owl ontology.
 *
 * @version 0.13
 */
public class OwlSchema extends AnnotatedElement implements NamedElement, OwlDeclaredSchema, Cloneable, Serializable {
   private File file = null;
   private final OntModel ontModel;
   private OwlClass owlThingClass;
   private boolean includeIndividuals = true;
   private String defaultNamespace = null;
   private String defaultSquashedNamespace = null;
   private String defaultPrefix = null;
   private boolean hasForeignElements = false;
   private boolean hasNonForeignElements = false;
   private String namespaceFromFile;
   private short representationType = OwlRepresentationType.TYPE_OWL_XML;
   private final Map<String, OwlImportedSchema> importedSchemas = new HashMap<>();
   private final Map<String, OwlImportedSchema> importedSchemasFromNamespace = new HashMap<>();
   private final Map<String, OwlDeclaredSchema> declaredSchemasFromNamespace = new HashMap<>();
   private final Map<String, String> prefixMap = new HashMap<>();
   private final Map<String, String> prefixToNamespace = new HashMap<>();
   private final Map<ElementKey, OwlClass> classes = new HashMap<>();
   private final Map<ElementKey, OwlIndividual> individuals = new HashMap<>();
   private final Map<ElementKey, OwlDatatypeProperty> datatypeProperties = new HashMap<>();
   private final Map<ElementKey, OwlObjectProperty> objectProperties = new HashMap<>();
   private final Map<ElementKey, OwlProperty> properties = new HashMap<>();
   private final Map<ElementKey, OwlAnnotation> elementsAnnotations = new HashMap<>();
   private final Map<ElementKey, OwlDatatype> datatypes = new HashMap<>();
   private final Set<String> namespaces = new HashSet<>();
   private Map<ElementKey, OwlClass> packages = null;

   public OwlSchema(OntModel ontModel) {
      this.ontModel = ontModel;
      computePrefixMap();
   }

   public OwlSchema(OntModel ontModel, short owlRepresentationType, File file) {
      this.ontModel = ontModel;
      this.file = file;
      this.representationType = owlRepresentationType;
      computePrefixMap();
   }

   /**
    * Reset the content of the schema.
    */
   public void reset() {
      this.classes.clear();
      this.individuals.clear();
      this.datatypeProperties.clear();
      this.objectProperties.clear();
      this.properties.clear();
      this.elementsAnnotations.clear();
      this.datatypes.clear();
      this.namespaces.clear();
   }

   /**
    * Set the file which specifies the ontology.
    *
    * @param file the file
    */
   public void setFile(File file) {
      this.file = file;
   }

   /**
    * Return the file which specifies the ontology.
    *
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * Return the OntModel.
    *
    * @return the OntModel
    */
   public OntModel getOntModel() {
      return ontModel;
   }

   private void computePrefixMap() {
      SchemasRepository schemasRepository = SchemasRepository.getInstance();
      Iterator<Entry<String, String>> it = ontModel.getNsPrefixMap().entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         String ns = NamespaceUtils.fixNamespace(entry.getValue());
         String prefix = entry.getKey();
         prefixMap.put(ns, prefix);
         prefixToNamespace.put(prefix, ns);
      }
      if (prefixToNamespace.containsKey("")) {
         defaultNamespace = prefixToNamespace.get("");
      }
      it = prefixToNamespace.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         String prefix = entry.getKey();
         String ns = NamespaceUtils.fixNamespace(entry.getValue());
         if (prefix.isEmpty()) {
            continue;
         }
         if (defaultNamespace != null && defaultNamespace.equals(ns)) {
            defaultPrefix = prefix;
         } else {
            switch (ns) {
               case "http://www.w3.org/2002/07/owl#":
               case "http://www.w3.org/2001/XMLSchema#":
               case "http://www.w3.org/XML/1998/namespace":
               case "http://www.w3.org/2000/01/rdf-schema#":
               case "http://lumii.lv/2011/1.0/owlgred#":
               case "http://www.w3.org/1999/02/22-rdf-syntax-ns#":
                  break;
               default:
                  if (defaultNamespace == null || !ns.equals(defaultNamespace)) {
                     OwlImportedSchema imported = new OwlImportedSchema(prefix, ns);
                     importedSchemas.put(prefix, imported);
                     importedSchemasFromNamespace.put(ns, imported);

                     if (schemasRepository.hasSchemaByNamespace(ns)) {
                        SchemasRepository.SchemaRep schemaRep = schemasRepository.getSchemaByNamespace(ns);
                        imported.setSchemaRep(schemaRep);
                     } else {
                        SchemasRepository.SchemaRep schemaRep = new SchemasRepository.SchemaRep(ns);
                        imported.setSchemaRep(schemaRep);
                     }
                     declaredSchemasFromNamespace.put(ns, imported.getSchemaRep());
                  }
                  break;
            }
         }
      }
      if (defaultNamespace == null && representationType == OwlRepresentationType.TYPE_OWL_XML && file != null) {
         String about = getAbout() + "#";
         it = prefixToNamespace.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String prefix = entry.getKey();
            String ns = NamespaceUtils.fixNamespace(entry.getValue());
            if (ns.endsWith(about)) {
               defaultNamespace = ns;
               defaultPrefix = prefix;
               break;
            }
         }
      }
      if (defaultNamespace != null) {
         int index = defaultNamespace.lastIndexOf("/");
         if (index != -1) {
            defaultSquashedNamespace = defaultNamespace.substring(0, index + 1);
         } else {
            defaultSquashedNamespace = defaultNamespace;
         }
      }
      if (file != null) {
         setupNamespaceFromFile();
      }
   }

   private void setupNamespaceFromFile() {
      String uriAsString = file.toURI().toString();
      int index = uriAsString.lastIndexOf('/');
      if (index <= 0) {
         namespaceFromFile = null;
      } else {
         // file:///D:/Java/ProceduresOntology/v21/FalconProcedures#
         String startPart = uriAsString.substring(0, index);
         if (startPart.startsWith("file:/")) {
            startPart = "file:///" + startPart.substring(6);
            namespaceFromFile = startPart;
         } else {
            namespaceFromFile = null;
         }
      }
   }

   private String getAbout() {
      try {
         String about = null;
         try ( BufferedReader reader = new BufferedReader(new FileReader(file))) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader xmlreader = factory.createXMLEventReader(reader);

            int index = 0;
            OUTER:
            while (xmlreader.hasNext()) {
               XMLEvent xmlevt = xmlreader.nextEvent();
               if (xmlevt.isStartElement()) {
                  StartElement elt = xmlevt.asStartElement();
                  switch (index) {
                     case 0: {
                        String name = elt.getName().getLocalPart();
                        if (name.equals("RDF")) {
                           index++;
                        } else {
                           break OUTER;
                        }
                        break;
                     }
                     case 1: {
                        String name = elt.getName().getLocalPart();
                        if (name.equals("Ontology")) {
                           QName qname = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about");
                           Attribute attr = elt.getAttributeByName(qname);
                           if (attr != null) {
                              about = attr.getValue();
                              break OUTER;
                           }
                        } else {
                           break OUTER;
                        }
                        break;
                     }
                     default:
                        break OUTER;
                  }
               }
            }
         }
         return about;

      } catch (IOException | XMLStreamException ex) {
         return null;
      }
   }

   public Map<String, OwlImportedSchema> getImportedSchemas() {
      return importedSchemas;
   }

   /**
    * Return true if a prefix correspond to an imported schema.
    *
    * @param prefix the prefix
    * @return true if the prefix correspond to an imported schema
    */
   public boolean hasImportedSchema(String prefix) {
      return importedSchemas.containsKey(prefix);
   }

   public OwlImportedSchema getImportedSchema(String prefix) {
      return importedSchemas.get(prefix);
   }

   /**
    * Return true if a namespace correspond to an imported schema.
    *
    * @param namespace the namespace
    * @return true if the prefix correspond to an imported schema
    */
   public boolean hasImportedSchemaFromNamespace(String namespace) {
      return importedSchemasFromNamespace.containsKey(namespace);
   }

   public Map<String, OwlImportedSchema> getImportedSchemasByNamespace() {
      return importedSchemasFromNamespace;
   }

   /**
    * Return the schemas on which this schema depends on by namespace.
    *
    * @return the schemas
    */
   @Override
   public Map<String, OwlDeclaredSchema> getDependenciesByNamespace() {
      return declaredSchemasFromNamespace;
   }

   /**
    * Set the default namespace. It will only be perfoemd if it is not previously defined.
    *
    * @param defaultPrefix the default prefix
    * @param defaultNamespace the default namespace
    */
   public void setDefaultNamespace(String defaultPrefix, String defaultNamespace) {
      if (this.defaultNamespace == null) {
         this.defaultNamespace = defaultNamespace;
         this.defaultPrefix = defaultPrefix;
         if (importedSchemas.containsKey(defaultPrefix)) {
            importedSchemas.remove(defaultPrefix);
            importedSchemasFromNamespace.remove(defaultNamespace);
            declaredSchemasFromNamespace.remove(defaultNamespace);
         }
      }
   }

   /**
    * Return the default namespace.
    *
    * @return the default namespace
    */
   public String getDefaultNamespace() {
      return defaultNamespace;
   }

   /**
    * Return the default squashed namespace.
    *
    * @return the default squashed namespace
    */
   public String getDefaultSquashedNamespace() {
      return defaultSquashedNamespace;
   }

   /**
    * Return true if there is a default namespace.
    *
    * @return true if there is a default namespace
    */
   public boolean hasDefaultNamespace() {
      return defaultNamespace != null;
   }

   /**
    * Return the prefix for the default namespace.
    *
    * @return the prefix for the default namespace
    */
   public String getDefaultPrefix() {
      return defaultPrefix;
   }

   /**
    * Return true if there is a default prefix.
    *
    * @return true if there is a default prefix
    */
   public boolean hasDefaultPrefix() {
      return defaultPrefix != null;
   }

   /**
    * Return true if the Schema has a namespace for a specified prefix.
    *
    * @param prefix the prefix
    * @return true if the Schema has a namespace for the specified prefix
    */
   public boolean hasNamespaceFromPrefix(String prefix) {
      return prefixToNamespace.containsKey(prefix);
   }

   /**
    * Return the namespace for a specified prefix.
    *
    * @param prefix the prefix
    * @return the namespace
    */
   public String getNamespaceFromPrefix(String prefix) {
      return prefixToNamespace.get(prefix);
   }

   /**
    * Return the map from prefix to namespaces.
    *
    * @return the map from prefix to namespaces
    */
   public Map<String, String> getPrefixToNamespaceMap() {
      return prefixToNamespace;
   }

   /**
    * Return the map from namespaces to prefix.
    *
    * @return the map from namespaces to prefix
    */
   public Map<String, String> getPrefixMap() {
      return prefixMap;
   }

   /**
    * Return true if there is a prefix for a namespace.
    *
    * @param namespace the namespace
    * @return true if there is a prefix for the namespace
    */
   public boolean hasPrefix(String namespace) {
      return prefixMap.containsKey(namespace);
   }

   /**
    * Return the prefix for a namespace.
    *
    * @param namespace the namespace
    * @return the prefix
    */
   public String getPrefix(String namespace) {
      return prefixMap.get(namespace);
   }

   /**
    * Return the namespaces.
    *
    * @return the namespaces
    */
   public Set<String> getNamespaces() {
      return namespaces;
   }

   public String getPotentialNamespaceFromFile() {
      return namespaceFromFile;
   }

   /**
    * Return the namespace of a resource.
    *
    * @param resource the resource
    * @return the namespace
    */
   public String getNamespace(Resource resource) {
      if (defaultNamespace == null) {
         return resource.getNameSpace();
      } else {
         String namespace = resource.getNameSpace();
         if (namespace.startsWith(namespaceFromFile)) {
            return defaultNamespace;
         } else {
            return resource.getNameSpace();
         }
      }
   }

   /**
    * Return the Thing class.
    *
    * @return the Thing class
    */
   public OwlClass getThingClass() {
      if (owlThingClass == null) {
         OntClass thingClass = ontModel.getOntClass("http://www.w3.org/2002/07/owl#Thing");
         owlThingClass = new OwlClass(thingClass);
      }
      return owlThingClass;
   }

   /**
    * Set if this schema is including individuals.
    *
    * @param includeIndividuals true if this schema is set to include individuals
    */
   public void setIncludeIndividuals(boolean includeIndividuals) {
      this.includeIndividuals = includeIndividuals;
   }

   /**
    * Return true if this schema is set including individuals.
    *
    * @return true if this schema is set to include individuals
    */
   public boolean isIncludingIndividuals() {
      return includeIndividuals;
   }

   /**
    * Return true if there are packages in the model.
    *
    * @return true if there are packages in the model
    */
   public boolean hasPackages() {
      return packages != null;
   }

   /**
    * Set the packages in the model.
    *
    * @param packages the packages
    */
   public void setPackages(Map<ElementKey, OwlClass> packages) {
      this.packages = packages;
   }

   /**
    * Return the packages in the model. Note that the returned value may be null if the model was not created with packages.
    *
    * @return the packages
    */
   public Map<ElementKey, OwlClass> getPackages() {
      return packages;
   }

   /**
    * Return true if a class is a package.
    *
    * @param key the class key
    * @return true if the class is a package
    */
   public boolean isPackage(ElementKey key) {
      if (packages == null) {
         return false;
      } else {
         return packages.containsKey(key);
      }
   }

   /**
    * Add an individual.
    *
    * @param individual the individual
    */
   public void addIndividual(OwlIndividual individual) {
      addNamespace(individual);
      individuals.put(individual.getKey(), individual);
      Iterator<OwlClass> it = individual.getParentClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         theClass.addIndividual(individual);
      }
   }

   public void addDatatype(OwlDatatype datatype) {
      datatypes.put(datatype.getKey(), datatype);
   }

   public Map<ElementKey, OwlDatatype> getDatatypes() {
      return datatypes;
   }

   public boolean hasDatatype(ElementKey key) {
      return datatypes.containsKey(key);
   }

   public OwlDatatype getDatatype(ElementKey key) {
      return datatypes.get(key);
   }

   public boolean hasElement(URI uri) {
      String uriAsString = uri.toString();
      if (uriAsString.contains("#")) {
         String namespace = uriAsString.substring(0, uriAsString.indexOf('#') + 1);
         String name = uriAsString.substring(uriAsString.indexOf('#') + 1);
         ElementKey key = ElementKey.create(namespace, name);
         if (individuals.containsKey(key)) {
            return true;
         } else if (classes.containsKey(key)) {
            return true;
         } else if (properties.containsKey(key)) {
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public NamedOwlElement getElement(URI uri) {
      String uriAsString = uri.toString();
      if (uriAsString.contains("#")) {
         String namespace = uriAsString.substring(0, uriAsString.indexOf('#') + 1);
         String name = uriAsString.substring(uriAsString.indexOf('#') + 1);
         ElementKey key = ElementKey.create(namespace, name);
         if (individuals.containsKey(key)) {
            return individuals.get(key);
         } else if (classes.containsKey(key)) {
            return classes.get(key);
         } else if (properties.containsKey(key)) {
            return properties.get(key);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
   
   public void setHasForeignElements(boolean hasForeignElements) {
      this.hasForeignElements = hasForeignElements;
   }
   
   public boolean hasForeignElements() {
      return hasForeignElements;
   }
   
   public void setHasNonForeignElements(boolean hasNonForeignElements) {
      this.hasNonForeignElements = hasNonForeignElements;
   }
   
   public boolean hasNonForeignElements() {
      return hasNonForeignElements;
   }   

   /**
    * Return true if the schema has no classes, individuals, and properties.
    *
    * @return true if the schema has no classes, individuals, and properties
    */
   public boolean isEmpty() {
      return properties.isEmpty() && classes.isEmpty() && individuals.isEmpty();
   }

   /**
    * Return the individuals.
    *
    * @return the individuals
    */
   public Map<ElementKey, OwlIndividual> getIndividuals() {
      return individuals;
   }

   /**
    * Return true if there is ajn individual for a specified key.
    *
    * @param key the key
    * @return true if there is ajn individual for the specified key
    */
   public boolean hasIndividual(ElementKey key) {
      return individuals.containsKey(key);
   }

   /**
    * Return the individual of a specified key.
    *
    * @param key the key
    * @return the individual
    */
   public OwlIndividual getIndividual(ElementKey key) {
      return individuals.get(key);
   }

   public OwlAnnotation getOrCreateAnnotation(ElementKey annotationKey) {
      if (hasElementAnnotation(annotationKey)) {
         return getElementAnnotation(annotationKey);
      } else {
         OwlAnnotation annotation = new OwlAnnotation(annotationKey);
         addElementAnnotation(annotation);
         return annotation;
      }
   }

   /**
    * Add an annotation.
    *
    * @param annotation the annotation
    */
   public void addElementAnnotation(OwlAnnotation annotation) {
      addNamespace(annotation);
      elementsAnnotations.put(annotation.getKey(), annotation);
   }

   /**
    * Return true if there is an annotation for a specified key.
    *
    * @param key the key
    * @return true if there is an annotation for the specified key
    */
   public boolean hasElementAnnotation(ElementKey key) {
      return elementsAnnotations.containsKey(key);
   }

   /**
    * Return the annotation for a specified key.
    *
    * @param key the key
    * @return the annotation
    */
   public OwlAnnotation getElementAnnotation(ElementKey key) {
      return elementsAnnotations.get(key);
   }

   /**
    * Return the annotations.
    *
    * @return the annotations
    */
   public Map<ElementKey, OwlAnnotation> getElementAnnotations() {
      return elementsAnnotations;
   }

   /**
    * Add an Owl class.
    *
    * @param owlClass the owl class
    */
   public void addOwlClass(OwlClass owlClass) {
      addNamespace(owlClass);
      classes.put(owlClass.getKey(), owlClass);
   }

   private void addNamespace(NamedOwlElement element) {
      String namespace = element.getNamespace();
      if (namespace != null && !namespaces.contains(namespace)) {
         namespaces.add(namespace);
      }
   }

   /**
    * Return the Owl classes.
    *
    * @return the Owl classes
    */
   public Map<ElementKey, OwlClass> getOwlClasses() {
      return classes;
   }

   /**
    * Return true if there is a Owl class for a specified key.
    *
    * @param key the key
    * @return true if there is a Owl class for the specified key
    */
   public boolean hasOwlClass(ElementKey key) {
      return classes.containsKey(key);
   }

   /**
    * Return the Owl class of a specified key.
    *
    * @param key the key
    * @return the Owl class
    */
   public OwlClass getOwlClass(ElementKey key) {
      return classes.get(key);
   }

   /**
    * Add an Owl property.
    *
    * @param owlProperty the Owl property
    */
   public void addOwlProperty(OwlProperty owlProperty) {
      addNamespace(owlProperty);
      properties.put(owlProperty.getKey(), owlProperty);
      if (owlProperty instanceof OwlDatatypeProperty) {
         datatypeProperties.put(owlProperty.getKey(), (OwlDatatypeProperty) owlProperty);
      } else if (owlProperty instanceof OwlObjectProperty) {
         objectProperties.put(owlProperty.getKey(), (OwlObjectProperty) owlProperty);
      }
   }

   /**
    * Return true if there is an Owl Datatype property for a specified key.
    *
    * @param key the key
    * @return true if there is an Owl Datatype property for the specified key
    */
   public boolean hasOwlDatatypeProperty(ElementKey key) {
      if (properties.containsKey(key)) {
         OwlProperty property = properties.get(key);
         return property.isDatatypeProperty();
      } else {
         return false;
      }
   }

   /**
    * Return true if there is an Owl property for a specified key.
    *
    * @param key the key
    * @return true if there is an Owl property for the specified key
    */
   public boolean hasOwlProperty(ElementKey key) {
      return properties.containsKey(key);
   }

   /**
    * Return the Owl properties.
    *
    * @return the Owl properties
    */
   public Map<ElementKey, OwlProperty> getOwlProperties() {
      return properties;
   }

   /**
    * Return the datatypes properties.
    *
    * @return the properties
    */
   public Map<ElementKey, OwlDatatypeProperty> getOwlDatatypeProperties() {
      return datatypeProperties;
   }

   /**
    * Return the object properties.
    *
    * @return the properties
    */
   public Map<ElementKey, OwlObjectProperty> getOwlObjectProperties() {
      return objectProperties;
   }

   /**
    * Return the Owl property of a specified key.
    *
    * @param key the key
    * @return the Owl property
    */
   public OwlProperty getOwlProperty(ElementKey key) {
      return properties.get(key);
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param key the class key
    * @return the dependant classes
    */
   public Map<ElementKey, OwlClass> getDependentClasses(ElementKey key) {
      if (!hasOwlClass(key)) {
         return null;
      } else {
         OwlClass theClass = getOwlClass(key);
         return getDependentClasses(theClass);
      }
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param key the class key
    * @param filter the filter
    * @return the dependant classes
    */
   public Map<ElementKey, OwlClass> getDependentClasses(ElementKey key, ElementFilter filter) {
      if (!hasOwlClass(key)) {
         return null;
      } else {
         OwlClass theClass = getOwlClass(key);
         return getDependentClasses(theClass, filter);
      }
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param theClass the class
    * @return the dependant classes
    */
   public Map<ElementKey, OwlClass> getDependentClasses(OwlClass theClass) {
      ElementFilter filter = new ElementFilter();
      return SchemaUtils.getDependentClasses(theClass, filter);
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param theClass the class
    * @param filter the filter
    * @return the dependant classes
    */
   public Map<ElementKey, OwlClass> getDependentClasses(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDependentClasses(theClass, filter);
   }

   @Override
   public OwlSchema clone() {
      try {
         Object o = super.clone();
         return (OwlSchema) o;
      } catch (CloneNotSupportedException ex) {
         return null;
      }
   }

   @Override
   public void accept(ElementVisitor visitor) {
      boolean cont = visitor.visit(this);
      if (cont) {
         Iterator<OwlDatatype> it = datatypes.values().iterator();
         while (it.hasNext()) {
            OwlDatatype theDatatype = it.next();
            theDatatype.accept(visitor);
         }
         Iterator<OwlClass> it4 = classes.values().iterator();
         while (it4.hasNext()) {
            OwlClass theClass = it4.next();
            theClass.accept(visitor);
         }
         Iterator<OwlProperty> it2 = properties.values().iterator();
         while (it2.hasNext()) {
            OwlProperty theProperty = it2.next();
            theProperty.accept(visitor);
         }
         Iterator<OwlIndividual> it3 = individuals.values().iterator();
         while (it3.hasNext()) {
            OwlIndividual theIndividual = it3.next();
            theIndividual.accept(visitor);
         }
      }
   }

   /**
    * Return the prefix for the default namespace.
    *
    * @return the prefix for the default namespace
    */
   @Override
   public String getPrefix() {
      return defaultPrefix;
   }

   /**
    * Return the URI of the default namespace.
    *
    * @return the URI
    */
   @Override
   public URI toURI() {
      try {
         URL url = new URL(defaultNamespace);
         return url.toURI();
      } catch (MalformedURLException | URISyntaxException ex) {
         return null;
      }
   }

   @Override
   public String getName() {
      return getNamespace();
   }

   @Override
   public String getDisplayedName() {
      return getPrefixedDisplayedName();
   }

   @Override
   public String getPrefixedDisplayedName() {
      if (defaultPrefix != null) {
         return defaultPrefix + ":" + defaultNamespace;
      } else {
         return defaultNamespace;
      }
   }

   @Override
   public String getNamespace() {
      return defaultNamespace;
   }

   @Override
   public ElementKey getKey() {
      return null;
   }
}
