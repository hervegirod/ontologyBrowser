/*
Copyright (c) 2024 Hervé Girod
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

import java.util.Map;
import org.xml.sax.SAXParseException;

/**
 * The XML handler is fired from XML parsing events.
 *
 * @since 0.13
 */
public interface XMLHandler {
   /**
    * Called if an exception occur when parsing the an XML file.
    *
    * @param e the exception
    */
   public default void fireXMLException(Exception e) {
   }

   /**
    * Called if a SAX exception occur when parsing the an XML file.
    *
    * @param e the SAX exception
    * @param exceptionType the SAX exception type (see {@link XMLExceptionType})
    */
   public default void xmlError(SAXParseException e, short exceptionType) {
   }

   /**
    * Called for the start of an XML element.
    *
    * @param qName the element qualified name
    * @param attributes the element attributes
    */
   public default void startXMLElement(String qName, Map<String, String> attributes) {
   }

   /**
    * Called for the end of an XML element.
    *
    * @param qName the element qualified name
    * @param cdata the element cdata (null if empty)
    */
   public default void endXMLElement(String cdata, String qName) {
   }
}
