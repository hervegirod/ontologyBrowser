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
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableCellRenderer;
import org.girod.ontobrowser.model.AnnotationValue;
import org.girod.ontobrowser.model.NamedOwlElement;

/**
 * Used to wrap the text inside a table cell.
 *
 * @version 0.7
 */
public class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
   // see https://stackoverflow.com/questions/37768335/how-to-word-wrap-inside-a-jtable-row
   public WordWrapCellRenderer() {
      setLineWrap(true);
      setWrapStyleWord(true);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JComponent comp;
      if (value instanceof AnnotationValue) {
         JEditorPane editor = new JEditorPane();
         editor.setEditable(false);
         editor.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
         editor.setContentType("text/html");
         if (value instanceof AnnotationValue.URIAnnotationValue) {
            AnnotationValue.URIAnnotationValue uriAnnotation = (AnnotationValue.URIAnnotationValue) value;
            StringBuilder buf = new StringBuilder();
            buf.append("<html><a href=\"");
            final URI uri = uriAnnotation.getURI();
            buf.append(uri.toString()).append("\">").append(uri.toString());
            buf.append("</a></html>");
            Font font = editor.getFont();
            font = font.deriveFont(Font.PLAIN);
            editor.setFont(font);
            editor.setText(buf.toString());
            editor.addHyperlinkListener(new HyperlinkListener() {
               @Override
               public void hyperlinkUpdate(HyperlinkEvent e) {
                  try {
                     Desktop.getDesktop().browse(e.getURL().toURI());
                  } catch (IOException | URISyntaxException ex) {
                  }
               }
            });
         } else if (value instanceof AnnotationValue.ElementAnnotationValue) {
            AnnotationValue.ElementAnnotationValue eltAnnotation = (AnnotationValue.ElementAnnotationValue) value;
            StringBuilder buf = new StringBuilder();
            buf.append("<html><a href=\"");
            NamedOwlElement element = eltAnnotation.getElement();
            URI uri = element.toURI();
            if (uri != null) {
               buf.append(uri.toString()).append("\">").append(element.getDisplayedName());
               buf.append("</a></html>");
               Font font = editor.getFont();
               font = font.deriveFont(Font.PLAIN);
               editor.setFont(font);
               editor.setText(buf.toString());
               editor.addHyperlinkListener(new HyperlinkListener() {
                  @Override
                  public void hyperlinkUpdate(HyperlinkEvent e) {
                     try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                     } catch (IOException | URISyntaxException ex) {
                     }
                  }
               });
            }
         }
         comp = editor;
      } else {
         JLabel label = new JLabel();
         label.setText(value.toString());
         Font font = label.getFont();
         font = font.deriveFont(Font.PLAIN);
         label.setFont(font);
         comp = label;
      }
      setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
      if (table.getRowHeight(row) != comp.getPreferredSize().height) {
         table.setRowHeight(row, comp.getPreferredSize().height);
      }
      return comp;
   }
}
