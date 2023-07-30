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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;

/**
 * This Dialog shows the properties of a Class.
 *
 * @since 0.5
 */
public class ShowPropertiesDialog extends AbstractShowLinksDialog<OwlClass, PropertyBridge> {

   public ShowPropertiesDialog(OwlClass owlClass, GraphPanel panel, Component parent) {
      super(owlClass, panel, parent, "Properties of");
   }

   @Override
   protected void initializeList() {
      // properties for the domain
      SortedMap<ElementKey, OwlProperty> map = new TreeMap<>(element.getOwlProperties());
      Iterator<Entry<ElementKey, OwlProperty>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlProperty> entry = it.next();
         OwlProperty property = entry.getValue();
         if (property instanceof OwlObjectProperty) {
            model.addElement(new PropertyBridge(entry.getValue(), true));
         }
      }
      // properties for the range
      map = new TreeMap<>(element.getRangeOwlProperties());
      it = map.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlProperty> entry = it.next();
         model.addElement(new PropertyBridge(entry.getValue(), false));
      }

      // data properties
      map = new TreeMap<>(element.getOwlProperties());
      it = map.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlProperty> entry = it.next();
         OwlProperty property = entry.getValue();
         if (property instanceof OwlDatatypeProperty) {
            model.addElement(new PropertyBridge(entry.getValue(), true));
         }
      }

      this.createList();
      list.setCellRenderer(new PropertyListCellRenderer());
   }

   @Override
   protected void rightClickOnList(int x, int y) {
      Object o = list.getSelectedValue();
      if (o != null) {
         PropertyBridge selectedBridge = (PropertyBridge) o;
         if (selectedBridge.isObjectProperty()) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Goto Property");
            item.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  gotoProperty(selectedBridge);
               }
            });
            menu.add(item);
            if (selectedBridge.isFromProperty()) {
               item = new JMenuItem("Goto Range");
               item.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                     gotoDomainRange(selectedBridge, false);
                  }
               });
            } else {
               item = new JMenuItem("Goto Domain");
               item.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                     gotoDomainRange(selectedBridge, true);
                  }
               });
            }
            menu.add(item);
            menu.show(list, x, y);
         }
      }
   }
   
   private void gotoDomainRange(PropertyBridge selectedBridge, boolean isDomain) {
      OwlObjectProperty objectProperty = (OwlObjectProperty) selectedBridge.getOwlProperty();
      ElementKey key;
      if (isDomain) {
         key = objectProperty.getRange().keySet().iterator().next();
      } else {
         key = objectProperty.getDomain().keySet().iterator().next();
      }
      panel.selectClass(key);
   }   

   private void gotoProperty(PropertyBridge selectedBridge) {
      OwlObjectProperty objectProperty = (OwlObjectProperty) selectedBridge.getOwlProperty();
      ElementKey key = objectProperty.getKey();
      panel.selectProperty(key);
   }
}
