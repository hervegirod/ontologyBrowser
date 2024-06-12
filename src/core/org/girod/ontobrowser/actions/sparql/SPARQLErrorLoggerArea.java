/*
Copyright (c) 2024 Hervé Girod
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
package org.girod.ontobrowser.actions.sparql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.apache.jena.query.QueryParseException;
import org.jeditor.gui.JEditor;
import org.mdiutil.swing.StylableSizableArea;

/**
 * The script error logger area, showing the exceptions StackTrace.
 *
 * @since 0.13
 */
public class SPARQLErrorLoggerArea extends JPanel {
   /**
    * Default height of the Message Area.
    */
   public static final int DEFAULT_ROWS = 10;
   private StylableSizableArea area;
   private JFrame frame;
   private final int offset;
   private final JEditor editor;
   private static final String TAB = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

   public SPARQLErrorLoggerArea(JEditor editor, int offset) {
      super();
      this.editor = editor;
      this.offset = offset;
   }

   /**
    * Set the logger area parent component, with a default number of rows for the area.
    *
    * @param parent the logger area parent component
    */
   public void setParent(Component parent) {
      setParent(parent, DEFAULT_ROWS);
   }

   @Override
   public Dimension getPreferredSize() {
      Dimension dim = super.getPreferredSize();
      dim.setSize(500, dim.getHeight());
      return dim;
   }

   /**
    * Configure the logger.
    *
    * @param gui the parent component
    * @param loggerSize the logger size
    * @param loggerMaxLines the logger maximum number of lines
    */
   public void configure(Component gui, int loggerSize, int loggerMaxLines) {
      this.setParent(gui, loggerSize);
      if (loggerMaxLines != -1) {
         this.setMaximumLines(loggerMaxLines);
      }
   }

   /**
    * Set the logger area parent component, and define the initial number of rows in the area.
    *
    * @param parent the logger area parent component
    * @param rows the initial number of rows in the area
    */
   public void setParent(Component parent, int rows) {
      initializeHTMLContent(rows);
      this.setLayout(new BorderLayout());
      this.add(new JScrollPane(area), BorderLayout.CENTER);
      if (parent instanceof JFrame) {
         frame = (JFrame) parent;
      } else {
         frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parent);
      }
      frame.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            resize();
         }

         @Override
         public void componentShown(ComponentEvent e) {
            resize();
         }
      });
   }

   private void initializeHTMLContent(int rows) {
      area = new StylableSizableArea(rows);
      area.initDocument();

      area.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
               String linkDesc = e.getDescription();
               if (linkDesc.startsWith("#")) {
                  linkDesc = linkDesc.substring(1);
                  int lineNumber = Integer.parseInt(linkDesc);
                  scrollTo(lineNumber);
               }
            }
         }
      });
   }

   private void scrollTo(int lineNumber) {
      editor.scrollTo(lineNumber, 0);
      int _offset = editor.getLineStartOffset(lineNumber - 1);
      editor.setCaretPosition(_offset);
   }

   private void resize() {
      Dimension d = frame.getContentPane().getSize();
      Insets insets = frame.getContentPane().getInsets();

      d.setSize(d.width - insets.right - insets.left - 10, area.getPreferredScrollableViewportSize().height);
      area.setSize(d);
      area.setPreferredScrollableViewportSize(d);
   }

   /**
    * Set the maximum lines of the area, if the area number of rows is limited.
    *
    * @param maximumLines the maximum lines of the area, or -1 if the area height is not limited
    */
   public void setMaximumLines(int maximumLines) {
      area.setMaximumLines(maximumLines);
   }

   /**
    * Return true if the area number of lines is limited.
    *
    * @return true if the area number of lines is limited
    */
   public boolean hasMaximumLines() {
      return area.hasMaximumLines();
   }

   /**
    * Set if the area number of lines is limited.
    *
    * @param hasMaximumLines true if the area number of lines is limited
    */
   public void setMaximumLinesBehavior(boolean hasMaximumLines) {
      area.setMaximumLinesBehavior(hasMaximumLines);
   }

   /**
    * Return the maximum lines of the area.
    *
    * @return the maximum lines of the area
    */
   public int getMaximumLines() {
      return area.getMaximumLines();
   }

   /**
    * Clear the content of the message area and resize it accordingly.
    */
   public void clear() {
      area.initDocument();
      resize();
   }

   /**
    * Append an ExceptionWrapper in red in the Logger area.
    *
    * @param exception the exception
    */
   public void error(QueryParseException exception) {
      error(exception.getMessage(), exception.getLine() - offset);
   }

   /**
    * Append a line of text in red in the Logger area.
    *
    * @param txt the text
    * @param lineNumber the line number
    */
   public void error(String txt, int lineNumber) {
      if (txt != null) {
         if (lineNumber == -1) {
            area.appendText("<font color=\"red\">" + txt + "</font>");
         } else {
            appendLink(txt, lineNumber);
         }
      }
   }

   public void appendLink(String txt, int lineNumber) {
      String link = TAB + "<a href=\"#" + lineNumber + "\" style=\"color:red\">" + txt + "</a>";
      area.appendText(link);
   }
}
