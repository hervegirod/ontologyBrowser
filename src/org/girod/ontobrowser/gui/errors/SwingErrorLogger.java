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
package org.girod.ontobrowser.gui.errors;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.swing.ExtensionFileFilter;
import org.mdiutil.xml.swing.BasicSAXHandler;

/**
 * The ErrorLogger is used to show errors detected during the opening of the Owl ontology.
 *
 * @since 0.7
 */
public class SwingErrorLogger implements ErrorLogger {
   private List<ParserLog> exceptions = null;
   private JButton OKButton = null;
   private JButton printButton = null;
   private JDialog dialog = null;
   private short level = ErrorLevel.INFO;
   private int countErrors = 0;
   private int countLogs = 0;

   public SwingErrorLogger() {
   }

   public void setLogLevel(short level) {
      this.level = level;
   }

   public short getLogLevel() {
      return level;
   }

   /**
    * Return the number of logs.
    *
    * @return the number of logs
    */
   @Override
   public int countLogs() {
      return countLogs;
   }

   /**
    * Return the number of errors.
    *
    * @return the number of errors
    */
   @Override
   public int countErrors() {
      return countErrors;
   }

   /**
    * Show the message of an exception.
    *
    * @param exception the exception
    */
   @Override
   public void showRuntimeException(Exception exception) {
      JTextPane area = constructExceptionPane(exception);

      OKButton = new JButton("OK");
      printButton = new JButton("Print");

      OKButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            dialog.dispose();
         }
      });

      printButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            doPrint();
         }
      });

      showExceptionDialogImpl("Runtime Exception", area);
   }

   /**
    * Show Exceptions encountered after the XML parsing.
    *
    * @param exceptions the exception results
    */
   @Override
   public void showParserExceptions(List<ParserLog> exceptions) {
      this.exceptions = exceptions;
      JTextPane area = constructLogResultPane(exceptions);

      OKButton = new JButton("OK");
      printButton = new JButton("Print");

      OKButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            dialog.dispose();
         }
      });

      printButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            doPrint();
         }
      });

      showExceptionDialogImpl("Parsing Exceptions", area);
   }

   private void showExceptionDialogImpl(String title, JTextPane area) {
      JScrollPane scroll = new JScrollPane(area);
      JOptionPane pane = new JOptionPane(scroll, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {OKButton, printButton});
      Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, null);
      dialog = pane.createDialog(frame, title);
      dialog.setResizable(true);
      dialog.setAlwaysOnTop(true);
      dialog.setVisible(true);
   }

   private void doPrint() {
      String[] ext = {"html"};
      ExtensionFileFilter htmlfilter = new ExtensionFileFilter(ext, "HTML files");
      JFileChooser chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      chooser.setDialogTitle("Save Errors File");
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setFileFilter(htmlfilter);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int ret = chooser.showOpenDialog(dialog);
      if (ret == JFileChooser.APPROVE_OPTION) {
         File file = FileUtilities.getCompatibleFile(chooser.getSelectedFile(), "html");
         try {
            doPrintImpl(file);
         } catch (IOException e) {
         }
      }
   }

   private void writeBR(BufferedWriter writer) throws IOException {
      writer.newLine();
      writer.append("</br>");
      writer.newLine();
   }

   private void writeLog(BufferedWriter writer, String message) throws IOException {
      writer.append("<font color=\"red\">" + message + "</font>\n");
      writeBR(writer);
   }

   private void doPrintImpl(File file) throws IOException {
      try ( BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
         writer.append("<html>");
         writeBR(writer);
         writer.append("<H1>Ontology Browser open errors</H1>");
         writer.newLine();

         Iterator<ParserLog> it = exceptions.iterator();
         while (it.hasNext()) {
            ParserLog error = it.next();
            writeLog(writer, error.getMessage());
         }
         writer.append("</html>");
         writer.flush();
      }
   }

   /**
    * Construct the JTextPane holding the mesaeg if a exception.
    */
   private JTextPane constructExceptionPane(Exception exception) {
      SizedTextPane area = new SizedTextPane(10, 40);
      area.append(exception.getMessage(), "red");
      return area;
   }

   /**
    * Construct the JTextPane holding the logs encountered during the parsing.
    */
   private JTextPane constructLogResultPane(List<ParserLog> exceptions) {
      SizedTextPane area = new SizedTextPane(10, 40);

      Iterator<ParserLog> it = exceptions.iterator();
      while (it.hasNext()) {
         ParserLog log = it.next();
         if (log.getType() == ErrorLevel.INFO) {
            area.append(log.getMessage() + "\n");
         } else {
            area.append(log.getMessage() + "\n", "red");
            countErrors++;
         }
         countLogs++;
      }
      return area;
   }

   /**
    * A JTextPane with a specified preferred scrollable size.
    */
   private class SizedTextPane extends JTextPane {
      private Dimension d;
      private int rows = 1;
      private int columns = 1;
      private HTMLDocument doc;
      private final HTMLEditorKit kit = new HTMLEditorKit();
      private String fontFace = null;
      private static final int FONT_SIZE = BasicSAXHandler.DEFAULT_SIZE;

      /**
       * Create a new SizedTextPane.
       *
       * @param rows the number of rows of the area
       * @param columns the number of columns of the area
       */
      public SizedTextPane(int rows, int columns) {
         super();
         this.rows = rows;
         this.columns = columns;

         setSize();
         createDocument();
      }

      private void setSize() {
         JTextArea textArea = new JTextArea(rows, columns);
         Font font = textArea.getFont();
         fontFace = font.getFamily();
         d = textArea.getPreferredSize();
      }

      private void createDocument() {
         this.setEditorKit(kit);
         doc = (HTMLDocument) kit.createDefaultDocument();
         this.setDocument(doc);
         this.setEditable(false);
      }

      public void append(String text) {
         appendImpl("<html><font face=\"" + fontFace + "\" size=\"" + FONT_SIZE + "\">" + text + "</font></html>\n");
      }

      public void append(String text, String htmlColor) {
         appendImpl("<html><font face=\"" + fontFace + "\" size=\"" + FONT_SIZE + "\" color=" + htmlColor + "\">" + text + "</font></html>\n");
      }

      private void appendImpl(String text) {
         try {
            Reader r = new StringReader(text);
            kit.read(r, doc, doc.getLength());
            this.setCaretPosition(doc.getLength());
         } catch (BadLocationException | IOException e) {
         }
      }

      @Override
      public Dimension getPreferredScrollableViewportSize() {
         return d;
      }
   }
}
