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
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.girod.ontobrowser.gui.tree.OwlElementRep;
import org.girod.ontobrowser.gui.tree.OwlImportedSchemaRep;
import org.girod.ontobrowser.gui.tree.OwlOntologyRep;
import org.girod.ontobrowser.model.AnnotationValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlImportedSchema;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.SchemasRepository;

/**
 * The Compoent panel factory.
 *
 * @version 0.8
 */
public class ComponentPanelFactory {
   private final GraphPanel panel;
   private final OwlSchema schema;

   public ComponentPanelFactory(GraphPanel panel, OwlSchema schema) {
      this.schema = schema;
      this.panel = panel;
   }

   public JComponent getComponentPanel(OwlImportedSchemaRep element) {
      if (element.isImportedSchema()) {
         return getOwlImportedSchemaPanel(element.getImportedSchema());
      } else {
         return getOwlImportedSchemaPanel(element.getImportedSchemaRep());
      }
   }

   public JComponent getComponentPanel(OwlOntologyRep element) {
      OwlSchema _schema = element.schema;
      return getOwlSchemaPanel(_schema);
   }

   public JComponent getComponentPanel(OwlElementRep selectedElement) {
      if (selectedElement == null) {
         return new JPanel();
      }
      NamedOwlElement namedElement = selectedElement.getOwlElement();
      if (namedElement instanceof OwlProperty) {
         return getOwlPropertyPanel((OwlProperty) namedElement);
      } else {
         return getDefaultElementPanel(namedElement);
      }
   }

   private JComponent getOwlImportedSchemaPanel(OwlImportedSchema schema) {
      return getOwlImportedSchemaPanel(schema.getSchemaRep());
   }

   private JComponent getOwlImportedSchemaPanel(SchemasRepository.SchemaRep schema) {
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
      thePanel.add(Box.createVerticalStrut(5));

      final DefaultTableModel tablemodel = new UneditableTableModel();
      final JTable table = new JTable();
      tablemodel.addColumn("Namespace");
      tablemodel.addColumn("Name");
      tablemodel.addColumn("Description");

      Vector v = new Vector();
      v.add(schema.getNamespace());
      String name = schema.getName();
      if (name == null) {
         name = " ";
      }
      v.add(name);
      String description = schema.getDescription();
      if (description == null) {
         description = " ";
      }
      v.add(description);
      tablemodel.addRow(v);

      table.setModel(tablemodel);
      WordWrapCellRenderer cellRenderer = new WordWrapCellRenderer();
      table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
      table.getColumnModel().getColumn(2).setCellRenderer(cellRenderer);

      thePanel.add(new JScrollPane(table));
      thePanel.add(Box.createVerticalGlue());
      return new JScrollPane(thePanel);
   }

   private JComponent getOwlSchemaPanel(OwlSchema schema) {
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
      thePanel.add(Box.createVerticalStrut(5));

      final DefaultTableModel tablemodel = new UneditableTableModel();
      final JTable table = new JTable();
      tablemodel.addColumn("Annotation");
      tablemodel.addColumn("Value");
      Iterator<Entry<ElementKey, AnnotationValue>> it = schema.getAnnotations().entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, AnnotationValue> entry = it.next();
         Vector v = new Vector();
         v.add(entry.getKey().toString());
         AnnotationValue value = entry.getValue();
         if (value instanceof AnnotationValue.URIAnnotationValue) {
            v.add(value);
         } else if (value instanceof AnnotationValue.ElementAnnotationValue) {
            v.add(value);
         } else {
            v.add(getAnnotationText(value.toString()));
         }
         tablemodel.addRow(v);
      }

