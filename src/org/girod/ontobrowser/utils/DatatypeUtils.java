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
package org.girod.ontobrowser.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jena.rdf.model.Literal;

/**
 * THe datatype utilities class allows to get the numeric value of a litteral.
 *
 * @since 0.4
 */
public class DatatypeUtils {
   private static final Pattern PAT_URI = Pattern.compile("xsd:(\\w+)");
   public static final short INT = 0;
   public static final short INTEGER = 1;
   public static final short NON_NEGATIVE_INTEGER = 2;
   public static final short NON_POSITIVE_INTEGER = 3;
   public static final short NEGATIVE_INTEGER = 4;
   public static final short POSITIVE_INTEGER = 5;
   public static final short UNSIGNED_INT = 6;
   public static final short LONG = 7;
   public static final short UNSIGNED_LONG = 8;
   public static final short SHORT = 9;
   public static final short UNSIGNED_SHORT = 10;

   private DatatypeUtils() {
   }

   /**
    * Get a parsed value of a literal as an int.
    *
    * @param literal the literal
    * @return the value as an int
    */
   public static int getValueAsInt(Literal literal) {
      String uri = literal.getDatatypeURI();
      String rawValue = literal.getString();
      Matcher m = PAT_URI.matcher(uri);
      if (m.matches()) {
         String typeS = m.group(1);
         switch (typeS) {
            case "int":
            case "integer":
            case "short":
            case "long":
               return parseInt(rawValue);
            case "positiveInteger":
               return parsePositiveInt(rawValue);
            case "nonPositiveInteger":
               return parseNonPositiveInt(rawValue);
            case "negativeInteger":
               return parseNegativeInt(rawValue);
            case "nonNegativeInteger":
               return parseNonNegativeInt(rawValue);
            default:
               return 0;
         }
      } else {
         return 0;
      }
   }

   private static int parseNonPositiveInt(String value) {
      try {
         int i = Integer.parseInt(value);
         return i > 0 ? i : -1;
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   private static int parsePositiveInt(String value) {
      try {
         int i = Integer.parseInt(value);
         return i > 0 ? i : 1;
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   private static int parseNonNegativeInt(String value) {
      try {
         int i = Integer.parseInt(value);
         return i < 0 ? 0 : i;
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   private static int parseNegativeInt(String value) {
      try {
         int i = Integer.parseInt(value);
         return i < 0 ? i : -1;
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   private static int parseInt(String value) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException e) {
         return 0;
      }
   }
}
