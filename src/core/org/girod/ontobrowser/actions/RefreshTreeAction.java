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
package org.girod.ontobrowser.actions;

import com.mxgraph.view.mxGraph;
import java.io.File;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.gui.errors.SwingErrorLogger;
import org.girod.ontobrowser.parsers.graph.GraphExtractor;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.bootstrap.swing.SwingFileProperties;

/**
 * The Action that refresh only the tree.
 *
 * @since 0.13
 */
public class RefreshTreeAction extends AbstractUpdateModelAction {
   private int selectedTab = 0;

   /**
    * Constructor.
    *
    * @param app the Application
    * @param prop the file properties
    */
   public RefreshTreeAction(MDIApplication app, SwingFileProperties prop) {
      super(app, "Refresh Tree", "Refresh Tree", prop);
      this.graphPanel = (GraphPanel) prop.getComponent();
      this.diagram = graphPanel.getDiagram();
      this.selectedTab = graphPanel.getSelectedTab();
   }

   @Override
   public void run() throws Exception {
      graphPanel.reset();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      File file = diagram.getFile();

      boolean addThingClass = conf.addThingClass;
      boolean showPackages = conf.showPackages;
      GraphExtractor extractor = new GraphExtractor(file, diagram.getSchema().getOntModel(), addThingClass, showPackages);
      schema = extractor.getGraph();
      diagram.setSchema(schema);
      mxGraph graph = createGraph(schema);
      diagram.setGraph(graph);
      diagram.setKeyToCell(cell4Class);

      if (graphPanel == null) {
         graphPanel = new GraphPanel((GUIApplication) app);
      }
      graphPanel.setDiagram(diagram);
      if (extractor.hasErrors()) {
         SwingErrorLogger logger = new SwingErrorLogger();
         logger.showParserExceptions(extractor.getErrors());
      }
   }

   @Override
   public void endAction() {
      if (diagram == null) {
         return;
      }

      graphPanel.revalidate();
      graphPanel.setSelectedTab(selectedTab);
   }
}
