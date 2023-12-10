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
package org.girod.ontobrowser.actions;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.AnnotationValue;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.app.swing.DefaultMDIDialogBuilder;
import org.mdi.bootstrap.MDIDialogType;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.bootstrap.swing.SwingFileProperties;

/**
 * This class allows to show the metadata content of an Ontology.
 *
 * @since 0.7
 */
public class OntologyMetadataViewer {
   public OntologyMetadataViewer() {
   }

   /**
    * Show the metadata content of the selected Ontology.
    *
    * @param appli the application
    */
   public void showOntologyMetadata(GUIApplication appli) {
      SwingFileProperties properties = appli.getSelectedProperties();
      OwlDiagram diagram = (OwlDiagram) properties.getObject();
      OwlSchema schema = diagram.getSchema();
      DefaultMDIDialogBuilder builder = new DefaultMDIDialogBuilder("Ontology Metadata");

      // prefix
      DefaultTableModel model = new DefaultTableModel();
      model.addColumn("Prefix");
      model.addColumn("URI");
      Iterator<Map.Entry<String, String>> it = schema.getPrefixMap().entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> entry = it.next();
         Vector<String> v = new Vector<>();
         v.add(entry.getValue());
         v.add(entry.getKey());
         model.addRow(v);
      }
      JTable uriTable = new JTable(model);
      uriTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
      JScrollPane scrolluri = new JScrollPane(uriTable);
      Dimension d = uriTable.getPreferredSize();
      d = new Dimension(d.width, uriTable.getRowHeight() * model.getRowCount());
      scrolluri.setPreferredSize(d);

      // annotations
      model = new DefaultTableModel();
      model.addColumn("Annotation");
      model.addColumn("Value");
      Iterator<Map.Entry<ElementKey, AnnotationValue>> it2 = schema.getAnnotations().entrySet().iterator();
      while (it2.hasNext()) {
         Map.Entry<ElementKey, AnnotationValue> entry = it2.next();
         Vector v = new Vector();
         v.add(entry.getKey().toString());
         v.add(entry.getValue());
         model.addRow(v);
      }
      JTable annotationsTable = new JTable(model);
      annotationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
      JScrollPane scrollannotations = new JScrollPane(annotationsTable);
      Dimension d2 = annotationsTable.getPreferredSize();
      d2 = new Dimension(d2.width, annotationsTable.getRowHeight() * model.getRowCount());
      scrollannotations.setPreferredSize(d2);

      builder.setResizable(true);
      Dimension dim = new Dimension(Math.max(d.width, d2.width) + 400, d.height + d2.height + 150);
      builder.addVerticalDialogPart(scrolluri, scrollannotations);
      builder.setSize(dim);

      appli.showDialog(builder, MDIDialogType.UNIQUE_INSTANCE);
   }
}
