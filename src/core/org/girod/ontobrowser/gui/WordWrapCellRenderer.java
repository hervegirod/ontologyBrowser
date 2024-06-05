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
import java.awt.FontMetrics;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
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
   // see https://stackoverflow.com/questions/965023/how-to-wrap-lines-in-a-jtable-cell
   public WordWrapCellRenderer() {
      setLineWrap(true);
      setWrapStyleWord(true);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JComponent comp;
      int columnWidth = getColumnWidth(table, column);
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
         Font font = label.getFont();
         font = font.deriveFont(Font.PLAIN);
         label.setText(getText(label, columnWidth, font, value.toString()));
         label.setFont(font);
         comp = label;
      }
      setSize(columnWidth, getPreferredSize().height);

      // see https://stackoverflow.com/questions/965023/how-to-wrap-lines-in-a-jtable-cell
      if (table.getRowHeight(row) != comp.getPreferredSize().height) {
         table.setRowHeight(row, comp.getPreferredSize().height + 20);
      }
      return comp;
   }

   private int getColumnWidth(JTable table, int column) {
      return table.getColumnModel().getColumn(column).getWidth();
   }

   private String getText(JLabel label, int columnWidth, Font font, String text) {
      boolean encloseHTML = text.startsWith("<html>");
      JLabel testLabel = new JLabel();
      FontMetrics metrics = testLabel.getFontMetrics(font);
      int charWidth = metrics.charWidth('A');
      int maxCharacters = columnWidth / charWidth;
      int count = 0;
      // see https://stackoverflow.com/questions/33937074/jtable-jtextarea-cell-wrapping/38932843#38932843
      StringBuilder buf = new StringBuilder();
      if (!encloseHTML) {
         buf.append("<html>");
      }
      StringTokenizer tok = new StringTokenizer(text, "\n");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         int strCount = tk.length();
         if (count + strCount <= maxCharacters) {
            buf.append(tk);
         } else {
            label.setVerticalAlignment(JLabel.TOP);
            int index = count + strCount - maxCharacters;
            buf.append(tk.substring(0, count + strCount - maxCharacters));
            buf.append("<br>");
            String remainingString = tk.substring(index);
            int offset = 0;
            for (int i = 0; i < remainingString.length() / maxCharacters; i++) {
               if (offset + index * maxCharacters < maxCharacters) {
                  buf.append(remainingString.substring(offset));
                  break;
               } else {
                  buf.append(remainingString.substring(offset, offset + maxCharacters));
                  offset += maxCharacters;
                  if (offset < remainingString.length()) {
                     buf.append("<br>");
                  }
               }
            }
            count = 0;
         }
         if (tok.hasMoreTokens()) {
            buf.append("<br>");
            count = 0;
         }
      }
      if (!encloseHTML) {
         buf.append("</html>");
      }
      return buf.toString();
   }
}
