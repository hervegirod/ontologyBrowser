/*
 Copyright (c) 2023 Dassault Aviation. All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on https://sourceforge.net/projects/protoframework/
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
