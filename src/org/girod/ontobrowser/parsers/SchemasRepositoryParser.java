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
package org.girod.ontobrowser.parsers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.NamespaceUtils;
import org.girod.ontobrowser.model.SchemasRepository;
import org.mdiutil.xml.XMLSAXParser;
import org.mdiutil.xml.swing.BasicSAXHandler;
import org.xml.sax.Attributes;

/**
 * This class allows to parse the schemas repository configuration.
 *
 * @version 0.11
 */
public class SchemasRepositoryParser extends BasicSAXHandler {
   private final SchemasRepository schemaRepository = SchemasRepository.getInstance();
   private SchemasRepository.SchemaRep schemaRep = null;
   private final Map<String, Set<String>> dependenciesValues = new HashMap<>();

   public SchemasRepositoryParser() {
   }

   public void parse(URL url) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      XMLSAXParser parser = new XMLSAXParser(this);
      parser.setSchema(conf.getSchemasRepositorySchema());
      parser.parse(url);
   }

   public void parse(File file) {
      try {
         parse(file.toURI().toURL());
      } catch (MalformedURLException ex) {
      }
   }

   /**
    * Receive notification of the beginning of an element.
    *
    */
   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) {
      switch (qname) {
         case "ontology":
            parseOntology(attr);
            break;
         case "dependsOn":
            if (schemaRep != null) {
               parseDependsOn(attr);
            }
            break;
      }
   }

   @Override
   public void endElement(String uri, String localname, String qname) {
      switch (qname) {
         case "ontologies":
            resolve();
            break;
      }
   }

   private void resolve() {
      Map<String, SchemasRepository.SchemaRep> schemasBynamespaces = schemaRepository.getSchemasByNamespace();      
      Iterator<Entry<String, Set<String>>> it = dependenciesValues.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, Set<String>> entry = it.next();
         String name = entry.getKey();
         SchemasRepository.SchemaRep theSchema = schemaRepository.getSchema(name);
         Iterator<String> it2 = entry.getValue().iterator();
         while (it2.hasNext()) {
            String namespace = it2.next();
            if (schemasBynamespaces.containsKey(namespace)) {
               SchemasRepository.SchemaRep dependsOn = schemasBynamespaces.get(namespace);
               theSchema.addDependency(dependsOn);
            }
         }
      }
   }

   private void parseOntology(Attributes attr) {
      String name = null;
      String family = null;
      String namespace = null;
      URL uri = null;
      String description = null;
      schemaRep = null;

      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         switch (attrname) {
            case "name":
               name = attrvalue;
               break;
            case "family":
               family = attrvalue;
               break;
            case "uri":
               try {
               uri = new URL(attrvalue);
            } catch (MalformedURLException ex) {
            }
            break;
            case "namespace":
               namespace = NamespaceUtils.fixNamespace(attrvalue);
               break;
            case "description":
               description = attrvalue;
               break;
            default:
               break;
         }
      }
      if (name != null && namespace != null) {
         schemaRep = schemaRepository.addSchema(name, namespace);
         if (schemaRep != null) {
            schemaRep.setFamily(family);
            schemaRep.setDescription(description);
            schemaRep.setURL(uri);
         }
      }
   }

   private void parseDependsOn(Attributes attr) {
      String namespace = null;

      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         if (attrname.equals("namespace")) {
            namespace = attrvalue;
         }
      }
      if (namespace != null) {
         Set<String> namespaces;
         String name = schemaRep.getName();
         if (dependenciesValues.containsKey(name)) {
           namespaces = dependenciesValues.get(name);            
         } else {
            namespaces = new HashSet<>();
            dependenciesValues.put(name, namespaces);
         }
         namespaces.add(namespace);
      }
   }
}
