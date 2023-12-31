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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.parsers.SchemasRepositoryParser;

/**
 * The schemas repository.
 *
 * @since 0.8
 */
public class SchemasRepository {
   private static SchemasRepository repository = null;
   private final Map<String, SchemaRep> schemas = new HashMap<>();
   private final Map<String, SchemaRep> schemasByNamespace = new HashMap<>();

   private SchemasRepository() {
   }

   /**
    * Return the schemas repository.
    *
    * @return the schemas repository
    */
   public static SchemasRepository getInstance() {
      if (repository == null) {
         repository = new SchemasRepository();
         repository.reset();
      }
      return repository;
   }

   public SchemaRep addSchema(String name, String namespace) {
      if (!schemas.containsKey(name)) {
         SchemaRep schemaRep = new SchemaRep(name, namespace);
         schemas.put(name, schemaRep);
         schemasByNamespace.put(namespace, schemaRep);
         return schemaRep;
      } else {
         return null;
      }
   }

   public boolean hasSchema(String name) {
      return schemas.containsKey(name);
   }

   public SchemaRep getSchema(String name) {
      return schemas.get(name);
   }

   public Map<String, SchemaRep> getSchemas() {
      return schemas;
   }

   public boolean hasSchemaByNamespace(String namespace) {
      return schemasByNamespace.containsKey(namespace);
   }

   public SchemaRep getSchemaByNamespace(String namespace) {
      return schemasByNamespace.get(namespace);
   }

   public Map<String, SchemaRep> getSchemasByNamespace() {
      return schemasByNamespace;
   }

   /**
    * Reset the repository content.
    */
   public void reset() {
      schemas.clear();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      SchemasRepositoryParser parser = new SchemasRepositoryParser();
      parser.parse(conf.getDefaultSchemasRepository());
   }

   /**
    * Represents a schema representation.
    *
    * @since 0.8
    */
   public static class SchemaRep implements OwlDeclaredSchema, Cloneable {
      private final String name;
      private String prefix;
      private final String namespace;
      private URL uri = null;
      private String family = null;
      private String description = null;
      private final Map<String, SchemaRep> dependencies = new HashMap<>();
      private final Map<String, SchemaRep> dependenciesByNamespace = new HashMap<>();

      public SchemaRep(String namespace) {
         this.name = null;
         this.namespace = namespace;
      }

      private SchemaRep(String name, String namespace) {
         this.name = name;
         this.namespace = namespace;
      }

      public SchemaRep create(String prefix) {
         SchemaRep schemaRep = clone();
         schemaRep.prefix = prefix;
         return schemaRep;
      }

      @Override
      public SchemaRep clone() {
         try {
            SchemaRep schemaRep = (SchemaRep) super.clone();
            return schemaRep;
         } catch (CloneNotSupportedException ex) {
            return null;
         }
      }

      /**
       * Return true if the schema has a name.
       *
       * @return true if the schema has a name
       */
      @Override
      public boolean hasName() {
         return name != null;
      }

      /**
       * Return the Schema name.
       *
       * @return the name
       */
      @Override
      public String getName() {
         return name;
      }

      /**
       * Return the schema prefix in the declaration. May be null.
       *
       * @return the prefix
       */
      @Override
      public String getPrefix() {
         return prefix;
      }

      /**
       * Return the schema namespace.
       *
       * @return the namespace
       */
      @Override
      public String getNamespace() {
         return namespace;
      }

      public void setURL(URL uri) {
         this.uri = uri;
      }

      public URL getURL() {
         return uri;
      }

      /**
       * Return the URI of the default namespace.
       *
       * @return the URI
       */
      @Override
      public URI toURI() {
         try {
            return uri.toURI();
         } catch (URISyntaxException ex) {
            return null;
         }
      }

      /**
       * Set the schema family.
       *
       * @param family the family
       */
      public void setFamily(String family) {
         this.family = family;
      }

      /**
       * Return the schema family. The family is used for sveral schemas whgich are part of a family of schemas (for example, the various Dublin Core
       * schemas).
       *
       * @return the family
       */
      public String getFamily() {
         return family;
      }

      /**
       * Set the schema description.
       *
       * @param description the description
       */
      public void setDescription(String description) {
         this.description = description;
      }

      /**
       * Return the schema description.
       *
       * @return the description
       */
      public String getDescription() {
         return description;
      }

      public void addDependency(SchemaRep dependsOn) {
         if (dependsOn.name != null) {
            dependencies.put(dependsOn.name, dependsOn);
         }
         dependenciesByNamespace.put(dependsOn.namespace, dependsOn);
      }

      /**
       * Return the schemas on which this schema depends on by namespace.
       *
       * @return the schemas
       */
      @Override
      public Map<String, SchemaRep> getDependenciesByNamespace() {
         return dependenciesByNamespace;
      }

      public Map<String, SchemaRep> getDependencies() {
         return dependencies;
      }

      public boolean hasDependencies() {
         return !dependencies.isEmpty();
      }
   }
}
