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
package org.girod.ontobrowser.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.mdiutil.lang.swing.HTMLSwingColors;

/**
 * The graph style.
 *
 * @since 0.4
 */
public class CustomGraphStyles {
   public static final short CLASS = 0;
   public static final short PACKAGE = 1;
   public static final short EXTERNAL_PACKAGE = 2;
   public static final short INDIVIDUAL = 3;
   public static final short PROPERTY = 4;
   private boolean showInnerGraphDisplay = false;
   private final Map<Short, ElementStyle> elementStyles = new HashMap<>();
   private static final Color LIGHT_GREEN = HTMLSwingColors.decodeColor("#90EE90");
   
   public CustomGraphStyles() {
      ElementStyle style = new ElementStyle(CLASS);
      elementStyles.put(CLASS, style);

      style = new ElementStyle(PACKAGE);
      elementStyles.put(PACKAGE, style);

      style = new ElementStyle(EXTERNAL_PACKAGE);
      elementStyles.put(EXTERNAL_PACKAGE, style);

      style = new ElementStyle(INDIVIDUAL);
      elementStyles.put(INDIVIDUAL, style);

      style = new ElementStyle(PROPERTY);
      elementStyles.put(PROPERTY, style);
      reset();
   }

   public final void reset() {
      showInnerGraphDisplay = false;
      
      Iterator<Entry<Short, ElementStyle>> it = elementStyles.entrySet().iterator();
      while (it.hasNext()) {
         Entry<Short, ElementStyle> entry = it.next();
         short type = entry.getKey();
         ElementStyle style = entry.getValue();
         switch (type) {
            case CLASS:
               style.backgroundColor = Color.LIGHT_GRAY;
               break;
            case PACKAGE:
               style.backgroundColor = LIGHT_GREEN;
               break;
            case EXTERNAL_PACKAGE:
               style.backgroundColor = Color.ORANGE;
               break;
            case INDIVIDUAL:
               style.backgroundColor = Color.MAGENTA;
               break;
            case PROPERTY:
               style.backgroundColor = Color.CYAN;
               break;
         }
      }
   }
   
   public void setShowInnerGraphDisplay(boolean showInnerGraphDisplay) {
      this.showInnerGraphDisplay = showInnerGraphDisplay;
   }
   
   public boolean isShowingInnerGraphDisplay() {
      return showInnerGraphDisplay;
   }

   public ElementStyle getStyle(short type) {
      return elementStyles.get(type);
   }

   public Color getBackgroundColor(short type) {
      return elementStyles.get(type).backgroundColor;
   }

   public String getBackgroundColorAsString(short type) {
      Color col = getBackgroundColor(type);
      return getColorAsString(col);
   }

   public String getColorAsString(Color col) {
      int red = col.getRed();
      int green = col.getGreen();
      int blue = col.getBlue();
      return "#" + toHexString(red) + toHexString(green) + toHexString(blue);
   }
   
   public String toHexString(int value) {
      String str = Integer.toHexString(value).toUpperCase();
      if (str.length() == 1) {
         str = "0" + str;
      }
      return str;
   }      

   public static class ElementStyle {
      public final short type;
      
      private ElementStyle(short type) {
         this.type = type;
      }
      
      public Color backgroundColor = null;
   }
}
