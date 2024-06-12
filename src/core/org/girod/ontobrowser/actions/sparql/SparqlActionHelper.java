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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.OwlSchema;
import org.jeditor.core.CodeEditorDefaults;
import org.jeditor.gui.JEditor;
import org.jeditor.scripts.base.Token;
import org.jeditor.scripts.tokenmarkers.SPARQLTokenMarker;
import org.mdi.app.swing.DefaultMDIDialogBuilder;
import org.mdi.app.swing.DefaultMDIDialogBuilder.DialogListener;
import org.mdi.bootstrap.MDIDialogType;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.bootstrap.swing.MDIDialogBuilder;
import org.mdiutil.io.FileUtilities;

/**
 * Encapsulate a SPARQL request.
 *
 * @since 0.13
 */
public class SparqlActionHelper {
   private static final Pattern TRIM_LEFT = Pattern.compile("(\\s*)(.*)");
   private final GUIApplication appli;
   private final OwlSchema schema;
   private static final CodeEditorDefaults defaults = new CodeEditorDefaults();

   static {
      defaults.eolMarkers = false;
      defaults.paintInvalid = false;
      defaults.setStyle(Token.KEYWORD1, Color.BLUE, false, true);
      defaults.setStyle(Token.KEYWORD2, new Color(13, 130, 0), false, true);
      defaults.setStyle(Token.KEYWORD3, Color.BLUE, false, true);
      defaults.setStyle(Token.COMMENT1, Color.DARK_GRAY, true, false);
      defaults.setStyle(Token.COMMENT2, Color.DARK_GRAY, true, false);
   }

   public SparqlActionHelper(GUIApplication appli, OwlSchema schema) {
      this.appli = appli;
      this.schema = schema;
   }

   public void showDialog() {
      DefaultMDIDialogBuilder builder = new DefaultMDIDialogBuilder("SPARQL Window");
      JMenuBar menubar = new JMenuBar();
      builder.setJMenuBar(menubar);
      JEditor area = new JEditor(defaults);
      area.setTokenMarker(new SPARQLTokenMarker());
      JMenu menu = new JMenu("File");
      menubar.add(menu);
      AbstractAction openAction = new AbstractAction("Open SPARQL") {
         @Override
         public void actionPerformed(ActionEvent e) {
            openSPARQL(area);
         }
      };
      menu.add(new JMenuItem(openAction));
      AbstractAction saveAction = new AbstractAction("Save SPARQL") {
         @Override
         public void actionPerformed(ActionEvent e) {
            saveSPARQL(area.getText());
         }
      };
      menu.add(new JMenuItem(saveAction));

      builder.setResizable(true);
      builder.addVerticalDialogPart(area);
      builder.setDialogType(MDIDialogBuilder.YES_CANCEL_DIALOG);
      builder.setYesLabel("Apply");
      builder.setListener(new DialogListener() {
         @Override
         public void apply() {
            applySPARQL(area.getText());
         }
      });
      appli.showDialog(builder, MDIDialogType.UNIQUE_INSTANCE);
   }

   private void openSPARQL(JEditor area) {
      JFileChooser chooser = new JFileChooser("Open SPARQL Request");
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      chooser.setFileFilter(conf.sparqlfilter);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setCurrentDirectory(conf.getDefaultDirectory());
      if (chooser.showOpenDialog(((GUIApplication) appli).getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
         File sparqlFile = chooser.getSelectedFile();
         StringBuilder buf = new StringBuilder();
         boolean first = true;
         try ( BufferedReader reader = new BufferedReader(new FileReader(sparqlFile))) {
            while (true) {
               String line = reader.readLine();
               if (line == null) {
                  break;
               } else {
                  if (first) {
                     first = false;
                  } else {
                     buf.append("\n");
                  }
                  buf.append(line);
               }
            }
            String sparql = buf.toString();
            if (conf.addPrefixInSPARQL) {
               sparql = removePrefixFromRequest(sparql);
            } else {
               sparql = addPrefixToRequest(sparql);
            }
            area.setText(sparql);
         } catch (IOException ex) {
         }
      }
   }

   public void saveSPARQL(String sparql) {
      JFileChooser chooser = new JFileChooser("Save SPARQL Request");
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      chooser.setFileFilter(conf.sparqlfilter);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setCurrentDirectory(conf.getDefaultDirectory());
      if (chooser.showSaveDialog(((GUIApplication) appli).getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
         if (!hasPrefix(sparql)) {
            sparql = addPrefixToRequest(sparql);
         }
         File sparqlFile = chooser.getSelectedFile();
         sparqlFile = FileUtilities.getCompatibleFile(sparqlFile, "sparql");
         try ( BufferedWriter writer = new BufferedWriter(new FileWriter(sparqlFile))) {
            writer.write(sparql);
            writer.flush();
         } catch (IOException e) {
         }
      }
   }

   private void applySPARQL(String sparql) {
      ExecuteSPARQLAction sparqlAction = new ExecuteSPARQLAction(appli, this, schema, sparql);
      appli.executeAction(sparqlAction);
   }

   public boolean hasPrefix(String sparql) {
      Matcher m = TRIM_LEFT.matcher(sparql);
      if (m.matches()) {
         sparql = m.group(2);
      }
      return sparql.toUpperCase().startsWith("PREFIX ");
   }

   public String removePrefixFromRequest(String sparql) {
      if (!hasPrefix(sparql)) {
         return sparql;
      }
      StringBuilder buf = new StringBuilder();
      try ( Scanner scanner = new Scanner(sparql)) {
         boolean hasPrefix = true;
         boolean firstLine = true;
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!hasPrefix || !hasPrefix(line)) {
               hasPrefix = false;
               if (firstLine) {
                  firstLine = false;
               } else {
                  buf.append("\n");
               }
               buf.append(line);
            }
         }
      }
      return buf.toString();
   }

   public String addPrefixToRequest(String sparql) {
      boolean hasPrefix = hasPrefix(sparql);
      if (hasPrefix) {
         return sparql;
      }
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      StringBuilder buf = new StringBuilder();
      Map<String, String> prefixMap = schema.getPrefixMap();
      Set<String> alreadyExists = new HashSet<>();
      Iterator<Map.Entry<String, String>> it = prefixMap.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> entry = it.next();
         String uri = entry.getKey();
         String prefix = entry.getValue();
         if (uri.endsWith("/#")) {
            uri = uri.substring(0, uri.length() - 2);
         }
         if (prefix.isEmpty()) {
            prefix = conf.basePrefix;
         }
         if (!alreadyExists.contains(uri)) {
            buf.append("PREFIX ").append(prefix).append(": <").append(uri).append(">");
            buf.append("\n");
            alreadyExists.add(uri);
         }
      }
      String queryAsString = buf.toString() + sparql;
      return queryAsString;
   }
}
