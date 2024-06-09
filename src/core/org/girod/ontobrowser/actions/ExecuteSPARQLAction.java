/*
Copyright (c) 2023, 2024 Hervé Girod
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

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.gui.errors.SwingErrorLogger;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.app.swing.DefaultMDIDialogBuilder;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.MDIDialogType;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdiutil.io.FileUtilities;

/**
 * A Sparql action, which executes a SPARQL query.
 *
 * @version 0.13
 */
public class ExecuteSPARQLAction extends AbstractMDIAction {
   private final OwlSchema schema;
   private final String sparql;
   private final SparqlActionHelper helper;
   private String resultAsString = null;
   private Exception exception = null;

   /**
    * Constructor.
    *
    * @param app the application
    * @param helper the SparqlActionHelper
    * @param schema the schema
    * @param sparql the SPARQL request
    */
   public ExecuteSPARQLAction(MDIApplication app, SparqlActionHelper helper, OwlSchema schema, String sparql) {
      super(app, "Execute SPARQL");
      this.schema = schema;
      this.helper = helper;
      this.sparql = sparql;
      this.setDescription("Execute SPARQL", "Execute SPARQL");
   }

   @Override
   public void run() throws Exception {
      OntModel model = schema.getOntModel();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      boolean addPrefix = conf.addPrefixInSPARQL;
      String queryAsString;
      if (addPrefix && !helper.hasPrefix(sparql)) {
         queryAsString = helper.addPrefixToRequest(sparql);
      } else {
         queryAsString = sparql;
      }
      try {
         Query query = QueryFactory.create(queryAsString);
         try ( QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet resultsSet = qexec.execSelect();
            resultAsString = ResultSetFormatter.asText(resultsSet);
         }
      } catch (Exception e) {
         SwingErrorLogger logger = new SwingErrorLogger();
         logger.showRuntimeException(e);
         this.exception = e;
      }
   }

   @Override
   public void endAction() {
      if (exception == null) {
         DefaultMDIDialogBuilder builder = new DefaultMDIDialogBuilder("SPARQL Result");

         JMenuBar menubar = new JMenuBar();
         builder.setJMenuBar(menubar);
         JMenu menu = new JMenu("File");
         menubar.add(menu);
         AbstractAction saveSPARQLAction = new AbstractAction("Save SPARQL") {
            @Override
            public void actionPerformed(ActionEvent e) {
               String queryAsString;
               if (!helper.hasPrefix(sparql)) {
                  queryAsString = helper.addPrefixToRequest(sparql);
               } else {
                  queryAsString = sparql;
               }
               helper.saveSPARQL(queryAsString);
            }
         };
         menu.add(new JMenuItem(saveSPARQLAction));
         
         AbstractAction saveResultAction = new AbstractAction("Save Result") {
            @Override
            public void actionPerformed(ActionEvent e) {
               saveResult(resultAsString);
            }
         };
         menu.add(new JMenuItem(saveResultAction));         

         JTextArea area = new JTextArea(40, 40);
         area.setText(resultAsString);
         builder.setResizable(true);
         builder.addVerticalDialogPart(new JScrollPane(area));
         GUIApplication guiAppli = (GUIApplication) app;
         guiAppli.showDialog(builder, MDIDialogType.UNIQUE_INSTANCE);
      }
   }
   
   private void saveResult(String sparql) {
      JFileChooser chooser = new JFileChooser("Save Result");
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      chooser.setFileFilter(conf.txtfilter);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setCurrentDirectory(conf.getDefaultDirectory());
      if (chooser.showSaveDialog(((GUIApplication) app).getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         file = FileUtilities.getCompatibleFile(file, "txt");
         try ( BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sparql);
            writer.flush();
         } catch (IOException e) {
         }
      }
   }   
}
