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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

/**
 * A Sparql action, which executes a SPARQL query.
 *
 * @version 0.8
 */
public class ExecuteSPARQLAction extends AbstractMDIAction {
   private final OwlSchema schema;
   private final String sparql;
   private String resultAsString = null;
   private Exception exception = null;

   /**
    * Constructor.
    */
   public ExecuteSPARQLAction(MDIApplication app, OwlSchema schema, String sparql) {
      super(app, "Execute SPARQL");
      this.schema = schema;
      this.sparql = sparql;
      this.setDescription("Execute SPARQL", "Execute SPARQL");
   }

   @Override
   public void run() throws Exception {
      OntModel model = schema.getOntModel();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      boolean addPrefix = conf.addPrefixInSPARQL;
      String queryAsString;
      if (addPrefix) {
         StringBuilder buf = new StringBuilder();
         Map<String, String> prefixMap = schema.getPrefixMap();
         Set<String> alreadyExists = new HashSet<>();
         Iterator<Entry<String, String>> it = prefixMap.entrySet().iterator();
         while (it.hasNext()) {
            Entry<String, String> entry = it.next();
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
         queryAsString = buf.toString() + sparql;
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
         JTextArea area = new JTextArea(40, 40);
         area.setText(resultAsString);
         builder.setResizable(true);
         builder.addVerticalDialogPart(new JScrollPane(area));
         GUIApplication guiAppli = (GUIApplication) app;
         guiAppli.showDialog(builder, MDIDialogType.UNIQUE_INSTANCE);
      }
   }
}
