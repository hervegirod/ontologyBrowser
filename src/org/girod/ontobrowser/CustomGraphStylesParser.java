/*
 * Dassault Aviation confidential
 * Copyright (c) 2021, Dassault Aviation. All rights reserved.
 *
 * This file is part of the FICOLab project.
 *
 * NOTICE: All information contained herein is, and remains the property of Dassault Aviation.
 * The intellectual and technical concepts contained herein are proprietary to Dassault Aviation,
 * and are protected by trade secret or copyright law.

 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * It can not be copied and/or distributed, in source or binary form, without the express permission of
 * Dassault Aviation.
 */
package org.girod.ontobrowser;

import java.awt.Color;
import java.io.File;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.mdiutil.lang.swing.HTMLSwingColors;
import org.mdiutil.xml.XMLSAXParser;
import org.mdiutil.xml.swing.BasicSAXHandler;
import org.xml.sax.Attributes;

/**
 * This class allows to parse the custom colors for the browser.
 *
 * @since 0.4
 */
public class CustomGraphStylesParser extends BasicSAXHandler {
   private CustomGraphStyles graphStyles;
   private CustomGraphStyles.ElementStyle currentStyle = null;
   

   public CustomGraphStylesParser() {
   }

   public void parse(File file) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      graphStyles = conf.getCustomGraphStyles();
      graphStyles.reset();
      XMLSAXParser parser = new XMLSAXParser(this);
      parser.setSchema(conf.getGraphStylesSchema());
      parser.parse(file);
   }

   /**
    * Receive notification of the beginning of an element.
    *
    */
   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) {
      if (qname.equals("element")) {
         parseElement(attr);
      } else if (qname.equals("background") && currentStyle != null) {
         parseBackgroundColor(attr);
      } else if (qname.equals("property") && currentStyle != null) {
         parseProperty(attr);         
      }
   }
   
   private void parseElement(Attributes attr) {
      currentStyle = null;
      
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         if (attrname.equals("type")) {
            switch (attrvalue) {
               case "class":
                  currentStyle = graphStyles.getStyle(CustomGraphStyles.CLASS);
                  break;
               case "package":
                  currentStyle = graphStyles.getStyle(CustomGraphStyles.PACKAGE);
                  break;
               case "externalPackage":
                  currentStyle = graphStyles.getStyle(CustomGraphStyles.EXTERNAL_PACKAGE);
                  break;      
               case "individual":
                  currentStyle = graphStyles.getStyle(CustomGraphStyles.INDIVIDUAL);
                  break;   
               case "property":
                  currentStyle = graphStyles.getStyle(CustomGraphStyles.PROPERTY);
                  break;                    
            }
         }
      }
   }

   private void parseBackgroundColor(Attributes attr) {
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         switch (attrname) {
            case "color": {
               Color col = HTMLSwingColors.decodeColor(attrvalue);
               currentStyle.backgroundColor = col;
               break;
            }
         }
      }
   }
   
   private void parseProperty(Attributes attr) {
      String key = null;
      String value = null;
      
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         switch (attrname) {
            case "key":
               key = attrvalue;
               break;
            case "value":
               value = attrvalue;
               break;               
         }
      }
      if (key != null && value != null) {
         if (currentStyle.type == CustomGraphStyles.PACKAGE) {
            if (key.equals("showInnerGraphDisplay")) {
               graphStyles.setShowInnerGraphDisplay(value.equals("true"));
            }
         }
      }
   }   
}