      table.setModel(tablemodel);
      table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
      table.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            // see https://stackoverflow.com/questions/7350893/click-event-on-jtable-java
            // see https://stackoverflow.com/questions/4256680/click-hyperlink-in-jtable
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 1) {
               Object o = tablemodel.getValueAt(row, 1);
               if (o instanceof AnnotationValue.URIAnnotationValue) {
                  try {
                     AnnotationValue.URIAnnotationValue cellElt = (AnnotationValue.URIAnnotationValue) o;
                     URI uri = cellElt.getURI();
                     Desktop.getDesktop().browse(uri);
                  } catch (IOException ex) {
                  }
               } else if (o instanceof AnnotationValue.ElementAnnotationValue) {
                  AnnotationValue.ElementAnnotationValue celElt = (AnnotationValue.ElementAnnotationValue) o;
                  NamedOwlElement element = celElt.getElement();
                  panel.selectElement(element);
               }
            }
         }
      });
      thePanel.add(new JScrollPane(table));
      thePanel.add(Box.createVerticalGlue());
      return new JScrollPane(thePanel);
   }

   private JComponent getOwlPropertyPanel(OwlProperty property) {
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
      addElementPanelHeader(thePanel, property);
      
      JPanel funcPanel = new JPanel();
      funcPanel.setLayout(new BoxLayout(funcPanel, BoxLayout.X_AXIS));
      JCheckBox funcCb = new JCheckBox("Functional");
      funcCb.setEnabled(false);
      funcCb.setSelected(property.isFunctionalProperty());
      funcPanel.add(funcCb);
      funcPanel.add(Box.createHorizontalStrut(5));
      funcCb = new JCheckBox("Inverse Functional");
      funcCb.setEnabled(false);
      funcCb.setSelected(property.isInverseFunctionalProperty());
      funcPanel.add(funcCb);
      funcPanel.add(Box.createHorizontalStrut(5));            
      funcPanel.add(Box.createHorizontalGlue());
      thePanel.add(funcPanel);
      
      thePanel.add(Box.createVerticalStrut(5));         
      
      addElementPanelAnnotations(thePanel, property);
      thePanel.add(Box.createVerticalGlue());
      return new JScrollPane(thePanel);
   }
   
   private void addElementPanelHeader(JPanel parentPanel, NamedOwlElement theElement) {
      StringBuilder html = new StringBuilder();
      html.append("<html>");
      String title = theElement.getKey().getPrefixedName(schema);
      html.append(title);
      html.append(" <i style=\"font-weight: normal;\">- ");
      html.append(theElement.getNamespace()).append(theElement.getDisplayedName());
      html.append("</i>");
      html.append("</html>");
      JPanel headerpanel = new JPanel();
      headerpanel.setLayout(new BoxLayout(headerpanel, BoxLayout.X_AXIS));
      JLabel label = new JLabel(html.toString());
      headerpanel.add((Box.createHorizontalStrut(5)));
      headerpanel.add(label);
      headerpanel.add((Box.createHorizontalGlue()));
      parentPanel.add(headerpanel);
      parentPanel.add(Box.createVerticalStrut(5));      
   }
   
   private void addElementPanelAnnotations(JPanel parentPanel, NamedOwlElement theElement) {
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
            v.add(value);
         } else if (value instanceof AnnotationValue.ElementAnnotationValue) {
            v.add(value);
         } else {
            v.add(getAnnotationText(value.toString()));
         }
         tablemodel.addRow(v);
      }

      table.setModel(tablemodel);
      table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
      table.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            // see https://stackoverflow.com/questions/7350893/click-event-on-jtable-java
            // see https://stackoverflow.com/questions/4256680/click-hyperlink-in-jtable
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 1) {
               Object o = tablemodel.getValueAt(row, 1);
               if (o instanceof AnnotationValue.URIAnnotationValue) {
                  try {
                     AnnotationValue.URIAnnotationValue cellElt = (AnnotationValue.URIAnnotationValue) o;
                     URI uri = cellElt.getURI();
                     Desktop.getDesktop().browse(uri);
                  } catch (IOException ex) {
                  }
               } else if (o instanceof AnnotationValue.ElementAnnotationValue) {
                  AnnotationValue.ElementAnnotationValue celElt = (AnnotationValue.ElementAnnotationValue) o;
                  NamedOwlElement element = celElt.getElement();
                  panel.selectElement(element);
               }
            }
         }
      });
      parentPanel.add(new JScrollPane(table));      
   }

   private JComponent getDefaultElementPanel(NamedOwlElement theElement) {
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
      addElementPanelHeader(thePanel, theElement);
      addElementPanelAnnotations(thePanel, theElement);
      thePanel.add(Box.createVerticalGlue());
      return new JScrollPane(thePanel);
   }

   private String getAnnotationText(String value) {
      StringBuilder buf = new StringBuilder();
      buf.append("<html>");
      StringTokenizer tok = new StringTokenizer(value, "\n");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         buf.append(tk);
         if (tok.hasMoreTokens()) {
            buf.append("<br>");
         }
      }
      buf.append("</html>");
      return buf.toString();
   }
}
