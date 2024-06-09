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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
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
import org.girod.ontobrowser.model.DatatypePropertyValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.ObjectPropertyValue;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlImportedSchema;
import org.girod.ontobrowser.model.OwlIndividual;
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
      } else if (namedElement instanceof OwlIndividual) {
         return getIndividualPanel((OwlIndividual) namedElement);
      } else {
         return getDefaultElementPanel(namedElement);
      }
   }

   private JComponent getOwlImportedSchemaPanel(OwlImportedSchema schema) {
      return getOwlImportedSchemaPanel(schema.getSchemaRep());
   }

   private String getName(ElementKey key) {
      String ns = key.getNamespace();
      if (ns == null) {
         return key.getName();
      }
      if (schema.hasPrefix(ns)) {
         ns = schema.getPrefix(ns);
         return ns + ":" + key.getName();
      } else {
         return key.getNamespace() + "#" + key.getName();
      }
   }

   private String getNameSpaceOrPrefix(SchemasRepository.SchemaRep schema) {
      String ns = schema.getPrefix();
      if (ns == null) {
         ns = schema.getNamespace();
      }
      return ns;
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
      v.add(getNameSpaceOrPrefix(schema));
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

      JPanel tablePanel = new JPanel(new BorderLayout());
      tablePanel.add(table, BorderLayout.CENTER);
      tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
      thePanel.add(tablePanel);
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
      JPanel tablePanel = new JPanel(new BorderLayout());
      tablePanel.add(table, BorderLayout.CENTER);
      tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
      thePanel.add(tablePanel);
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

   private void addElementPanelTitle(JPanel parentPanel, String title) {
      StringBuilder html = new StringBuilder();
      html.append("<html>");
      html.append("<i style=\"font-weight: normal;\">");
      html.append(title);
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

   private void addIndividualPanelDataProperties(JPanel parentPanel, OwlIndividual individual) {
      final DefaultTableModel tablemodel = new UneditableTableModel();
      final JTable table = new JTable();
      tablemodel.addColumn("DatatypeProperty");
      tablemodel.addColumn("Value");
      TreeMap<ElementKey, List<DatatypePropertyValue>> map = new TreeMap<>(individual.getDatatypePropertyValues());
      Iterator<Entry<ElementKey, List<DatatypePropertyValue>>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, List<DatatypePropertyValue>> entry = it.next();
         List<DatatypePropertyValue> values = entry.getValue();
         Iterator<DatatypePropertyValue> it2 = values.iterator();
         while (it2.hasNext()) {
            DatatypePropertyValue theValue = it2.next();
            Vector v = new Vector();
            v.add(new SelectableElement(schema, theValue.getDatatypeProperty()));
            v.add(theValue);
            tablemodel.addRow(v);
         }
      }
      table.setModel(tablemodel);
      table.getColumnModel().getColumn(0).setCellRenderer(new WordWrapCellRenderer());
      table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
      table.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 0) {
               Object o = tablemodel.getValueAt(row, 0);
               SelectableElement element = (SelectableElement) o;
               OwlDatatypeProperty property = (OwlDatatypeProperty) element.getElement();
               panel.selectElement(property);
            }
         }
      });
      JPanel tablePanel = new JPanel(new BorderLayout());
      tablePanel.add(table, BorderLayout.CENTER);
      tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
      parentPanel.add(tablePanel);
   }

   private void addIndividualPanelObjectProperties(JPanel parentPanel, OwlIndividual individual) {
      final DefaultTableModel tablemodel = new UneditableTableModel();
      final JTable table = new JTable();
      tablemodel.addColumn("ObjectProperty");
      tablemodel.addColumn("Individual");
      TreeMap<ElementKey, List<ObjectPropertyValue>> map = new TreeMap<>(individual.getObjectPropertyValues());
      Iterator<Entry<ElementKey, List<ObjectPropertyValue>>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, List<ObjectPropertyValue>> entry = it.next();
         List<ObjectPropertyValue> values = entry.getValue();
         Iterator<ObjectPropertyValue> it2 = values.iterator();
         while (it2.hasNext()) {
            ObjectPropertyValue theValue = it2.next();
            Vector v = new Vector();
            v.add(new SelectableElement(schema, theValue.getProperty()));
            v.add(new SelectableElement(schema, theValue.getTarget()));
            v.add(theValue);
            tablemodel.addRow(v);
         }
      }

      table.setModel(tablemodel);
      WordWrapCellRenderer cellRenderer = new WordWrapCellRenderer();
      table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
      table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
      table.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (col == 0) {
               Object o = tablemodel.getValueAt(row, 0);
               SelectableElement element = (SelectableElement) o;
               panel.selectElement(element.getElement());
            } else if (col == 1) {
               Object o = tablemodel.getValueAt(row, 1);
               SelectableElement element = (SelectableElement) o;
               panel.selectElement(element.getElement());
            }
         }
      });
      parentPanel.add(table);
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
         v.add(getName(entry.getKey()));
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
      parentPanel.add(table);
   }

   private JComponent getIndividualPanel(OwlIndividual individual) {
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
      addElementPanelHeader(thePanel, individual);
      if (individual.hasAnnotations()) {
         thePanel.add(Box.createVerticalStrut(5));
         addElementPanelTitle(thePanel, "Annotations");
         thePanel.add(Box.createVerticalStrut(5));
         addElementPanelAnnotations(thePanel, individual);
      }
      if (individual.hasDatatypePropertyValues()) {
         thePanel.add(Box.createVerticalStrut(5));
         addElementPanelTitle(thePanel, "Data properties");
         addIndividualPanelDataProperties(thePanel, individual);
      }
      if (individual.hasObjectPropertyValues()) {
         thePanel.add(Box.createVerticalStrut(5));
         addElementPanelTitle(thePanel, "Object properties");
         addIndividualPanelObjectProperties(thePanel, individual);
      }
      thePanel.add(Box.createVerticalGlue());
      return new JScrollPane(thePanel);
   }

   private JComponent getDefaultElementPanel(NamedOwlElement theElement) {
      JPanel thePanel = new JPanel();
      thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
      addElementPanelHeader(thePanel, theElement);
      thePanel.add(Box.createVerticalStrut(5));
      addElementPanelTitle(thePanel, "Annotations");
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
