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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.girod.jgraphml.GraphMLFactory;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.DiagramDefaults;
import org.girod.jgraphml.model.EdgeLabel;
import org.girod.jgraphml.model.GraphMLDiagram;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.jgraphml.model.GraphMLNode;
import org.girod.jgraphml.model.ShapeType;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.OwlDeclaredSchema;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.swing.GUIApplication;

/**
 * The Action that save import dependencies as yEd diagrams.
 *
 * @since 0.8
 */
public class ExportImportGraphAction extends AbstractMDIAction {
   private File file = null;
   private final OwlSchema schema;
   private GraphMLDiagram graph;
   private DiagramDefaults defaults = null;

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param schema the schema
    * @param file the file to open
    */
   public ExportImportGraphAction(MDIApplication app, String desc, String longDesc, OwlSchema schema, File file) {
      super(app, desc);
      this.file = file;
      this.schema = schema;
      this.setDescription(desc, desc);
   }

   /**
    * Perform the export.
    */
   public void export() {
      GraphMLNode rootNode = graph.addNode();
      String namespace = schema.getDefaultNamespace();
      String prefix = schema.getDefaultPrefix();
      if (namespace != null && prefix != null) {
         rootNode.setLabel(prefix + ":" + namespace);
      } else if (namespace != null) {
         rootNode.setLabel(namespace);
      } else if (prefix != null) {
         rootNode.setLabel(prefix + ":");
      } else {
         rootNode.setLabel("Schema");
      }
      rootNode.setFillColor(Color.GREEN);
      Map<String, GraphMLNode> namespaceToNode = new HashMap<>();
      addDependencies(rootNode, namespaceToNode, schema);
   }

   private void addDependencies(GraphMLNode schemaNode, Map<String, GraphMLNode> namespaceToNode, OwlDeclaredSchema theSchema) {
      Iterator<? extends OwlDeclaredSchema> it = theSchema.getDependenciesByNamespace().values().iterator();
      while (it.hasNext()) {
         OwlDeclaredSchema declaredSchema = it.next();
         String namespace = declaredSchema.getNamespace();
         GraphMLNode node;
         if (namespaceToNode.containsKey(namespace)) {
            node = namespaceToNode.get(namespace);
         } else {
            node = graph.addNode();
            node.setType(ShapeType.ROUNDRECTANGLE);
            if (declaredSchema.hasName()) {
               node.setLabel(declaredSchema.getName() + "\n" + namespace);
            } else {
               node.setLabel(namespace);
            }
            namespaceToNode.put(namespace, node);
         }
         GraphMLEdge edge = schemaNode.addEdgeTo(node);
         if (declaredSchema.getPrefix() != null) {
            edge.setLabel(declaredSchema.getPrefix());
         }
         addDependencies(node, namespaceToNode, declaredSchema);
      }
   }

   @Override
   public void run() throws Exception {
      configure();
      export();
      saveDiagram();

      File yEdDirectory = BrowserConfiguration.getInstance().getYedExeDirectory();
      File yEdFile = new File(yEdDirectory, "yEd.exe");
      String[] cmdArray = {yEdFile.getAbsolutePath(), file.getCanonicalPath()};

      try {
         ProcessBuilder pb = new ProcessBuilder(cmdArray);
         pb.directory(yEdDirectory);
         pb.start();
      } catch (IOException e) {
         ((GUIApplication) app).getMessageArea().append("Impossible to launch yed exexutable", "red");
      }
   }

   private void configure() {
      GraphMLFactory factory = GraphMLFactory.getInstance();
      graph = factory.newDiagram();
      defaults = graph.getDefaults();
      defaults.edgeLabelAutoRotate = true;
      defaults.edgeLabelAutoFlip = true;
      defaults.nodeFontSize = 11;
      defaults.edgeFontSize = 11;
      defaults.autosized = true;
      defaults.padWidth = 20;
      defaults.padHeight = 20;
      defaults.edgeLabelDistance = -4f;
      defaults.edgeLabelPosition = EdgeLabel.ParamModel.POSITION_TAIL;
      defaults.arrowSource = Arrows.NONE;
      defaults.arrowTarget = Arrows.STANDARD;
   }

   /**
    * Save the diagram.
    *
    * @throws java.io.IOException if the diagram can not be saved
    */
   protected void saveDiagram() throws IOException {
      if (file != null) {
         GraphMLFactory factory = GraphMLFactory.getInstance();
         factory.saveDiagram(graph, file);
      }
   }
}
