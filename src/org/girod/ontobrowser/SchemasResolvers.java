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
the project website at the project page on https://github.com/hervegirod/owlToGraph
 */
package org.girod.ontobrowser;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.jena.riot.system.stream.JenaIOEnvironment;
import org.apache.jena.riot.system.stream.LocationMapper;
import org.mdiutil.lang.ResourceLoader;

/**
 * The schemas resolver.
 *
 * @since 0.7
 */
public class SchemasResolvers {
   private static SchemasResolvers resolvers = null;

   private SchemasResolvers() {
   }

   public static SchemasResolvers getInstance() {
      if (resolvers == null) {
         resolvers = new SchemasResolvers();
      }
      return resolvers;
   }

   /**
    * Set the alternate locations for schemas.
    */
   public void setAlternateLocations() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      setAlternateLocations(conf.getAlternateLocations(), conf.useBuiltinSchemas);
   }

   /**
    * Set the alternate locations for schemas.
    *
    * @param alternateLocations the schemas alternate locations
    * @param useBuiltinSchemas true fi built-in shcemas are used
    */
   public void setAlternateLocations(File[] alternateLocations, boolean useBuiltinSchemas) {
      LocationMapper mapper = new LocationMapper();
      JenaIOEnvironment.setGlobalLocationMapper(mapper);
      if (useBuiltinSchemas) {
         setBuiltinLocations(mapper);
      }
      if (alternateLocations != null && alternateLocations.length != 0) {
         for (int i = 0; i < alternateLocations.length; i++) {
            File schemaFile = alternateLocations[i];
            mapper.altMapping(schemaFile.toURI().toString());
         }
      }
   }

   private void setBuiltinLocations(LocationMapper mapper) {
      ResourceLoader loader = new ResourceLoader("org/girod/ontobrowser/standard");
      try {
         URL url = loader.getURL("owl-time.owl");
         mapper.altMapping(url.toURI().toString());
         url = loader.getURL("dublin_core_elements.rdf");
         mapper.altMapping(url.toURI().toString());         
         url = loader.getURL("geo.ttl");
         mapper.altMapping(url.toURI().toString());
         url = loader.getURL("bfo.owl");
         mapper.altMapping(url.toURI().toString());         
      } catch (URISyntaxException ex) {
      }
   }
}
