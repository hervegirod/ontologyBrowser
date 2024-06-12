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
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.apache.jena.query.QueryParseException;
import org.jeditor.core.CodeEditorDefaults;
import org.jeditor.gui.CodeEditorHighlighter;
import org.jeditor.gui.JEditor;
import org.jeditor.scripts.tokenmarkers.SPARQLTokenMarker;

/**
 * A panel that present a SPARQL error.
 *
 * @since 0.13
 */
public class SPARQLErrorWindow extends JFrame {
   private QueryParseException exception = null;
   private final int line;
   private final int offset;
   private SPARQLErrorLoggerArea area = null;
   private JEditor editor = null;
   private final String sparql;
   private SPARQLErrorWindowListener listener = null;

   /**
    * Constructor.
    *
    * @param sparql the sparql expression
    * @param offset the offset
    * @param exception the exception
    */
   public SPARQLErrorWindow(String sparql, int offset, QueryParseException exception) {
      super();
      this.sparql = sparql;
      this.exception = exception;
      this.line = exception.getLine();
      this.offset = offset;
      this.setTitle("SPARQL Error");
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      createPanel();
   }

   /**
    * Set the SPARQL error WindowListener.
    *
    * @param listener the WindowListener
    */
   public void setErrorWindowListener(SPARQLErrorWindowListener listener) {
      this.listener = listener;
   }

   /**
    * Return the SPARQL error WindowListener.
    *
    * @return the error WindowListener
    */
   public SPARQLErrorWindowListener getErrorWindowListener() {
      return listener;
   }

   @Override
   public void dispose() {
      super.dispose();
      if (listener != null) {
         listener.windowClosed(this);
      }
   }

   private void createPanel() {
      JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      Container pane = this.getContentPane();
      pane.setLayout((new BorderLayout()));
      pane.add(split, BorderLayout.CENTER);

      JPanel optionsPanel = new JPanel();
      JButton okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            dispose();
         }
      });
      optionsPanel.add(okButton);
      pane.add(optionsPanel, BorderLayout.SOUTH);

      CodeEditorDefaults defaults = new CodeEditorDefaults();
      defaults.hasPopup = true;
      editor = new JEditor(defaults);

      editor.setTokenMarker(new SPARQLTokenMarker());
      editor.setText(sparql);
      CodeEditorHighlighter highlighter = new CodeEditorHighlighter();
      highlighter.append(line - offset - 1, Color.ORANGE);
      editor.setHighlighter(highlighter);
      editor.scrollTo(line - offset - 1, 0);
      split.setTopComponent(editor);
      area = new SPARQLErrorLoggerArea(editor, offset);
      area.setParent(this);
      area.error(exception);
      split.setBottomComponent(area);
      this.pack();
   }

}
