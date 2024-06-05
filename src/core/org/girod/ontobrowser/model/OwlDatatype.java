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

/**
 * Represents an Owl datatype.
 *
 * @version 0.13
 */
public class OwlDatatype extends NamedOwlElement<OwlDatatype> implements BaseTypes {
   public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema#";
   private short type = CUSTOM;

   public OwlDatatype(ElementKey key) {
      super(key.getNamespace(), key.getName());
      setupTypeImpl();
   }

   public OwlDatatype(String namespace, String name) {
      super(namespace, name);
      setupTypeImpl();
   }

   public short getType() {
      return type;
   }

   private void setupTypeImpl() {
      if (namespace.equals(XML_SCHEMA)) {
         switch (name) {
            case "int":
               type = INT;
               break;
            case "nonNegativeInteger":
               type = NON_NEGATIVE_INT;
               break;
            case "positiveInteger":
               type = POSITIVE_INT;
               break;
            case "boolean":
               type = BOOLEAN;
               break;
            case "float":
               type = FLOAT;
               break;
            case "double":
               type = DOUBLE;
               break;
            case "long":
               type = LONG;
               break;
            case "short":
               type = SHORT;
               break;
            case "string":
               type = STRING;
               break;
            default:
               type = CUSTOM;
         }
      }
   }

   @Override
   public String toString() {
      if (namespace.equals(XML_SCHEMA)) {
         return "xs:" + name;
      } else {
         return super.toString();
      }
   }

   @Override
   public OwlDatatype clone() {
      Object o = super.clone();
      return (OwlDatatype) o;
   }

   @Override
   public void accept(ElementVisitor visitor) {
      visitor.visit(this);
   }
}
