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
package org.girod.ontobrowser.parsers;

import java.io.File;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.PackageConfigType;
import org.girod.ontobrowser.model.PackagesConfiguration;
import org.mdiutil.xml.XMLSAXParser;
import org.mdiutil.xml.swing.BasicSAXHandler;
import org.xml.sax.Attributes;

/**
 * This class allows to parse the packages configuration.
 *
 * @version 0.8
 */
public class PackagesConfigurationParser extends BasicSAXHandler {
   private PackagesConfiguration packagesConfiguration;

   public PackagesConfigurationParser() {
   }

   public void parse(File file) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      packagesConfiguration = conf.getPackagesConfiguration();
      packagesConfiguration.reset();
      XMLSAXParser parser = new XMLSAXParser(this);
      parser.setSchema(conf.getPackagesConfigurationSchema());
      parser.parse(file);
   }

   /**
    * Receive notification of the beginning of an element.
    *
    */
   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) {
      switch (qname) {
         case "packagesConfiguration":
            parseDefaults(attr);
            break;
         case "forgetPackage":
            addPackage(attr, true);
            break;
         case "forgetNamespace":
            parseNamespace(attr);
            break;
         case "forcePackage":
            addPackage(attr, false);
            break;
         default:
            break;
      }
   }

   private void parseDefaults(Attributes attr) {
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         if (attrname.equals("acceptDefaults")) {
            packagesConfiguration.acceptDefaults(attrvalue.equals("true"));
         }
      }
   }

   private void parseNamespace(Attributes attr) {
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         if (attrname.equals("namespace")) {
            packagesConfiguration.forgetNamespace(attrvalue);
         }
      }
   }

   private void addPackage(Attributes attr, boolean forget) {
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         if (attrname.equals("path")) {
            String path = attrvalue;
            int index = path.lastIndexOf('#');
            ElementKey key;
            if (index != -1) {
               String name = path.substring(index + 1);
               if (index != 0) {
                  String namespace = path.substring(0, index + 1);
                  key = new ElementKey(namespace, name);
               } else {
                  key = new ElementKey(name);
               }
            } else {
               key = new ElementKey(path);
            }
            if (forget) {
               packagesConfiguration.addClass(key, PackageConfigType.FORGET_PACKAGE);
            } else {
               packagesConfiguration.addClass(key, PackageConfigType.FORCE_PACKAGE);
            }
         }
      }
   }
}
