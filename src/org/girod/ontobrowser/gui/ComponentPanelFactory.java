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

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.girod.ontobrowser.gui.tree.OwlElementRep;
import org.girod.ontobrowser.model.AnnotationValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlSchema;

/**
 *
 * @since 0.6
 */
public class ComponentPanelFactory {
   private final OwlSchema schema;

   public ComponentPanelFactory(OwlSchema schema) {
      this.schema = schema;
   }

   public JComponent getComponentPanel(OwlElementRep selectedElement) {
      if (selectedElement == null) {
         return new JPanel();
      }
      NamedOwlElement namedElement = selectedElement.getOwlElement();
      return getOwlClassPanel(namedElement);
   }

   private JComponent getOwlClassPanel(NamedOwlElement theElement) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      StringBuilder html = new StringBuilder();
      html.append("<html>");
      String title = theElement.getKey().getPrefixedName(schema);
      html.append(title);
      html.append(" <i style=\"font-weight: normal;\">- ");
      html.append(theElement.getNamespace()).append(theElement.getName());
      html.append("</i>");
      html.append("</html>");
      JLabel label = new JLabel(html.toString());
      panel.add(label);
      panel.add(Box.createVerticalStrut(5));

      final DefaultTableModel tablemodel = new UneditableTableModel();
      final JTable table = new JTable();
      tablemodel.addColumn("Annotation");
      tablemodel.addColumn("Value");
      Iterator<Entry<ElementKey, AnnotationValue>> it = theElement.getAnnotations().entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, AnnotationValue> entry = it.next();
         Vector v = new Vector();
         v.add(entry.getKey().toString());
         AnnotationValue value = entry.getValue();
         if (value instanceof AnnotationValue.URIAnnotationValue) {

            v.add(new URICellElement((AnnotationValue.URIAnnotationValue) value));
         } else {
            v.add(value.toString());
         }
         tablemodel.addRow(v);
      }
      
      table.setModel(tablemodel);
      table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
      table.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            // see https://stackoverflow.com/questions/7350893/click-event-on-jtable-java
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 1) {
               Object o = tablemodel.getValueAt(row, 1);
               if (o instanceof URICellElement) {
                  try {
                     URICellElement celElt = (URICellElement)o;
                     URI uri = celElt.getURI();
                     Desktop.getDesktop().browse(uri);
                  } catch (IOException ex) {
                  }
               }
            }
         }
      });
      panel.add(new JScrollPane(table));
      panel.add(Box.createVerticalGlue());
      return new JScrollPane(panel);
   }
}
