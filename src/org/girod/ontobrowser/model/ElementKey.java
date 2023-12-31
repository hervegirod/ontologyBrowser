/*
Copyright (c) 2021, 2023 Hervé Girod
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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * The element key, with a name and namespace.
 *
 * @version 0.8
 */
public class ElementKey implements Comparable<ElementKey>, Cloneable, Serializable {
   public static final String XML_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
   /**
    * The Thing key.
    */
   public static final ElementKey THING = ElementKey.create("http://www.w3.org/2002/07/owl#", "Thing");
   private final String namespace;
   private final String name;

   public ElementKey(String name) {
      this.namespace = null;
      this.name = name;
   }

   public ElementKey(OwlSchema schema, String prefix, String name) {
      if (schema.hasNamespaceFromPrefix(prefix)) {
         this.namespace = schema.getNamespaceFromPrefix(prefix);
         this.name = name;
      } else {
         this.namespace = null;
         this.name = name;
      }
   }

   public ElementKey(String namespace, String name) {
      this.namespace = namespace;
      this.name = name;
   }

   /**
    * Return true if this key represents the Thing class.
    *
    * @return true if this key represents the Thing class
    */
   public boolean isThing() {
      return this.equals(ElementKey.THING);
   }

   /**
    * Return the prefixed name for the key.
    *
    * @param schema the schema
    * @return the prefixed name
    */
   public String getPrefixedName(OwlSchema schema) {
      if (namespace == null) {
         return name;
      } else {
         String prefix = schema.getPrefix(namespace);
         if (prefix == null) {
            return name;
         } else {
            return prefix + ":" + name;
         }
      }
   }

   public URI toURI() {
      String uriAsString = namespace + name;
      try {
         return new URI(uriAsString);
      } catch (URISyntaxException ex) {
         return null;
      }
   }

   public static ElementKey createFromURI(String uriAsString) {
      if (uriAsString.contains("#")) {
         int index = uriAsString.indexOf("#");
         String namespace = uriAsString.substring(0, index + 1);
         String name = uriAsString.substring(index + 1);
         return create(namespace, name);
      } else {
         return null;
      }
   }

   public static ElementKey create(String name) {
      ElementKey key = new ElementKey(name);
      return key;
   }

   public static ElementKey create(String namespace, String name) {
      ElementKey key = new ElementKey(namespace, name);
      return key;
   }

   @Override
   public ElementKey clone() {
      try {
         Object o = super.clone();
         return (ElementKey) o;
      } catch (CloneNotSupportedException ex) {
         return null;
      }
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 79 * hash + Objects.hashCode(this.namespace);
      hash = 79 * hash + Objects.hashCode(this.name);
      return hash;
   }

   @Override
   public String toString() {
      if (namespace == null) {
         return name;
      } else {
         return namespace + name;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final ElementKey other = (ElementKey) obj;
      if (!Objects.equals(this.namespace, other.namespace)) {
         return false;
      }
      if (!Objects.equals(this.name, other.name)) {
         return false;
      }
      return true;
   }

   @Override
   public int compareTo(ElementKey o) {
      if (o.namespace == null && namespace == null) {
         return name.compareTo(o.name);
      } else if (o.namespace == null) {
         return 1;
      } else if (namespace == null) {
         return -1;
      } else {
         int compared = namespace.compareTo(o.namespace);
         if (compared == 0) {
            return name.compareTo(o.name);
         } else {
            return compared;
         }
      }
   }

   public String getName() {
      return name;
   }

   public String getNamespace() {
      return namespace;
   }
}
