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
package org.girod.ontobrowser.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.Iterator;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.girod.ontobrowser.gui.tree.ModelTreeRenderer;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.restriction.OwlRestriction;

/**
 * The renderer for the dependencies window.
 *
 * @version 0.13
 */
public class DependenciesListCellRenderer extends DefaultListCellRenderer {
   private static final Icon CLASS_ICON;
   private static final Icon DATAPROPERTY_ICON;
   private static final Icon OBJECTPROPERTY_ICON;
   private static final Icon INDIVIDUAL_ICON;
   private static final Icon DATATYPE_ICON;

   static {
      CLASS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("class.gif"));
      DATAPROPERTY_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("propertydata.png"));
      OBJECTPROPERTY_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("propertyobject.png"));
      INDIVIDUAL_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("individual.png"));
      DATATYPE_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("datatype.png"));
   }
   private final OwlSchema model;

   public DependenciesListCellRenderer(OwlSchema model) {
      super();
      this.model = model;
   }

   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof String) {
         setIcon(null);
         c.setFont(c.getFont().deriveFont(Font.ITALIC));
      } else if (value instanceof PropertyBridge) {
         Icon icon;
         PropertyBridge bridge = (PropertyBridge) value;
         if (bridge.isObjectProperty()) {
            if (bridge.isFromProperty()) {
               icon = OBJECTPROPERTY_ICON;
            } else {
               icon = OBJECTPROPERTY_ICON;
            }
         } else {
            icon = DATAPROPERTY_ICON;
         }
         setIcon(icon);
         c.setFont(c.getFont().deriveFont(Font.PLAIN));
         String text = getPropertyText(bridge);
         setText(text);
      } else if (value instanceof OwlProperty) {
         Icon icon;
         OwlProperty property = (OwlProperty) value;
         if (property.isObjectProperty()) {
            icon = OBJECTPROPERTY_ICON;
         } else {
            icon = DATAPROPERTY_ICON;
         }
         setIcon(icon);
      } else if (value instanceof OwlIndividual) {
         setIcon(INDIVIDUAL_ICON);
      } else if (value instanceof OwlClass) {
         setIcon(CLASS_ICON);
      } else if (value instanceof OwlDatatype) {
         setIcon(DATATYPE_ICON);         
      }
      return this;
   }

   private String getPropertyText(PropertyBridge bridge) {
      OwlProperty property = bridge.getOwlProperty();
      if (bridge.isObjectProperty()) {
         OwlObjectProperty objectProperty = (OwlObjectProperty) property;
         boolean from = bridge.isFromProperty();
         StringBuilder buf = new StringBuilder();
         buf.append("<html>");
         buf.append("<b>").append(property.getDisplayedName());
         Map<ElementKey, OwlRestriction> restrictions;
         if (from) {
            buf.append(" <font color=\"blue\">to Range</font></b> <i>");
            restrictions = objectProperty.getRange();
         } else {
            buf.append(" <font color=\"blue\">fromDomain</font></b> <i>");
            restrictions = objectProperty.getDomain();
         }
         Iterator<ElementKey> it = restrictions.keySet().iterator();
         while (it.hasNext()) {
            ElementKey key = it.next();
            buf.append(key.getPrefixedName(model));
            if (it.hasNext()) {
               buf.append(", ");
            }
         }
         buf.append("</i></html>");
         return buf.toString();
      } else {
         OwlDatatypeProperty datatypeproperty = (OwlDatatypeProperty) property;
         StringBuilder buf = new StringBuilder();
         buf.append("<html>");
         buf.append("<b>").append(property.getDisplayedName()).append("</b>");
         buf.append(" <i>");
         Iterator<OwlDatatype> it = datatypeproperty.getTypes().values().iterator();
         while (it.hasNext()) {
            OwlDatatype datatype = it.next();
            buf.append(datatype.toString());
            if (it.hasNext()) {
               buf.append(", ");
            }
         }
         buf.append("</i></html>");
         return buf.toString();
      }
   }
}
