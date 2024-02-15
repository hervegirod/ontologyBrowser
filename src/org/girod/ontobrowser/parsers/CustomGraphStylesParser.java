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

import java.awt.Color;
import java.io.File;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.mdiutil.lang.swing.HTMLSwingColors;
import org.mdiutil.xml.XMLSAXParser;
import org.mdiutil.xml.swing.BasicSAXHandler;
import org.xml.sax.Attributes;

/**
 * This class allows to parse the custom colors for the browser.
 *
 * @version 0.11
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
        } else if (qname.equals("diagramLayout")) {
            parseDiagramLayout(attr);            
        } else if (qname.equals("background") && currentStyle != null) {
            parseBackgroundColor(attr);
        } else if (qname.equals("property") && currentStyle != null) {
            parseProperty(attr);            
        }
    }
    
    private void parseDiagramLayout(Attributes attr) {
        for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            if (attrname.equals("distance")) {
                try {
                    float distance = Float.parseFloat(attrvalue);
                    if (distance > 0) {
                        graphStyles.setLayoutDistance(distance);
                    }
                } catch (NumberFormatException e) {
                }
            } else if (attrname.equals("maximumSteps")) {
                try {
                    int maximumSteps = Integer.parseInt(attrvalue);
                    if (maximumSteps > 0) {
                        graphStyles.setLayoutMaximumSteps(maximumSteps);
                    }
                } catch (NumberFormatException e) {
                }
            }
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
